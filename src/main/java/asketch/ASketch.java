package asketch;

import static asketch.alloy.AlloyASTVisitor.constructPreds;
import static asketch.alloy.etc.Constants.ABSTRACT_BO;
import static asketch.alloy.etc.Constants.ABSTRACT_CO;
import static asketch.alloy.etc.Constants.ABSTRACT_E;
import static asketch.alloy.etc.Constants.ABSTRACT_LO;
import static asketch.alloy.etc.Constants.ABSTRACT_Q;
import static asketch.alloy.etc.Constants.ABSTRACT_UO;
import static asketch.alloy.etc.Constants.ABSTRACT_UOE;
import static asketch.alloy.etc.Constants.ABSTRACT_UOF;
import static asketch.alloy.etc.Constants.BO_FUN_NAME;
import static asketch.alloy.etc.Constants.CO_FUN_NAME;
import static asketch.alloy.etc.Constants.CROSS_PRODUCT;
import static asketch.alloy.etc.Constants.DIFF;
import static asketch.alloy.etc.Constants.EQ;
import static asketch.alloy.etc.Constants.EXPR_FUN_NAME;
import static asketch.alloy.etc.Constants.IN;
import static asketch.alloy.etc.Constants.INT;
import static asketch.alloy.etc.Constants.INTERSECT;
import static asketch.alloy.etc.Constants.LONE;
import static asketch.alloy.etc.Constants.NEQ;
import static asketch.alloy.etc.Constants.NO;
import static asketch.alloy.etc.Constants.NONE;
import static asketch.alloy.etc.Constants.NOT_IN;
import static asketch.alloy.etc.Constants.ONE;
import static asketch.alloy.etc.Constants.REFLEXIVE_TRANSITIVE_CLOSURE;
import static asketch.alloy.etc.Constants.RESULT_E;
import static asketch.alloy.etc.Constants.SKETCH_COMMAND_NAME;
import static asketch.alloy.etc.Constants.SLASH;
import static asketch.alloy.etc.Constants.SOME;
import static asketch.alloy.etc.Constants.TRANSITIVE_CLOSURE;
import static asketch.alloy.etc.Constants.TRANSPOSE;
import static asketch.alloy.etc.Constants.UNION;
import static asketch.alloy.etc.Constants.UNIV;
import static asketch.alloy.etc.Constants.UOE_FUN_NAME;
import static asketch.alloy.etc.Constants.UO_FUN_NAME;
import static asketch.alloy.util.AlloyUtil.cardOfSig;
import static asketch.alloy.util.AlloyUtil.compileAlloyModule;
import static asketch.alloy.util.AlloyUtil.connectExprHolesWithSameVarName;
import static asketch.alloy.util.AlloyUtil.extractHTML;
import static asketch.alloy.util.AlloyUtil.findSigsAndFields;
import static asketch.alloy.util.AlloyUtil.findSubnode;
import static asketch.alloy.util.AlloyUtil.generateRelationNameInMetaModel;
import static asketch.alloy.util.AlloyUtil.repeats;
import static asketch.etc.Names.NEW_LINE;
import static asketch.etc.Names.SOLVE_FILE_PATH;
import static asketch.opts.DefaultOptions.logger;
import static asketch.util.FileUtil.createDirsIfNotExist;
import static asketch.util.StringUtil.afterSubstring;
import static asketch.util.Util.printASketchUsage;

import asketch.alloy.ASketchParser;
import asketch.alloy.RelationAndVariableCollector;
import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.Relation;
import asketch.alloy.cand.Type;
import asketch.alloy.exception.AlloySyntaxErrorException;
import asketch.alloy.exception.HoleParsingException;
import asketch.alloy.exception.TestValuationUnsatisfiableException;
import asketch.alloy.fragment.BO;
import asketch.alloy.fragment.CO;
import asketch.alloy.fragment.E;
import asketch.alloy.fragment.Hole;
import asketch.alloy.fragment.LO;
import asketch.alloy.fragment.Q;
import asketch.alloy.fragment.UO;
import asketch.alloy.fragment.UOE;
import asketch.alloy.fragment.UOF;
import asketch.alloy.util.AlloyProgram;
import asketch.alloy.util.TestTranslator;
import asketch.opts.ASketchOpt;
import asketch.util.TextFileReader;
import asketch.util.TextFileWriter;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Browsable;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.ast.Sig;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class represents the hierarchical technique for ASketchSolve.
 */
public class ASketch {

  public static void solveMetaModelForConcreteHoleValues(AlloyProgram alloyProgram,
      CompModule module, ASketchOpt opt)
      throws TestValuationUnsatisfiableException, AlloySyntaxErrorException, HoleParsingException {
    // Stores solution if there is any.
    String[] solution = new String[alloyProgram.getHoles().size()];
    // Construct the meta Alloy model that encodes many models
    // and save it to the disk.
    Map<String, String> mapToOriginalNames = constructMetaAlloyModelAndSaveIt(alloyProgram, module,
        opt);
    // Solving
    final String[] lastReport = new String[5];
    A4Reporter reporter = new A4Reporter() {
      @Override
      public void translate(String solver, int bitwidth, int maxseq, int skolemDepth,
          int symmetry) {
        super.translate(solver, bitwidth, maxseq, skolemDepth, skolemDepth);
        lastReport[0] = solver;
      }

      @Override
      public void solve(int primaryVars, int totalVars, int clauses) {
        super.solve(primaryVars, totalVars, clauses);
        lastReport[1] = String.valueOf(primaryVars);
        lastReport[2] = String.valueOf(totalVars);
        lastReport[3] = String.valueOf(clauses);
      }

      @Override
      public void resultSAT(Object command, long solvingTime, Object solution) {
        super.resultSAT(command, solvingTime, solution);
        lastReport[4] = String.valueOf(solvingTime);
      }
    };
    Map<String, String> mapToSigNames = new HashMap<>();
    List<Hole> holes = alloyProgram.getHoles();
    long searchSpace = 1;
    for (int i = 0; i < holes.size(); i++) {
      Hole hole = holes.get(i);
      searchSpace *= hole.getCands().size();
      logger.info(
          "Hole " + i + ": Name(" + hole.getName() + ") Type(" + hole.getClass().getSimpleName()
              + ") Candidates(" + hole.getCands().size() + ")");
    }
    logger.info("Search Space: " + searchSpace);

    String metaModel = TextFileReader.readText(SOLVE_FILE_PATH);
    StringBuilder fact = new StringBuilder();
    boolean foundSolution = false;
    for (int i = 0; i < opt.getSolNum(); i++) {
      fact.append(String.join("||",
          mapToSigNames.entrySet().stream()
              .map(entry -> entry.getKey() + "!=" + entry.getValue())
              .collect(Collectors.toList())))
          .append(NEW_LINE);
      TextFileWriter
          .writeText(metaModel + "\nfact {\n" + fact.toString() + "}", SOLVE_FILE_PATH, false);
      if (findSolution(reporter, opt.getA4Options(), mapToOriginalNames, solution, mapToSigNames)) {
        foundSolution = true;
        logger.info("Solution " + (i + 1) + " (" + lastReport[4] + "ms): " + String
            .join(", ", Arrays.asList(solution)));
      } else {
        break;
      }
    }
    if (!foundSolution) {
      logger.info("No solution is found.");
    }
  }

  public static boolean findSolution(A4Reporter reporter, A4Options options,
      Map<String, String> mapToOriginalRelationNames, String[] solution,
      Map<String, String> mapToSigNames) {
    CompModule metaModel = compileAlloyModule(SOLVE_FILE_PATH);
    Command runSketch = null;
    for (Command command : metaModel.getAllCommands()) {
      String cmdName = extractHTML(command.getHTML(), Pattern.compile("</b> (.*?)$"));
      if (SKETCH_COMMAND_NAME.equals(cmdName)) {
        runSketch = command;
        break;
      }
    }
    assert runSketch != null;
    try {
      A4Solution valuation = TranslateAlloyToKodkod
          .execute_command(reporter, metaModel.getAllReachableSigs(), runSketch, options);
      if (valuation != null && valuation.satisfiable()) {
        String xml = valuation.toString();
        Matcher matcher = Pattern.compile("(_R.*_)(\\d+)=\\{(.*?)\\$\\d*\\}").matcher(xml);
        while (matcher.find()) {
          String resSigName = matcher.group(1);
          int holeId = Integer.valueOf(matcher.group(2));
          String sigName = matcher.group(3);
          solution[holeId] = mapToOriginalRelationNames.get(sigName);
          // In case if we want to generate more solutions.
          mapToSigNames.put(resSigName + holeId, sigName);
        }
        return true;
      }
    } catch (Err err) {
      err.printStackTrace();
    }
    return false;
  }

  public static Map<String, String> constructMetaAlloyModelAndSaveIt(AlloyProgram alloyProgram,
      CompModule module, ASketchOpt opt)
      throws TestValuationUnsatisfiableException, AlloySyntaxErrorException, HoleParsingException {
    // Load expressions generated for each hole.  Only the first
    // hole with the unique set of basic relations gets assigned.
    loadExpressionsOfHoles(alloyProgram, opt);

    // Find signatures and fields as they are declared.
    List<Relation> sigsAndFields = findSigsAndFields(module);
    // Quick union and find.  The indexes of holes with the same
    // variable name will reuse the same generated construct.
    List<Hole> holes = alloyProgram.getHoles();
    int[] connectedHoles = connectExprHolesWithSameVarName(holes);
    // If an expression hole must be integer type, then all the holes connected to the expression
    // hole must also be integer type.
    Set<Integer> intRootIndexes = new HashSet<>();
    for (int i = 0; i < holes.size(); i++) {
      Hole hole = holes.get(i);
      if (!(hole instanceof E)) {
        continue;
      }
      E exprHole = (E) hole;
      if (exprHole.isIntType()) {
        intRootIndexes.add(connectedHoles[i]);
      }
    }
    for (int i = 0; i < holes.size(); i++) {
      if (intRootIndexes.contains(connectedHoles[i])) {
        ((E) holes.get(i)).setIntType(true);
      }
    }
    // Meta model
    StringBuilder metaModel = new StringBuilder();
    // Construct sig for meta model.
    metaModel.append(constructSigsAndRun(module, opt));
    // Construct pred for meta model.
    metaModel.append(constructPreds(alloyProgram, module));
    // Map from the sig names back to the original operators or expressions.
    Map<String, String> mapToOriginalNames = new HashMap<>();
    // Generate operator sigs and functions on demand.
    metaModel
        .append(generateOperatorSigsAndFunDecl(alloyProgram, connectedHoles, mapToOriginalNames));
    // Construct function declarations for expression holes.
    // Append them to the meta Alloy model
    String funDecl = generateFunDecl(alloyProgram, connectedHoles, sigsAndFields,
        mapToOriginalNames, opt);
    metaModel.append(funDecl);
    // Add test facts translated from test predicates.
    String testFacts = TestTranslator.translate(opt);
    metaModel.append(testFacts);
    // Save the meta model to disk so we can use Alloy analyzer to parse it.
    TextFileWriter.writeText(metaModel.toString(), SOLVE_FILE_PATH, false);
    // Return a mapping to the original relation names.
    return mapToOriginalNames;
  }

  private static String constructSigsAndRun(CompModule module, ASketchOpt opt) {
    // Construct signature declarations.
    StringBuilder sigDeclAndRun = new StringBuilder();
    Browsable sigParent = findSubnode(module, "sig");
    if (sigParent != null) {
      List<? extends Browsable> sigs = sigParent.getSubnodes();
      for (int i = 0; i < sigs.size(); i++) {
        Sig sig = (Sig) sigs.get(i);
        sigDeclAndRun.append(sig.isAbstract == null ? "" : "abstract ");
        String mult = cardOfSig(sig);
        if (mult.equals("set")) {
          mult = "";
        } else {
          mult += " ";
        }
        String ext = "";
        if (!sig.isTopLevel()) {
          if (sig.isSubsig != null) { // S extends P
            ext = " extends " + afterSubstring(((Sig.PrimSig) sig).parent.label, SLASH, true);
          } else { // S in P
            ext = " in " + afterSubstring(((Sig.SubsetSig) sig).parents.get(0).label, SLASH, true);
          }
        }
        sigDeclAndRun.append(mult).append("sig ")
            .append(afterSubstring(sig.toString(), SLASH, true)).append(ext).append(" {}\n\n");
      }
    }
    sigDeclAndRun.append(SKETCH_COMMAND_NAME).append(": run {} for ").append(opt.getScope())
        .append(" but ").append(opt.getIntegerBitWidth()).append(" Int")
        .append("\n\n");
    return sigDeclAndRun.toString();
  }

  private static String generateOperatorSigsAndFunDecl(AlloyProgram alloyProgram,
      int[] connectedHoles, Map<String, String> mapToOriginalOperatorNames) {
    StringBuilder operatorSigsAndFunDecl = new StringBuilder();
    Set<Class> visitedOperatorType = new HashSet<>();
    List<Hole> holes = alloyProgram.getHoles();
    for (int i = 0; i < holes.size(); i++) {
      Hole hole = holes.get(i);
      if (hole instanceof Q) {
        if (visitedOperatorType.add(hole.getClass())) {
          String all = ABSTRACT_Q + "_All";
          String no = ABSTRACT_Q + "_No";
          String some = ABSTRACT_Q + "_Some";
          String lone = ABSTRACT_Q + "_Lone";
          String one = ABSTRACT_Q + "_One";
          operatorSigsAndFunDecl.append("abstract sig " + ABSTRACT_Q + " {}\n")
              .append("one sig " + all + ", " + no + ", " + some + ", " + lone + ", " + one
                  + " extends " + ABSTRACT_Q + " {}\n");
          mapToOriginalOperatorNames.put(all, "all");
          mapToOriginalOperatorNames.put(no, "no");
          mapToOriginalOperatorNames.put(some, "some");
          mapToOriginalOperatorNames.put(lone, "lone");
          mapToOriginalOperatorNames.put(one, "one");
          // Quantifier function declaration must be generated on the fly.
        }
      } else if (hole instanceof CO) {
        String eq = ABSTRACT_CO + "_Eq";
        String in = ABSTRACT_CO + "_In";
        String neq = ABSTRACT_CO + "_NEq";
        String nin = ABSTRACT_CO + "_NIn";
        if (visitedOperatorType.add(hole.getClass())) {
          operatorSigsAndFunDecl.append("abstract sig " + ABSTRACT_CO + " {}\n")
              .append(
                  "one sig " + eq + ", " + in + ", " + neq + ", " + nin + " extends " + ABSTRACT_CO
                      + " {}\n");
          mapToOriginalOperatorNames.put(eq, "=");
          mapToOriginalOperatorNames.put(in, "in");
          mapToOriginalOperatorNames.put(neq, "!=");
          mapToOriginalOperatorNames.put(nin, "!in");
        }
        if (connectedHoles[i] != i) {
          continue;
        }
        Set<String> cands = hole.getCands()
            .stream().map(Candidate::getValue).collect(Collectors.toSet());
        StringBuilder content = new StringBuilder();
        if (cands.contains(EQ)) {
          content.append("  h = " + eq + " => e1 " + EQ + " e2\n");
        }
        if (cands.contains(IN)) {
          content.append("  h = " + in + " => e1 " + IN + " e2\n");
        }
        if (cands.contains(NEQ)) {
          content.append("  h = " + neq + " => e1 " + NEQ + " e2\n");
        }
        if (cands.contains(NOT_IN)) {
          content.append("  h = " + nin + " => e1 " + NOT_IN + " e2\n");
        }
        operatorSigsAndFunDecl.append("pred " + CO_FUN_NAME + i +
            "(h: " + ABSTRACT_CO + ", e1, e2: univ) {\n").append(content).append("}\n\n");
        operatorSigsAndFunDecl.append("pred " + CO_FUN_NAME + i +
            "(h: " + ABSTRACT_CO + ", e1, e2: univ->univ) {\n").append(content).append("}\n\n");
      } else if (hole instanceof UO) {
        String no = ABSTRACT_UO + "_No";
        String some = ABSTRACT_UO + "_Some";
        String lone = ABSTRACT_UO + "_Lone";
        String one = ABSTRACT_UO + "_One";
        if (visitedOperatorType.add(hole.getClass())) {
          operatorSigsAndFunDecl.append("abstract sig " + ABSTRACT_UO + " {}\n")
              .append("one sig " + no + ", " + some + ", " + lone + ", " + one + " extends "
                  + ABSTRACT_UO + " {}\n");
          mapToOriginalOperatorNames.put(no, "no");
          mapToOriginalOperatorNames.put(some, "some");
          mapToOriginalOperatorNames.put(lone, "lone");
          mapToOriginalOperatorNames.put(one, "one");
        }
        if (connectedHoles[i] != i) {
          continue;
        }
        Set<String> cands = hole.getCands()
            .stream().map(Candidate::getValue).collect(Collectors.toSet());
        StringBuilder content = new StringBuilder();
        if (cands.contains(NO)) {
          content.append("  h = " + no + " => " + NO + " e\n");
        }
        if (cands.contains(SOME)) {
          content.append("  h = " + some + " => " + SOME + " e \n");
        }
        if (cands.contains(LONE)) {
          content.append("  h = " + lone + " => " + LONE + " e\n");
        }
        if (cands.contains(ONE)) {
          content.append("  h = " + one + " => " + ONE + " e\n");
        }
        operatorSigsAndFunDecl
            .append("pred " + UO_FUN_NAME + i + "(h: " + ABSTRACT_UO + ", e: univ) {\n")
            .append(content).append("}\n\n");
        operatorSigsAndFunDecl
            .append("pred " + UO_FUN_NAME + i + "(h: " + ABSTRACT_UO + ", e: univ->univ) {\n")
            .append(content).append("}\n\n");
      } else if (hole instanceof BO) {
        String amp = ABSTRACT_BO + "_Intersect";
        String plus = ABSTRACT_BO + "_Union";
        String minus = ABSTRACT_BO + "_Diff";
        if (visitedOperatorType.add(hole.getClass())) {
          operatorSigsAndFunDecl.append("abstract sig " + ABSTRACT_BO + " {}\n")
              .append("one sig " + amp + ", " + plus + ", " + minus + " extends " + ABSTRACT_BO
                  + " {}\n");
          mapToOriginalOperatorNames.put(amp, "&");
          mapToOriginalOperatorNames.put(plus, "+");
          mapToOriginalOperatorNames.put(minus, "-");
        }
        if (connectedHoles[i] != i) {
          continue;
        }
        Set<String> cands = hole.getCands()
            .stream().map(Candidate::getValue).collect(Collectors.toSet());
        StringBuilder content = new StringBuilder();
        if (cands.contains(INTERSECT)) {
          content.append("  h = " + amp + " => e1 " + INTERSECT + " e2 else\n");
        }
        if (cands.contains(UNION)) {
          content.append("  h = " + plus + " => e1 " + UNION + " e2 else\n");
        }
        if (cands.contains(DIFF)) {
          content.append("  h = " + minus + " => e1 " + DIFF + " e2 else ");
        }
        operatorSigsAndFunDecl.append(
            "fun " + BO_FUN_NAME + i + "(h: " + ABSTRACT_BO + ", e1, e2: univ): set univ {\n")
            .append(content).append("none\n}\n\n");
        operatorSigsAndFunDecl.append(
            "fun " + BO_FUN_NAME + i + "(h: " + ABSTRACT_BO
                + ", e1, e2: univ->univ): set univ->univ {\n")
            .append(content).append("none->none\n}\n\n");
      } else if (hole instanceof UOE) {
        String tilde = ABSTRACT_UOE + "_Transpose";
        String star = ABSTRACT_UOE + "_Reflexive_Closure";
        String caret = ABSTRACT_UOE + "_Closure";
        if (visitedOperatorType.add(hole.getClass())) {
          operatorSigsAndFunDecl.append("abstract sig " + ABSTRACT_UOE + " {}\n")
              .append("one sig " + tilde + ", " + star + ", " + caret + " extends " + ABSTRACT_UOE
                  + " {}\n");
          mapToOriginalOperatorNames.put(tilde, "~");
          mapToOriginalOperatorNames.put(star, "*");
          mapToOriginalOperatorNames.put(caret, "^");
        }
        if (connectedHoles[i] != i) {
          continue;
        }
        Set<String> cands = hole.getCands()
            .stream().map(Candidate::getValue).collect(Collectors.toSet());
        operatorSigsAndFunDecl.append(
            "fun " + UOE_FUN_NAME + i + "(h: " + ABSTRACT_UOE + ", e: univ->univ): univ->univ {\n");
        if (cands.contains(TRANSPOSE)) {
          operatorSigsAndFunDecl.append("  h = " + tilde + " => " + TRANSPOSE + "e else\n");
        }
        if (cands.contains(REFLEXIVE_TRANSITIVE_CLOSURE)) {
          operatorSigsAndFunDecl
              .append("  h = " + star + " => " + REFLEXIVE_TRANSITIVE_CLOSURE + " e else\n");
        }
        if (cands.contains(TRANSITIVE_CLOSURE)) {
          operatorSigsAndFunDecl
              .append("  h = " + caret + " => " + TRANSITIVE_CLOSURE + " e else none->none\n");
        }
        operatorSigsAndFunDecl.append("}\n\n");
      } else if (hole instanceof LO) {
        if (visitedOperatorType.add(hole.getClass())) {
          String and = ABSTRACT_LO + "_And";
          String or = ABSTRACT_LO + "_Or";
          String biImply = ABSTRACT_LO + "_BiImply";
          String imply = ABSTRACT_LO + "_Imply";
          operatorSigsAndFunDecl.append("abstract sig " + ABSTRACT_LO + " {}\n")
              .append("one sig " + and + ", " + or + ", " + biImply + ", " + imply
                  + " extends " + ABSTRACT_LO + " {}\n");
          mapToOriginalOperatorNames.put(and, "&&");
          mapToOriginalOperatorNames.put(or, "||");
          mapToOriginalOperatorNames.put(biImply, "<=>");
          mapToOriginalOperatorNames.put(imply, "=>");
          // Logical operator function declaration must be generated on the fly.
        }
      } else if (hole instanceof UOF) {
        if (visitedOperatorType.add(hole.getClass())) {
          String origin = ABSTRACT_UOF + "_Origin";
          String negate = ABSTRACT_UOF + "_Negate";
          operatorSigsAndFunDecl.append("abstract sig " + ABSTRACT_UOF + " {}\n")
              .append("one sig " + origin + ", " + negate + " extends " + ABSTRACT_UOF + " {}\n");
          mapToOriginalOperatorNames.put(origin, "ɛ");
          mapToOriginalOperatorNames.put(negate, "!");
          // Unary operator formula function declaration must be generated on the fly.
        }
      } else {
        // TODO(kaiyuan): Implement other types of holes
      }
    }
    return operatorSigsAndFunDecl.toString();
  }

  public static void loadExpressionsOfHoles(AlloyProgram alloyProgram, ASketchOpt opt) {
    List<Hole> holes = alloyProgram.getHoles();
    for (int i = 0; i < holes.size(); i++) {
      Hole hole = holes.get(i);
      // Add relations to holes with unique scope.
      hole.removeAllCands();
      hole.addCandidates(opt.getVarToExprs().get(hole.getName()));
    }
  }

  public static String generateFunDecl(AlloyProgram alloyProgram, int[] connectedHoles,
      List<Relation> sigsAndFields, Map<String, String> mapToOriginalRelationNames,
      ASketchOpt opt) {
    StringBuilder sigDecl = new StringBuilder();
    StringBuilder funDecl = new StringBuilder();
    List<Hole> holes = alloyProgram.getHoles();
    for (int i = 0; i < holes.size(); i++) {
      Hole hole = holes.get(i);
      if (connectedHoles[i] == -1 || !(hole instanceof E)) {
        continue;
      }
      // Generate special sig to store solving result.
      sigDecl.append("one sig ")
          .append(RESULT_E).append(i)
          .append(" in ").append(ABSTRACT_E).append(connectedHoles[i])
          .append(" {}\n\n");
      if (connectedHoles[i] != i) {
        continue;
      }
      E exprHole = (E) hole;
      List<Relation> basicRelations = exprHole.getPrimaryRelations();
      // Create abstract sig.
      String abstractSigName = ABSTRACT_E + i;
      sigDecl.append("abstract sig ").append(abstractSigName).append(" {}\n");
      // Create function parameter list.
      funDecl.append("fun ").append(EXPR_FUN_NAME).append(i)
          .append("[h: ").append(abstractSigName);
      for (Relation basicRelation : basicRelations) {
        String relationName = basicRelation.getValue();
        List<String> types = basicRelation.getTypes().stream()
            .map(Type::getGenType).collect(Collectors.toList());
        if (types.size() == 1) { // Sigs.
          if (relationName.equals(types.get(0))) {
            relationName = relationName + "s";
          }
          String relationType = types.get(0);
          String relationCard = basicRelation.getCards().get(0);
          funDecl.append(", ").append(relationName).append(": ").append(relationCard).append(" ")
              .append(relationType);
        } else { // Parameters, variables and fields.
          String relationType = String.join("->", types);
          funDecl.append(", ").append(relationName).append(": ").append(relationType);
        }
      }
      String columnType = exprHole.isIntType() ? INT : UNIV;
      funDecl.append("]: ").append(
          String.join(CROSS_PRODUCT,
              repeats(columnType, opt.getVarToArity().get(exprHole.getName()))))
          .append(" {\n");
      // Create sub sigs and function body
      sigDecl.append("one sig ");
      String sigPrefix = "";
      String funPrefix = "";
      StringBuilder closingParentheses = new StringBuilder();
      for (int cnt = 0; cnt < exprHole.getCands().size(); cnt++) {
        String subSigName = abstractSigName + "N" + cnt;
        String relationName = exprHole.getCands().get(cnt).getValue();
        String newRelationName = generateRelationNameInMetaModel(relationName, sigsAndFields);
        mapToOriginalRelationNames.put(subSigName, relationName);
        sigDecl.append(sigPrefix).append(subSigName);
        sigPrefix = ", ";
        funDecl.append(funPrefix).append("  (h = ").append(subSigName)
            .append(" => ").append(newRelationName)
            .append(" else");
        funPrefix = "\n";
        closingParentheses.append(")");
      }
      sigDecl.append(" extends ").append(abstractSigName).append("{}\n");
      funDecl.append(" ").append(
          String.join(CROSS_PRODUCT, repeats(NONE, opt.getVarToArity().get(exprHole.getName()))))
          .append(closingParentheses.toString()).append("\n");
      funDecl.append("}\n\n");
    }
    return sigDecl.toString() + funDecl.toString();
  }

  public static void main(String[] args)
      throws HoleParsingException, AlloySyntaxErrorException, TestValuationUnsatisfiableException {
    if (args.length != 5) {
      logger.error("Wrong number of arguments: " + args.length);
      printASketchUsage();
      return;
    }
    String modelPath = args[0];
    String fragmentPath = args[1];
    String testPath = args[2];
    int scope = Integer.valueOf(args[3]);
    int solNum = Integer.valueOf(args[4]);
    ASketchOpt opt = new ASketchOpt(modelPath, fragmentPath, testPath, scope, solNum);
    opt.getVarToExprs().forEach((k, v) -> logger.info(k + " := " + v));
    // Check if mandatory directories and files exist, and create them if not.
    createDirsIfNotExist();
    logger.debug("Arguments: " + String.join(", ", args));
    // Read Alloy model with holes as string
    String modelText = TextFileReader.readText(modelPath);
    // Parse Alloy program to detect holes.
    AlloyProgram alloyProgram = ASketchParser.parse(modelText);
    // Find relations and variables for each hole.
    CompModule module = RelationAndVariableCollector.collect(alloyProgram);
    // Collect arity of candidate expressions for each hole.
    opt.computeVariableArity(module);
    // Compute column span for each hole.
    alloyProgram.computeHoleColSpan();
    // Generate meta Alloy model and solve it.
    solveMetaModelForConcreteHoleValues(alloyProgram, module, opt);
  }
}
