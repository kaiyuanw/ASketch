package asketch.alloy;

import static asketch.alloy.etc.Constants.ABSTRACT_BO;
import static asketch.alloy.etc.Constants.ABSTRACT_CO;
import static asketch.alloy.etc.Constants.ABSTRACT_LO;
import static asketch.alloy.etc.Constants.ABSTRACT_Q;
import static asketch.alloy.etc.Constants.ABSTRACT_UO;
import static asketch.alloy.etc.Constants.ABSTRACT_UOE;
import static asketch.alloy.etc.Constants.ABSTRACT_UOF;
import static asketch.alloy.etc.Constants.ALL;
import static asketch.alloy.etc.Constants.AND;
import static asketch.alloy.etc.Constants.BO_FUN_NAME;
import static asketch.alloy.etc.Constants.CO_FUN_NAME;
import static asketch.alloy.etc.Constants.EXPR_FUN_NAME;
import static asketch.alloy.etc.Constants.IFF;
import static asketch.alloy.etc.Constants.IMPLIES;
import static asketch.alloy.etc.Constants.LONE;
import static asketch.alloy.etc.Constants.LO_FUN_NAME;
import static asketch.alloy.etc.Constants.NEGATE;
import static asketch.alloy.etc.Constants.NO;
import static asketch.alloy.etc.Constants.ONE;
import static asketch.alloy.etc.Constants.OR;
import static asketch.alloy.etc.Constants.ORIGIN;
import static asketch.alloy.etc.Constants.Q_FUN_NAME;
import static asketch.alloy.etc.Constants.RESULT_BO;
import static asketch.alloy.etc.Constants.RESULT_CO;
import static asketch.alloy.etc.Constants.RESULT_E;
import static asketch.alloy.etc.Constants.RESULT_LO;
import static asketch.alloy.etc.Constants.RESULT_Q;
import static asketch.alloy.etc.Constants.RESULT_UO;
import static asketch.alloy.etc.Constants.RESULT_UOE;
import static asketch.alloy.etc.Constants.RESULT_UOF;
import static asketch.alloy.etc.Constants.SLASH;
import static asketch.alloy.etc.Constants.SOME;
import static asketch.alloy.etc.Constants.UOE_FUN_NAME;
import static asketch.alloy.etc.Constants.UOF_FUN_NAME;
import static asketch.alloy.etc.Constants.UO_FUN_NAME;
import static asketch.alloy.util.AlloyUtil.cardOfRel;
import static asketch.alloy.util.AlloyUtil.connectExprHolesWithSameVarName;
import static asketch.alloy.util.AlloyUtil.createRelationFromParameter;
import static asketch.alloy.util.AlloyUtil.createRelationFromVariable;
import static asketch.alloy.util.AlloyUtil.extractBold;
import static asketch.alloy.util.AlloyUtil.extractHTML;
import static asketch.alloy.util.AlloyUtil.extractNormal;
import static asketch.alloy.util.AlloyUtil.findSigsAndFields;
import static asketch.alloy.util.AlloyUtil.findSubnode;
import static asketch.alloy.util.AlloyUtil.findSubnodes;
import static asketch.alloy.util.AlloyUtil.putNodeToHoleMapIfAbsent;
import static asketch.alloy.util.StringUtil.stringIsOr;
import static asketch.util.StringUtil.afterSubstring;

import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.Relation;
import asketch.alloy.cand.Type;
import asketch.alloy.fragment.BO;
import asketch.alloy.fragment.CO;
import asketch.alloy.fragment.Hole;
import asketch.alloy.fragment.LO;
import asketch.alloy.fragment.Q;
import asketch.alloy.fragment.UO;
import asketch.alloy.fragment.UOE;
import asketch.alloy.fragment.UOF;
import asketch.alloy.util.AlloyProgram;
import edu.mit.csail.sdg.ast.Browsable;
import edu.mit.csail.sdg.ast.ExprBinary;
import edu.mit.csail.sdg.ast.ExprCall;
import edu.mit.csail.sdg.ast.ExprList;
import edu.mit.csail.sdg.ast.ExprUnary;
import edu.mit.csail.sdg.ast.ExprUnary.Op;
import edu.mit.csail.sdg.parser.CompModule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AlloyASTVisitor {

  /**
   * Reconstruct the Alloy predicate with holes from AST.  This method create a meta construct for
   * each hole and update the meta model.
   */
  public static String constructPreds(AlloyProgram alloyProgram, CompModule module) {
    // Meta model
    StringBuilder metaModel = new StringBuilder();
    // Alloy AST node to ASketch hole mapping
    Map<Browsable, Integer> nodeToHoleMap = new HashMap<>();
    // Find all sigs and fields
    List<Relation> sigsAndFields = findSigsAndFields(module);
    // Quick union and find.  The indexes of holes with
    // the same scope will have the same value.
    int[] connectedHoles = connectExprHolesWithSameVarName(alloyProgram.getHoles());
    // We only handle holes in predicate for now.
    Browsable predsNode = findSubnode(module, "pred");
    if (predsNode != null) {
      // Construct nodeToHoleMap
      for (int i = 0; i < predsNode.getSubnodes().size(); i++) {
        Browsable pred = predsNode.getSubnodes().get(i);
        visitASTNode(pred, alloyProgram.getHoles(), new HashSet<>(), sigsAndFields, nodeToHoleMap);
      }
      // Build meta Alloy model
      for (int i = 0; i < predsNode.getSubnodes().size(); i++) {
        Browsable pred = predsNode.getSubnodes().get(i);
        Deque<Browsable> deque = new LinkedList<>();
        deque.offer(pred);
        while (!deque.isEmpty()) {
          Browsable node = deque.poll();
          if (nodeToHoleMap.containsKey(node)) {
            // Generate predicate for quantifiers only
            int holdId = nodeToHoleMap.get(node);
            Hole hole = alloyProgram.getHoles().get(holdId);
            if (hole instanceof Q) {
              List<Browsable> qVarNodes = findSubnodes(node, "var");
              String varDecl = String.join(", ", qVarNodes.stream()
                  .map(n -> constructHierarchy(n, nodeToHoleMap, alloyProgram, sigsAndFields,
                      connectedHoles, deque))
                  .collect(Collectors.toList()));
              Browsable qBodyNode = findSubnode(node, "body");
              metaModel.append(generateQPredDecl(alloyProgram, holdId,
                  varDecl + " | " + constructHierarchy(qBodyNode, nodeToHoleMap, alloyProgram,
                      sigsAndFields, connectedHoles, deque)));
              metaModel.append("one sig " + RESULT_Q + holdId + " in " + ABSTRACT_Q + " {}\n");
            } else if (hole instanceof CO) {
              metaModel.append("one sig " + RESULT_CO + holdId + " in " + ABSTRACT_CO + " {}\n");
            } else if (hole instanceof UO) {
              metaModel.append("one sig " + RESULT_UO + holdId + " in " + ABSTRACT_UO + " {}\n");
            } else if (hole instanceof BO) {
              metaModel.append("one sig " + RESULT_BO + holdId + " in " + ABSTRACT_BO + " {}\n");
            } else if (hole instanceof UOE) {
              metaModel.append("one sig " + RESULT_UOE + holdId + " in " + ABSTRACT_UOE + " {}\n");
            } else if (hole instanceof LO) {
              // Go one level deeper if the current node is a NOOP unary expression.
              if (node instanceof ExprUnary) {
                ExprUnary exprUnary = (ExprUnary) node;
                if (exprUnary.op == Op.NOOP) {
                  node = exprUnary.sub;
                }
              }
              if (node instanceof ExprList) { // && or ||
                ExprList exprList = (ExprList) node;
                List<String> commons = exprList.args.stream().map(
                    arg -> constructHierarchy(arg, nodeToHoleMap, alloyProgram, sigsAndFields,
                        connectedHoles, deque)).collect(Collectors.toList());
                metaModel.append(generateLOPredDecl(alloyProgram, holdId, commons));
              } else { // <=> or =>
                ExprBinary exprBinary = (ExprBinary) node;
                metaModel.append(generateLOPredDecl(alloyProgram, holdId, Arrays.asList(
                    constructHierarchy(exprBinary.left, nodeToHoleMap, alloyProgram, sigsAndFields,
                        connectedHoles, deque),
                    constructHierarchy(exprBinary.right, nodeToHoleMap, alloyProgram, sigsAndFields,
                        connectedHoles, deque))));
              }
              metaModel.append("one sig " + RESULT_LO + holdId + " in " + ABSTRACT_LO + " {}\n");
            } else if (hole instanceof UOF) { // !
              ExprUnary exprUnary = (ExprUnary) node;
              metaModel.append(generateUOFPredDecl(alloyProgram, holdId,
                  constructHierarchy(exprUnary.sub, nodeToHoleMap, alloyProgram, sigsAndFields,
                      connectedHoles, deque)));
              metaModel.append("one sig " + RESULT_UOF + holdId + " in " + ABSTRACT_UOF + " {}\n");
            } else {
              // TODO(kaiyuan): Implement other types of holes
            }
            continue;
          }
          metaModel.append(
              constructHierarchy(node, nodeToHoleMap, alloyProgram, sigsAndFields, connectedHoles,
                  deque));
        }
      }
    }
    return metaModel.toString();
  }

  /**
   * DFS the tree and map each hole to an Alloy AST node.
   */
  private static void visitASTNode(Browsable node, List<Hole> holes, Set<Hole> assignedHoles,
      List<Relation> relations, Map<Browsable, Integer> nodeToHoleMap) {
    String boldPart = extractBold(node);
    if (boldPart != null) {
      if (stringIsOr(boldPart, "pred")) {
        List<Browsable> parameters = findSubnodes(node, "parameter");
        parameters.forEach(
            parameter -> visitASTNode(parameter, holes, assignedHoles, relations, nodeToHoleMap));
        List<Relation> newRelations = new ArrayList<>(relations);
        parameters
            .forEach(parameter -> newRelations.add(createRelationFromParameter(parameter)));
        Browsable body = findSubnode(node, "body");
        visitASTNode(body, holes, assignedHoles, newRelations, nodeToHoleMap);
        return;
      }
      if (stringIsOr(boldPart, "fun")) {
        List<Browsable> parameters = findSubnodes(node, "parameter");
        parameters.forEach(
            parameter -> visitASTNode(parameter, holes, assignedHoles, relations, nodeToHoleMap));
        Browsable returnType = findSubnode(node, "return type");
        visitASTNode(returnType, holes, assignedHoles, relations, nodeToHoleMap);
        List<Relation> newRelations = new ArrayList<>(relations);
        parameters
            .forEach(parameter -> newRelations.add(createRelationFromParameter(parameter)));
        Browsable body = findSubnode(node, "body");
        visitASTNode(body, holes, assignedHoles, newRelations, nodeToHoleMap);
        return;
      }
      if (stringIsOr(boldPart, "fact", "assert")) {
        Browsable body = node.getSubnodes().get(0);
        visitASTNode(body, holes, assignedHoles, relations, nodeToHoleMap);
        return;
      }
      if (stringIsOr(boldPart, "sig", "field")) { // Does not handle call yet
        String relationName = afterSubstring(extractNormal(node), SLASH, true);
        putNodeToHoleMapIfAbsent(node, relationName, holes, assignedHoles, relations,
            nodeToHoleMap);
        return;
      }
      if (boldPart.contains("AND")) {
        putNodeToHoleMapIfAbsent(node, "&&", holes, assignedHoles, relations, nodeToHoleMap);
        node.getSubnodes()
            .forEach(
                subNode -> visitASTNode(subNode, holes, assignedHoles, relations, nodeToHoleMap));
        return;
      }
      if (boldPart.contains("OR")) {
        putNodeToHoleMapIfAbsent(node, "||", holes, assignedHoles, relations, nodeToHoleMap);
        node.getSubnodes()
            .forEach(
                subNode -> visitASTNode(subNode, holes, assignedHoles, relations, nodeToHoleMap));
        return;
      }
      if (stringIsOr(boldPart, "no", "lone", "one", "some", "all")) { // Quantifier node
        putNodeToHoleMapIfAbsent(node, boldPart, holes, assignedHoles, relations, nodeToHoleMap);
        List<Browsable> vars = findSubnodes(node, "var");
        vars
            .forEach(var -> visitASTNode(var, holes, assignedHoles, relations, nodeToHoleMap));
        List<Relation> newRelations = new ArrayList<>(relations);
        vars.forEach(var -> newRelations.add(createRelationFromVariable(var)));
        Browsable body = findSubnode(node, "body");
        visitASTNode(body, holes, assignedHoles, newRelations, nodeToHoleMap);
        return;
      }
      if (stringIsOr(boldPart, "var")) { // Var node
        Browsable child = node.getSubnodes().get(0);
        visitASTNode(child, holes, assignedHoles, relations, nodeToHoleMap);
        return;
      }
      if (stringIsOr(boldPart, "body", "return type")) {
        Browsable child = node.getSubnodes().get(0);
        visitASTNode(child, holes, assignedHoles, relations, nodeToHoleMap);
        return;
      }
      if (stringIsOr(boldPart, "variable")) {
        String varName = extractNormal(node);
        putNodeToHoleMapIfAbsent(node, varName, holes, assignedHoles, relations, nodeToHoleMap);
        return;
      }
      if (stringIsOr(boldPart, "+", "&amp;", "-", ".", "-&gt;", "&lt;=&gt;", "=&gt;", "=", "!=",
          "in",
          "!in")) {
        if (boldPart.equals("&amp;")) {
          putNodeToHoleMapIfAbsent(node, "&", holes, assignedHoles, relations, nodeToHoleMap);
        } else if (boldPart.equals("-&gt;")) {
          putNodeToHoleMapIfAbsent(node, "->", holes, assignedHoles, relations, nodeToHoleMap);
        } else if (boldPart.equals("&lt;=&gt;")) {
          putNodeToHoleMapIfAbsent(node, "<=>", holes, assignedHoles, relations, nodeToHoleMap);
        } else if (boldPart.equals("=&gt;")) {
          putNodeToHoleMapIfAbsent(node, "=>", holes, assignedHoles, relations, nodeToHoleMap);
        } else {
          putNodeToHoleMapIfAbsent(node, boldPart, holes, assignedHoles, relations, nodeToHoleMap);
        }
        Browsable left = node.getSubnodes().get(0);
        Browsable right = node.getSubnodes().get(1);
        visitASTNode(left, holes, assignedHoles, relations, nodeToHoleMap);
        visitASTNode(right, holes, assignedHoles, relations, nodeToHoleMap);
        return;
      }
    }
    String normalPart = extractNormal(node);
    if (normalPart != null) {
      String card = cardOfRel(normalPart);
      if (card != null) {
        Browsable child = node.getSubnodes().get(0);
        visitASTNode(child, holes, assignedHoles, relations, nodeToHoleMap);
        return;
      }
      if (stringIsOr(normalPart, "~", "*", "^", "!")) {
        putNodeToHoleMapIfAbsent(node, normalPart, holes, assignedHoles, relations, nodeToHoleMap);
        Browsable operand = node.getSubnodes().get(0);
        visitASTNode(operand, holes, assignedHoles, relations, nodeToHoleMap);
        return;
      }
      if (stringIsOr(normalPart, "no", "lone", "one", "some")) {
        putNodeToHoleMapIfAbsent(node, normalPart, holes, assignedHoles, relations, nodeToHoleMap);
        Browsable operand = node.getSubnodes().get(0);
        visitASTNode(operand, holes, assignedHoles, relations, nodeToHoleMap);
        return;
      }
    }
  }

  /**
   * Recursively visit each node and if the node is associated with some hole, then generate the
   * pred/fun invocation and return the string representation at the root node.
   */
  public static String constructHierarchy(Browsable browsable,
      Map<Browsable, Integer> nodeToHoleMap, AlloyProgram alloyProgram,
      List<Relation> sigsAndFields, int[] connectedHoles, Deque<Browsable> deque) {
    String boldPart = extractBold(browsable);
    if (boldPart != null) {
      // Only handle pred node, ignore fun, fact and assert for now.
      if (stringIsOr(boldPart, "pred")) {
        String predName = afterSubstring(
            extractHTML(browsable.getHTML(), Pattern.compile("</b> (.*)")), SLASH, true);
        List<Browsable> parameterNodes = findSubnodes(browsable, "parameter");
        // Parameters should not contain holes and the type of original
        // parameter should not be changed to the new type.  Here we
        // simply take advantage of the getBrowsableString() method.
        String paraDecl = String.join(", ", parameterNodes.stream()
            .map(para -> afterSubstring(extractNormal(para), SLASH, true) + ": "
                + getBrowsableString(para.getSubnodes().get(0)))
            .collect(Collectors.toList()));
        String prefix = paraDecl.equals("") ? "" : ", ";
        Browsable body = findSubnode(browsable, "body");
        return "pred " + predName + "(" + paraDecl + prefix + generateParameterDeclFromRelations(
            sigsAndFields) + ") {\n" + constructHierarchy(body, nodeToHoleMap, alloyProgram,
            sigsAndFields, connectedHoles, deque) + "\n}\n";
      }
      if (stringIsOr(boldPart, "sig", "field")) { // Does not handle call yet
        if (nodeToHoleMap.containsKey(browsable)) {
          return generateFunOrPredCall(EXPR_FUN_NAME, RESULT_E, nodeToHoleMap.get(browsable),
              connectedHoles, alloyProgram);
        }
        String relationName = afterSubstring(extractNormal(browsable), SLASH, true);
        if (stringIsOr(boldPart, "sig")) {
          relationName += "s";
        }
        return relationName;
      }
      if (boldPart.contains("AND")) {
        if (nodeToHoleMap.containsKey(browsable)) {
          deque.offer(browsable);
          return generateFunOrPredCall(LO_FUN_NAME, RESULT_LO, nodeToHoleMap.get(browsable),
              connectedHoles, alloyProgram);
        }
        return "(" + String.join(" && ", browsable.getSubnodes().stream()
            .map(node -> constructHierarchy(node, nodeToHoleMap, alloyProgram, sigsAndFields,
                connectedHoles, deque))
            .collect(Collectors.toList())) + ")";
      }
      if (boldPart.contains("OR")) {
        if (nodeToHoleMap.containsKey(browsable)) {
          deque.offer(browsable);
          return generateFunOrPredCall(LO_FUN_NAME, RESULT_LO, nodeToHoleMap.get(browsable),
              connectedHoles, alloyProgram);
        }
        return "(" + String.join(" || ", browsable.getSubnodes().stream()
            .map(node -> constructHierarchy(node, nodeToHoleMap, alloyProgram, sigsAndFields,
                connectedHoles, deque))
            .collect(Collectors.toList())) + ")";
      }
      if (stringIsOr(boldPart, "no", "lone", "one", "some", "all")) { // Quantifier node
        List<Browsable> qVarNodes = findSubnodes(browsable, "var");
        String varDecl = String.join(", ", qVarNodes.stream()
            .map(node -> constructHierarchy(node, nodeToHoleMap, alloyProgram, sigsAndFields,
                connectedHoles, deque))
            .collect(Collectors.toList()));
        if (nodeToHoleMap.containsKey(browsable)) {
          deque.offer(browsable);
          return generateFunOrPredCall(Q_FUN_NAME, RESULT_Q, nodeToHoleMap.get(browsable),
              connectedHoles, alloyProgram);
        }
        Browsable qBody = findSubnode(browsable, "body");
        return boldPart + " " + varDecl + " | " + constructHierarchy(qBody, nodeToHoleMap,
            alloyProgram, sigsAndFields, connectedHoles, deque);
      }
      if (stringIsOr(boldPart, "var")) { // Var node
        // Could be one_of or ->, ...
        String varName = extractNormal(browsable).trim();
        Browsable child = browsable.getSubnodes().get(0);
        return varName + ": " + constructHierarchy(child, nodeToHoleMap, alloyProgram,
            sigsAndFields, connectedHoles, deque);
      }
      if (stringIsOr(boldPart, "body", "return type")) {
        // We don't see body node has more than 1 child node.
        Browsable child = browsable.getSubnodes().get(0);
        return constructHierarchy(child, nodeToHoleMap, alloyProgram, sigsAndFields, connectedHoles,
            deque);
      }
      if (stringIsOr(boldPart, "variable")) {
        return extractNormal(browsable);
      }
      if (stringIsOr(boldPart, "&lt;=&gt;", "=&gt;")) {
        if (nodeToHoleMap.containsKey(browsable)) {
          deque.offer(browsable);
          return generateFunOrPredCall(LO_FUN_NAME, RESULT_LO, nodeToHoleMap.get(browsable),
              connectedHoles, alloyProgram);
        }
        Browsable leftOperand = browsable.getSubnodes().get(0);
        Browsable rightOperand = browsable.getSubnodes().get(1);
        String op = boldPart.equals("&lt;=&gt;") ? "<=>" : "=>";
        return "(" + constructHierarchy(leftOperand, nodeToHoleMap, alloyProgram, sigsAndFields,
            connectedHoles, deque) + op + constructHierarchy(rightOperand, nodeToHoleMap,
            alloyProgram, sigsAndFields, connectedHoles, deque) + ")";
      }
      if (stringIsOr(boldPart, "+", "&amp;", "-")) {
        Browsable leftOperand = browsable.getSubnodes().get(0);
        Browsable rightOperand = browsable.getSubnodes().get(1);
        if (nodeToHoleMap.containsKey(browsable)) {
          deque.offer(browsable);
          return generateFunOrPredCall(BO_FUN_NAME, RESULT_BO, nodeToHoleMap.get(browsable),
              connectedHoles,
              constructHierarchy(leftOperand, nodeToHoleMap, alloyProgram, sigsAndFields,
                  connectedHoles, deque),
              constructHierarchy(rightOperand, nodeToHoleMap, alloyProgram, sigsAndFields,
                  connectedHoles, deque));
        }
        String op = boldPart.equals("&amp;") ? "&" : boldPart;
        return "(" + constructHierarchy(leftOperand, nodeToHoleMap, alloyProgram, sigsAndFields,
            connectedHoles, deque) + op + constructHierarchy(rightOperand, nodeToHoleMap,
            alloyProgram, sigsAndFields, connectedHoles, deque) + ")";
      }
      if (stringIsOr(boldPart, ".", "-&gt;")) {
        Browsable leftOperand = browsable.getSubnodes().get(0);
        Browsable rightOperand = browsable.getSubnodes().get(1);
        if (nodeToHoleMap.containsKey(browsable)) {
          deque.offer(browsable);
          return generateFunOrPredCall(EXPR_FUN_NAME, RESULT_E, nodeToHoleMap.get(browsable),
              connectedHoles, alloyProgram);
        }
        String op = boldPart.equals("-&gt;") ? "->" : boldPart;
        return "(" + constructHierarchy(leftOperand, nodeToHoleMap, alloyProgram, sigsAndFields,
            connectedHoles, deque) + op + constructHierarchy(rightOperand,
            nodeToHoleMap, alloyProgram, sigsAndFields, connectedHoles, deque) + ")";
      }
      if (stringIsOr(boldPart, "=", "!=", "in", "!in")) {
        Browsable leftOperand = browsable.getSubnodes().get(0);
        Browsable rightOperand = browsable.getSubnodes().get(1);
        if (nodeToHoleMap.containsKey(browsable)) {
          deque.offer(browsable);
          return generateFunOrPredCall(CO_FUN_NAME, RESULT_CO, nodeToHoleMap.get(browsable),
              connectedHoles,
              constructHierarchy(leftOperand, nodeToHoleMap, alloyProgram, sigsAndFields,
                  connectedHoles, deque),
              constructHierarchy(rightOperand, nodeToHoleMap, alloyProgram, sigsAndFields,
                  connectedHoles, deque));
        }
        String space = stringIsOr(boldPart, "in", "!in") ? " " : "";
        return "(" + constructHierarchy(leftOperand, nodeToHoleMap, alloyProgram, sigsAndFields,
            connectedHoles, deque) + space + boldPart + space + constructHierarchy(rightOperand,
            nodeToHoleMap, alloyProgram, sigsAndFields, connectedHoles, deque) + ")";
      }
      if (stringIsOr(boldPart, "call")) {
        ExprCall call = (ExprCall) browsable;
        String originalArgs = String.join(",", call.args.stream()
            .map(node -> constructHierarchy(node, nodeToHoleMap, alloyProgram, sigsAndFields,
                connectedHoles, deque))
            .collect(Collectors.toList()));
        String prefix = originalArgs.isEmpty() ? "" : ", ";
        return "(" + afterSubstring(call.fun.label, SLASH, true) + "["
            + originalArgs + prefix + generateArgInvocationFromRelations(sigsAndFields) +"])";
      }
    }
    String normalPart = extractNormal(browsable);
    if (normalPart != null) {
      String card = cardOfRel(normalPart);
      if (card != null) {
        Browsable child = browsable.getSubnodes().get(0);
        return card + " " + constructHierarchy(child, nodeToHoleMap, alloyProgram, sigsAndFields,
            connectedHoles, deque);
      }
      if (stringIsOr(normalPart, "~", "*", "^")) {
        Browsable operand = browsable.getSubnodes().get(0);
        if (nodeToHoleMap.containsKey(browsable)) {
          deque.offer(browsable);
          return generateFunOrPredCall(UOE_FUN_NAME, RESULT_UOE, nodeToHoleMap.get(browsable),
              connectedHoles,
              constructHierarchy(operand, nodeToHoleMap, alloyProgram, sigsAndFields,
                  connectedHoles, deque));
        }
        return "(" + normalPart + constructHierarchy(operand, nodeToHoleMap, alloyProgram,
            sigsAndFields, connectedHoles, deque) + ")";
      }
      if (stringIsOr(normalPart, "!")) {
        Browsable operand = browsable.getSubnodes().get(0);
        if (nodeToHoleMap.containsKey(browsable)) {
          deque.offer(browsable);
          return generateFunOrPredCall(UOF_FUN_NAME, RESULT_UOF, nodeToHoleMap.get(browsable),
              connectedHoles, alloyProgram);
        }
        return "(" + normalPart + constructHierarchy(operand, nodeToHoleMap, alloyProgram,
            sigsAndFields, connectedHoles, deque) + ")";
      }
      if (stringIsOr(normalPart, "no", "lone", "one", "some")) {
        Browsable operand = browsable.getSubnodes().get(0);
        if (nodeToHoleMap.containsKey(browsable)) {
          deque.offer(browsable);
          return generateFunOrPredCall(UO_FUN_NAME, RESULT_UO, nodeToHoleMap.get(browsable),
              connectedHoles,
              constructHierarchy(operand, nodeToHoleMap, alloyProgram, sigsAndFields,
                  connectedHoles, deque));
        }
        return normalPart + " " + constructHierarchy(operand, nodeToHoleMap, alloyProgram,
            sigsAndFields, connectedHoles, deque);
      }
    }
    return null;
  }

  /**
   * This method traverse the Alloy AST from the given node and print the string representation from
   * the node.  This can be used to construct the predicate directly from AST.
   */
  public static String getBrowsableString(Browsable browsable) {
    String boldPart = extractBold(browsable);
    if (boldPart != null) {
      if (stringIsOr(boldPart, "pred")) {
        String predName = afterSubstring(
            extractHTML(browsable.getHTML(), Pattern.compile("</b> (.*)")), SLASH, true);
        List<Browsable> parameterNodes = findSubnodes(browsable, "parameter");
        String paraDecl = String.join(", ", parameterNodes.stream()
            .map(para -> afterSubstring(extractNormal(para), SLASH, true) + ": "
                + getBrowsableString(para.getSubnodes().get(0)))
            .collect(Collectors.toList()));
        Browsable bodyNode = findSubnode(browsable, "body");
        return "pred " + predName + "(" + paraDecl + ") {\n" + getBrowsableString(bodyNode)
            + "\n}\n";
      }
      if (stringIsOr(boldPart, "fun")) {
        String funName = extractNormal(browsable);
        List<Browsable> parameterNodes = findSubnodes(browsable, "parameter");
        String paraDecl = String.join(", ", parameterNodes.stream()
            .map(para -> afterSubstring(extractNormal(para), SLASH, true) + ": "
                + getBrowsableString(para.getSubnodes().get(0)))
            .collect(Collectors.toList()));
        Browsable returnNode = findSubnode(browsable, "return type");
        Browsable bodyNode = findSubnode(browsable, "body");
        return "fun " + funName + "[" + paraDecl + "]:" + getBrowsableString(returnNode) + " {\n"
            + getBrowsableString(bodyNode) + "\n}\n";
      }
      if (stringIsOr(boldPart, "fact")) {
        return boldPart + " {\n" + getBrowsableString(browsable.getSubnodes().get(0)) + "\n}\n";
      }
      if (stringIsOr(boldPart, "assert")) {
        return "assert " + extractNormal(browsable) + " {\n" + getBrowsableString(
            browsable.getSubnodes().get(0)) + "\n}\n";
      }
      if (stringIsOr(boldPart, "sig", "field")) { // Does not handle call yet
        String relationName = extractNormal(browsable);
        return afterSubstring(relationName, SLASH, true);
      }
      if (boldPart.contains("AND")) {
        return "(" + String.join(" && ", browsable.getSubnodes().stream()
            .map(AlloyASTVisitor::getBrowsableString)
            .collect(Collectors.toList())) + ")";
      }
      if (boldPart.contains("OR")) {
        return "(" + String.join(" || ", browsable.getSubnodes().stream()
            .map(AlloyASTVisitor::getBrowsableString)
            .collect(Collectors.toList())) + ")";
      }
      if (stringIsOr(boldPart, "no", "lone", "one", "some", "all")) { // Quantifier node
        List<Browsable> qVarNodes = findSubnodes(browsable, "var");
        String varDecl = String.join(", ", qVarNodes.stream()
            .map(AlloyASTVisitor::getBrowsableString)
            .collect(Collectors.toList()));
        Browsable qBodyNode = findSubnode(browsable, "body");
        return boldPart + " " + varDecl + " | " + getBrowsableString(qBodyNode);
      }
      if (stringIsOr(boldPart, "var")) { // Var node
        // Could be one_of or ->, ...
        String varName = extractNormal(browsable).trim();
        Browsable childNode = browsable.getSubnodes().get(0);
        return varName + ": " + getBrowsableString(childNode);
      }
      if (stringIsOr(boldPart, "body", "return type")) {
        // We don't see body node has more than 1 child node.
        Browsable childNode = browsable.getSubnodes().get(0);
        return getBrowsableString(childNode);
      }
      if (stringIsOr(boldPart, "variable")) {
        return extractNormal(browsable);
      }
      if (stringIsOr(boldPart, "&lt;=&gt;", "=&gt;")) {
        Browsable leftOperand = browsable.getSubnodes().get(0);
        Browsable rightOperand = browsable.getSubnodes().get(1);
        String op = boldPart.equals("&lt;=&gt;") ? "<=>" : "=>";
        return "(" + getBrowsableString(leftOperand) + op + getBrowsableString(rightOperand) + ")";
      }
      if (stringIsOr(boldPart, "+", "&amp;", "-", ".", "-&gt;", "=", "!=", "in", "!in")) {
        Browsable leftOperand = browsable.getSubnodes().get(0);
        Browsable rightOperand = browsable.getSubnodes().get(1);
        String space = stringIsOr(boldPart, "in", "!in") ? " " : "";
        String op = boldPart.equals("&amp;") ? "&" : boldPart;
        return "(" + getBrowsableString(leftOperand) + space + op + space + getBrowsableString(
            rightOperand) + ")";
      }
      if (stringIsOr(boldPart, "call")) {
        ExprCall call = (ExprCall) browsable;
        return "(" + afterSubstring(call.fun.label, SLASH, true) + "["
            + String.join(",", call.args.stream()
            .map(AlloyASTVisitor::getBrowsableString)
            .collect(Collectors.toList())) + "])";
      }
    }
    String normalPart = extractNormal(browsable);
    if (normalPart != null) {
      String card = cardOfRel(normalPart);
      if (card != null) {
        Browsable childNode = browsable.getSubnodes().get(0);
        return card + " " + getBrowsableString(childNode);
      }
      if (stringIsOr(normalPart, "~", "*", "^", "!")) {
        Browsable operand = browsable.getSubnodes().get(0);
        return "(" + normalPart + getBrowsableString(operand) + ")";
      }
      if (stringIsOr(normalPart, "no", "lone", "one", "some")) {
        Browsable operand = browsable.getSubnodes().get(0);
        return normalPart + " " + getBrowsableString(operand);
      }
    }
    return null;
  }

  private static String generateParameterDeclFromRelations(List<Relation> relations) {
    StringBuilder newParas = new StringBuilder();
    String prefix = "";
    for (Relation relation : relations) {
      // Ignore relations other than sigs
      List<String> types = relation.getTypes().stream()
          .map(Type::getGenType).collect(Collectors.toList());
      if (types.size() == 1) {
        String relationName = relation.getValue();
        if (relation.getValue().equals(types.get(0))) { // Signature
          relationName = relationName + "s";
        }
        String relationType = types.get(0);
        String relationCard = relation.getCards().get(0);
        newParas.append(prefix).append(relationName).append(": ").append(relationCard).append(" ")
            .append(relationType);
        prefix = ", ";
      } else { // Fields
        String fieldName = relation.getValue();
        String fieldType = String.join("->", types);
        newParas.append(prefix).append(fieldName).append(": ").append(fieldType);
        prefix = ", ";
      }
    }
    return newParas.toString();
  }

  private static String generateArgInvocationFromRelations(List<Relation> relations) {
    StringBuilder newArgs = new StringBuilder();
    String prefix = "";
    for (Relation relation : relations) {
      String relationName = relation.getValue();
      if (relation.getTypes().size() == 1 && relationName
          .equals(relation.getTypes().get(0).getGenType())) {
        relationName = relationName + "s";
      }
      newArgs.append(prefix).append(relationName);
      prefix = ", ";
    }
    return newArgs.toString();
  }

  /**
   * This function is only invoked for Q, LO and E.
   */
  private static String generateFunOrPredCall(String funOrPredName, String firstArgName, int holeId,
      int[] connectedHoles, AlloyProgram alloyProgram) {
    // Find basic relations
    StringBuilder call = new StringBuilder();
    if (connectedHoles[holeId] == -1) { // Deprecated.
      // Quantifier predicate may not share the same name.
      if (funOrPredName.equals(Q_FUN_NAME) || funOrPredName.equals(LO_FUN_NAME)
          || funOrPredName.equals(UOF_FUN_NAME)) {
        call.append(funOrPredName).append(holeId);
      } else {
        call.append(funOrPredName);
      }
    } else {
      // Create unique function name.
      if (funOrPredName.equals(Q_FUN_NAME) || funOrPredName.equals(LO_FUN_NAME)
          || funOrPredName.equals(UOF_FUN_NAME)) {
        call.append(funOrPredName).append(holeId);
      } else {
        call.append(funOrPredName).append(connectedHoles[holeId]);
      }
    }
    // Create argument list; the first argument is the
    // special token which represents an expression.
    call.append("[").append(firstArgName).append(holeId);
    // Create the rest of arguments; the order follows
    // the order sigs and fields are declared.
    for (Relation basicRelation : alloyProgram.getHoles().get(holeId).getPrimaryRelations()) {
      String relationName = basicRelation.getValue();
      if (basicRelation.getTypes().size() == 1 && relationName
          .equals(basicRelation.getTypes().get(0).getGenType())) {
        relationName = relationName + "s";
      }
      call.append(", ").append(relationName);
      // Here we don't append s after signatures because later we will
      // replace all sigs with new names.
    }
    call.append("]");
    return "(" + call.toString() + ")";
  }

  private static String generateFunOrPredCall(String funOrPredName, String firstArgName, int holeId,
      int[] connectedHoles, String leftOperand, String rightOperand) {
    // Find basic relations
    StringBuilder call = new StringBuilder();
    call.append(funOrPredName).append(connectedHoles[holeId]);
    // Create argument list; the first argument is the
    // special token which represents an expression.
    call.append("[")
        .append(firstArgName).append(holeId).append(", ")
        .append(leftOperand).append(", ")
        .append(rightOperand).append("]");
    return "(" + call.toString() + ")";
  }

  private static String generateFunOrPredCall(String funOrPredName, String firstArgName, int holeId,
      int[] connectedHoles, String operand) {
    // Find basic relations
    StringBuilder call = new StringBuilder();
    call.append(funOrPredName).append(connectedHoles[holeId]);
    // Create argument list; the first argument is the
    // special token which represents an expression.
    call.append("[")
        .append(firstArgName).append(holeId).append(", ")
        .append(operand).append("]");
    return "(" + call.toString() + ")";
  }

  private static String generateQPredDecl(AlloyProgram alloyProgram, int holeId, String common) {
    StringBuilder predDecl = new StringBuilder();
    Hole qHole = alloyProgram.getHoles().get(holeId);
    predDecl.append("pred " + Q_FUN_NAME + holeId + "(h: " + ABSTRACT_Q + ", "
        + generateParameterDeclFromRelations(qHole.getPrimaryRelations()) + ") {\n");
    Set<String> cands = qHole.getCands()
        .stream().map(Candidate::getValue).collect(Collectors.toSet());
    if (cands.contains(ALL)) {
      predDecl.append("  h = " + ABSTRACT_Q + "_All => " + ALL + " " + common + "\n");
    }
    if (cands.contains(NO)) {
      predDecl.append("  h = " + ABSTRACT_Q + "_No => " + NO + " " + common + "\n");
    }
    if (cands.contains(SOME)) {
      predDecl.append("  h = " + ABSTRACT_Q + "_Some => " + SOME + " " + common + "\n");
    }
    if (cands.contains(LONE)) {
      predDecl.append("  h = " + ABSTRACT_Q + "_Lone => " + LONE + " " + common + "\n");
    }
    if (cands.contains(ONE)) {
      predDecl.append("  h = " + ABSTRACT_Q + "_One => " + ONE + " " + common + "\n");
    }
    predDecl.append("}\n");
    return predDecl.toString();
  }

  /**
   * Currently does not support multiple LOs like "formula1 \LO\ formula2 \LO\ formula3", because of
   * the way Alloy parse && and ||.
   */
  private static String generateLOPredDecl(AlloyProgram alloyProgram, int holeId,
      List<String> commons) {
    StringBuilder predDecl = new StringBuilder();
    Hole loHole = alloyProgram.getHoles().get(holeId);
    predDecl.append("pred " + LO_FUN_NAME + holeId + "(h: " + ABSTRACT_LO + ", "
        + generateParameterDeclFromRelations(loHole.getPrimaryRelations()) + ") {\n");
    Set<String> cands = loHole.getCands()
        .stream().map(Candidate::getValue).collect(Collectors.toSet());
    if (cands.contains(AND)) {
      predDecl.append(
          "  h = " + ABSTRACT_LO + "_And => " + String.join(" " + AND + " ", commons) + "\n");
    }
    if (cands.contains(OR)) {
      predDecl
          .append("  h = " + ABSTRACT_LO + "_Or => " + String.join(" " + OR + " ", commons) + "\n");
    }
    if (cands.contains(IMPLIES)) {
      predDecl.append(
          "  h = " + ABSTRACT_LO + "_Imply => " + String.join(" " + IMPLIES + " ", commons) + "\n");
    }
    if (cands.contains(IFF)) {
      predDecl.append(
          "  h = " + ABSTRACT_LO + "_BiImply => " + String.join(" " + IFF + " ", commons) + "\n");
    }
    predDecl.append("}\n");
    return predDecl.toString();
  }

  private static String generateUOFPredDecl(AlloyProgram alloyProgram, int holeId, String common) {
    StringBuilder predDecl = new StringBuilder();
    Hole qHole = alloyProgram.getHoles().get(holeId);
    predDecl.append("pred " + UOF_FUN_NAME + holeId + "(h: " + ABSTRACT_UOF + ", "
        + generateParameterDeclFromRelations(qHole.getPrimaryRelations()) + ") {\n");
    Set<String> cands = qHole.getCands()
        .stream().map(Candidate::getValue).collect(Collectors.toSet());
    if (cands.contains(ORIGIN)) {
      predDecl.append("  h = " + ABSTRACT_UOF + "_Origin => " + ORIGIN + common + "\n");
    }
    if (cands.contains(NEGATE)) {
      predDecl.append("  h = " + ABSTRACT_UOF + "_Negate => " + NEGATE + common + "\n");
    }
    predDecl.append("}\n");
    return predDecl.toString();
  }
}
