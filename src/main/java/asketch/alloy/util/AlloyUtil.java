package asketch.alloy.util;

import static asketch.alloy.etc.Constants.CROSS_PRODUCT;
import static asketch.alloy.etc.Constants.SLASH;
import static asketch.alloy.etc.Operators.ONE;
import static asketch.alloy.etc.Operators.SET;
import static asketch.util.StringUtil.afterSubstring;

import asketch.alloy.cand.Relation;
import asketch.alloy.cand.Type;
import asketch.alloy.etc.ColSpan;
import asketch.alloy.etc.Constants;
import asketch.alloy.etc.RowSpan;
import asketch.alloy.fragment.E;
import asketch.alloy.fragment.Hole;
import asketch.etc.Names;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.Pos;
import edu.mit.csail.sdg.ast.Browsable;
import edu.mit.csail.sdg.ast.Sig;
import edu.mit.csail.sdg.ast.Sig.PrimSig;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.parser.CompUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlloyUtil {

  public static List<Relation> findSigsAndFields(CompModule module) {
    return findSigsAndFields(module, null);
  }

  public static List<Relation> findSigsAndFields(CompModule module,
      Map<String, String> inheritanceMap) {
    List<Relation> sigsAndRelations = new ArrayList<>();
    Browsable sigParent = findSubnode(module, "sig");
    List<? extends Browsable> sigs = sigParent.getSubnodes();
    for (int i = 0; i < sigs.size(); i++) {
      Sig sig = (Sig) sigs.get(i);
      String sigName = afterSubstring(sig.label, Names.SLASH, true);
      // Construct inheritance map.  The type hierarchy goes up to univ.
      if (inheritanceMap != null) {
        boolean isSubsig = sig.isSubsig != null;
        if (isSubsig) { // S extends P
          inheritanceMap
              .put(sigName, afterSubstring(((Sig.PrimSig) sig).parent.label, Names.SLASH, true));
        } else { // S in P
          inheritanceMap.put(sigName,
              afterSubstring(((Sig.SubsetSig) sig).parents.get(0).label, Names.SLASH, true));
        }
      }
      if (sig instanceof PrimSig) {
        // Sig name is also it's type.  We add sigs as relations
        String sigCard = cardOfSig(sig);
        sigsAndRelations
            .add(new Relation(sigName, 1, Collections.singletonList(createType(sigName)),
                Collections.singletonList(sigCard)));
        // Search for relations declared in sigs
        for (int j = (hasExtension(sig) ? 1 : 0);
            j < sig.getSubnodes().size() - (hasFact(sig) ? 1 : 0); j++) {
          Browsable field = sig.getSubnodes().get(j);
          String fieldName = extractHTML(field.getHTML(), Pattern.compile("</b> (.*?) <i>"));
          String relationType = extractHTML(field.getHTML(), Pattern.compile("<i>\\{(.*?)\\}</i>"));
          String[] relationSubtypes = relationType.split(CROSS_PRODUCT);
          for (int k = 0; k < relationSubtypes.length; k++) {
            relationSubtypes[k] = afterSubstring(relationSubtypes[k], SLASH, true);
          }
          // The arity of relations should be >= 2, we do not
          // support card1 mult1->mult2 card2 except the default ->.
          // But we do support card for binary relations.
          if (relationSubtypes.length == 2) {
            Browsable card = field.getSubnodes().get(0);
            String rightCard = cardOfRel(extractHTML(card.getHTML(), Pattern.compile("(.*?) <i>")));
            Type leftType = createType(relationSubtypes[0]);
            Type rightType = createType(relationSubtypes[1]);
            sigsAndRelations.add(new Relation(fieldName, 2, Arrays.asList(leftType, rightType),
                Arrays.asList(sigCard, rightCard)));
          } else { // relationSubtypes.length >= 3
            List<String> cards = new ArrayList<>();
            for (int k = 0; k < relationSubtypes.length; k++) {
              if (k == 0) {
                cards.add(sigCard);
              } else {
                cards.add(Constants.SET);
              }
            }
            sigsAndRelations.add(
                new Relation(fieldName, relationSubtypes.length, createTypes(relationSubtypes),
                    cards));
          }
        }
      }
    }
    return sigsAndRelations;
  }

  public static void findParametersAndVariables(AlloyProgram alloyProgram, CompModule module,
      String browsableName) {
    Browsable browsable = findSubnode(module, browsableName);
    if (browsable != null) {
      for (int i = 0; i < browsable.getSubnodes().size(); i++) {
        Browsable construct = browsable.getSubnodes().get(i);
        RowSpan rowSpan = new RowSpan(construct.pos().y - 1, construct.pos().y2 - 1);
        List<E> holesInRowSpan = findRelevantExprHoles(rowSpan, alloyProgram.getHoles());
        for (Browsable para : findSubnodes(construct, "parameter")) {
          for (E exprHole : holesInRowSpan) {
            exprHole.addPrimaryRelations(createRelationFromParameter(para));
          }
        }
        visitVariables(construct, holesInRowSpan);
      }
    }
  }

  public static Relation createRelationFromParameter(Browsable para) {
    String paraName = extractHTML(para.getHTML(), Pattern.compile("</b> (.*?) <i>"));
    String paraType = extractHTML(para.getHTML(), Pattern.compile("<i>\\{(.*?)\\}</i>"));
    String[] paraSubtypes = paraType.split(CROSS_PRODUCT);
    for (int k = 0; k < paraSubtypes.length; k++) {
      paraSubtypes[k] = afterSubstring(paraSubtypes[k], SLASH, true);
    }
    String[] cards = new String[paraSubtypes.length];
    if (paraSubtypes.length == 1) { // Find parameter cardinality if arity == 1
      Browsable child = para.getSubnodes().get(0);
      String bold = extractHTML(child.getHTML(), Pattern.compile("<b>(.*?)</b>"));
      if (bold == null) { // Explicit cardinality.
        cards[0] = cardOfRel(extractHTML(child.getHTML(), Pattern.compile("(.*?) <i>")));
      } else { // Implicit cardinality is one.
        cards[0] = ONE.getValue();
      }
    } else { // arity >= 2
      for (int k = 0; k < cards.length; k++) {
        cards[k] = SET.getValue();
      }
    }
    return new Relation(paraName, paraSubtypes.length, createTypes(paraSubtypes),
        Arrays.asList(cards));
  }

  private static void visitVariables(Browsable browsable, List<E> exprHoles) {
    LinkedList<Browsable> astQueue = new LinkedList<>();
    Browsable body = findSubnode(browsable, "body");
    if (body != null) {
      astQueue.add(body);
    } else {
      astQueue.add(browsable);
    }
    while (!astQueue.isEmpty()) {
      Browsable astNode = astQueue.remove();
      List<? extends Browsable> children = astNode.getSubnodes();
      if (children.size() >= 2) {
        // First var.
        Browsable quantifiedVar = children.get(0);
        String varBold = extractHTML(quantifiedVar.getHTML(), Pattern.compile("<b>(.*?)</b>"));
        // Last body.
        Browsable quantifiedBody = children.get(children.size() - 1);
        String bodyBold = extractHTML(quantifiedBody.getHTML(), Pattern.compile("<b>(.*?)</b>"));
        // Find quantified formula
        if (varBold != null && varBold.equals("var") && bodyBold != null && bodyBold
            .equals("body")) {
          // Find expression holes in the quantified formula body
          RowSpan bodyRowSpan = new RowSpan(quantifiedBody.pos().y - 1,
              quantifiedBody.pos().y2 - 1);
          List<E> holesInRowSpan = findRelevantExprHoles(bodyRowSpan, exprHoles);
          // Add all variable declarations.
          for (int i = 0; i < children.size() - 1; i++) {
            quantifiedVar = children.get(i);
            for (E exprHole : holesInRowSpan) {
              exprHole.addPrimaryRelations(createRelationFromVariable(quantifiedVar));
            }
          }
        }
      }
      for (Browsable n : astNode.getSubnodes()) {
        String bold = extractHTML(n.getHTML(), Pattern.compile("<b>(.*?)</b>"));
        if (bold == null) {
          astQueue.add(n);
        } else if (bold.equals("field") || bold.equals("sig") || bold.equals("call"));
        else {
          astQueue.add(n);
        }
      }
    }
  }

  public static Relation createRelationFromVariable(Browsable var) {
    // Collect quantified variable name, arity, type and cardinality
    String varName = extractHTML(var.getHTML(), Pattern.compile("</b> (.*?) <i>"));
    String varType = extractHTML(var.getHTML(), Pattern.compile("<i>\\{(.*?)\\}</i>"));
    String[] varSubtypes = varType.split(CROSS_PRODUCT);
    for (int k = 0; k < varSubtypes.length; k++) {
      varSubtypes[k] = afterSubstring(varSubtypes[k], SLASH, true);
    }
    String[] cards = new String[varSubtypes.length];
    if (varSubtypes.length == 1) { // Find var cardinality if arity == 1
      Browsable child = var.getSubnodes().get(0);
      cards[0] = cardOfRel(extractHTML(child.getHTML(), Pattern.compile("(.*?) <i>")));
    } else { // arity >= 2
      for (int k = 0; k < cards.length; k++) {
        cards[k] = "set";
      }
    }
    return new Relation(varName, varSubtypes.length, createTypes(varSubtypes),
        Arrays.asList(cards));
  }

  private static List<E> findRelevantExprHoles(RowSpan rowSpan, List<? extends Hole> holes) {
    List<E> holesInRowSpan = new ArrayList<>();
    for (Hole hole : holes) {
      if (rowSpan.containsLine(hole.getLineNumber()) && hole instanceof E) {
        holesInRowSpan.add((E) hole);
      }
    }
    return holesInRowSpan;
  }

  public static Browsable findSubnode(Browsable parent, String childName) {
    for (Browsable child : parent.getSubnodes()) {
      String bold = extractHTML(child.getHTML(), Pattern.compile("<b>(.*?)</b>"));
      if (bold != null && bold.contains(childName)) {
        return child;
      }
    }
    return null;
  }

  public static List<Browsable> findSubnodes(Browsable parent, String childName) {
    List<Browsable> result = new ArrayList<>();
    for (Browsable child : parent.getSubnodes()) {
      String bold = extractHTML(child.getHTML(), Pattern.compile("<b>(.*?)</b>"));
      if (bold != null && bold.contains(childName)) {
        result.add(child);
      }
    }
    return result;
  }

  public static String extractHTML(String html, Pattern pattern) {
    Matcher matcher = pattern.matcher(html);
    if (matcher.find()) {
      return matcher.group(1);
    } else {
      return null;
    }
  }

  public static String extractBold(Browsable browsable) {
    String res = extractHTML(browsable.getHTML(), Pattern.compile("<b>(.*?)</b>"));
    return res == null ? null : res.trim();
  }

  public static String extractNormal(Browsable browsable) {
    String res = extractHTML(browsable.getHTML(), Pattern.compile("</b>:?(.*?) <i>"));
    if (res != null) {
      return res.trim();
    }
    res = extractHTML(browsable.getHTML(), Pattern.compile("(.*?) <i>"));
    return res == null ? null : res.trim();
  }

  private static boolean hasExtension(Sig s) {
    if (s.getSubnodes().isEmpty()) {
      return false;
    } else {
      Browsable ext = s.getSubnodes().get(0);
      String sigExt = extractHTML(ext.getHTML(), Pattern.compile("<b>(.*?)</b>"));
      return sigExt != null && (sigExt.equals("extends sig") || sigExt.equals("in sig"));
    }
  }

  private static boolean hasFact(Sig s) {
    if (s.getSubnodes().isEmpty()) {
      return false;
    } else {
      int size = s.getSubnodes().size();
      Browsable fact = s.getSubnodes().get(size - 1);
      String sigFact = extractHTML(fact.getHTML(), Pattern.compile("<b>(.*?)</b>"));
      return sigFact != null && sigFact.equals("fact");
    }
  }

  public static String cardOfSig(Sig s) {
    if (s.isLone != null) {
      return Constants.LONE;
    } else if (s.isOne != null) {
      return Constants.ONE;
    } else if (s.isSome != null) {
      return Constants.SOME;
    } else {
      return Constants.SET;
    }
  }

  public static String cardOfRel(String card) {
    switch (card) {
      case "lone of":
        return Constants.LONE;
      case "one of":
        return Constants.ONE;
      case "some of":
        return Constants.SOME;
      case "set of":
        return Constants.SET;
      default:
        return null;
    }
  }

  public static List<Type> createTypes(String... typeNames) {
    List<Type> types = new ArrayList<>();
    for (String typeName : typeNames) {
      types.add(createType(typeName));
    }
    return types;
  }

  public static Type createType(String type) {
    return createType(type, type);
  }

  public static Type createType(String genType, String pruningType) {
    return new Type(genType, pruningType);
  }

  public static List<String> repeats(String toRepeat, int repeats) {
    List<String> res = new ArrayList<>();
    for (int i = 0; i < repeats; i++) {
      res.add(toRepeat);
    }
    return res;
  }

  public static CompModule compileAlloyModule(String modelPath) {
    try {
      return CompUtil.parseEverything_fromFile(A4Reporter.NOP, null, modelPath);
    } catch (Err e) {
      return null;
    }
  }

  public static int[] connectExprHolesWithSameVarName(List<Hole> holes) {
    // Quick union connected graph
    int[] connectedHoles = new int[holes.size()];
    // Initialize connected graph with -1
    for (int i = 0; i < connectedHoles.length; i++) {
      connectedHoles[i] = -1;
    }
    // uniqueRelations keeps track of the holes that share the same variable name.
    Map<String, Integer> uniqueVars = new HashMap<>();
    for (int i = 0; i < holes.size(); i++) {
      Hole hole = holes.get(i);
      // Find the index of the first hole with the key
      int rootHoleIndex = uniqueVars.getOrDefault(hole.getName(), i);
      uniqueVars.putIfAbsent(hole.getName(), rootHoleIndex);
      // Connect the first hole with the same basic candidates to the rest holes.
      // Quick union
      connectedHoles[i] = rootHoleIndex;
    }
    return connectedHoles;
  }

  /**
   * Create a unique name for the original sigs and replace all occurrence of the original name in
   * the relationName with the new name.
   */
  public static String generateRelationNameInMetaModel(String relationName,
      List<Relation> sigsAndFields) {
    for (Relation relation : sigsAndFields) {
      // Ignore relations other than sigs, this is because
      // we don't want to handle x and x'.
      if (relation.getTypes().size() != 1) {
        continue;
      }
      String sigName = relation.getValue();
      String newSigName = sigName + "s";
      relationName = relationName.replaceAll(sigName, newSigName);
    }
    return relationName;
  }

  public static void putNodeToHoleMapIfAbsent(Browsable node, String nodeValue, List<Hole> holes,
      Set<Hole> assignedHoles, List<Relation> relations, Map<Browsable, Integer> nodeToHoleMap) {
    // All nodes are associated with some nodes.
    if (assignedHoles.size() == holes.size()) {
      return;
    }
    // Iterate over each hole and see if the pos matches its line number and column span.
    Pos pos = node.pos();
    for (int holeId = 0; holeId < holes.size(); holeId++) {
      Hole hole = holes.get(holeId);
      // Skip if the content does not match or the hole is assigned before
      if (!nodeValue.equals("->")
          && (!hole.getContent().equals(nodeValue) || assignedHoles.contains(hole))) {
        continue;
      }
      // Skip if the hole fall outside of pos
      if (hole.getLineNumber() < pos.y - 1 || hole.getLineNumber() > pos.y2 - 1) {
        continue;
      }
      // If the node is in a single line and it intersects with the hole,
      // then add it.
      if (pos.y == pos.y2) {
        ColSpan colSpan = hole.getColSpan();
        if (pos.x - 1 >= colSpan.getEnd() || pos.x2 <= colSpan.getBegin()) {
          continue;
        }
        if (!(hole instanceof E)) {
          hole.addPrimaryRelations(relations);
        }
        nodeToHoleMap.put(node, holeId);
        assignedHoles.add(hole);
        return;
      }
      // Pick the first hole and assign it to the node.  This is not correct but may
      // be sufficient for our example.
      if (!(hole instanceof E)) {
        hole.addPrimaryRelations(relations);
      }
      nodeToHoleMap.put(node, holeId);
      assignedHoles.add(hole);
      break;
    }
  }
}
