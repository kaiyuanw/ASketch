package asketch.alloy.util;

import static asketch.alloy.etc.Constants.SLASH;
import static asketch.alloy.util.AlloyUtil.compileAlloyModule;
import static asketch.alloy.util.AlloyUtil.extractHTML;
import static asketch.alloy.util.AlloyUtil.findSigsAndFields;
import static asketch.alloy.util.AlloyUtil.findSubnode;
import static asketch.etc.Names.TEMP_TEST_PATH;
import static asketch.util.StringUtil.afterSubstring;
import static asketch.util.StringUtil.beforeSubstring;

import asketch.alloy.ASketchParser;
import asketch.alloy.RelationAndVariableCollector;
import asketch.alloy.cand.Relation;
import asketch.alloy.etc.RowSpan;
import asketch.alloy.exception.AlloySyntaxErrorException;
import asketch.alloy.exception.HoleParsingException;
import asketch.alloy.exception.TestValuationUnsatisfiableException;
import asketch.alloy.fragment.Hole;
import asketch.opts.ASketchOpt;
import asketch.util.TextFileReader;
import asketch.util.TextFileWriter;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Browsable;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.Func;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.ast.Sig;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;
import java.util.ArrayList;
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
 * This class represents the test translator that convert AUnit test predicate to the equivalent
 * fact ASketchSolve uses.
 */
public class TestTranslator {

  public static String translate(ASketchOpt opt)
      throws HoleParsingException, AlloySyntaxErrorException, TestValuationUnsatisfiableException {
    String modelText = TextFileReader.readText(opt.getModelPath());
    modelText += TextFileReader.readText(opt.getTestPath());
    // Parse Alloy program to detect holes.
    AlloyProgram alloyProgram = ASketchParser.parse(modelText);
    // Find relations and variables for each hole.
    CompModule module = RelationAndVariableCollector.collect(alloyProgram);
    // Remove holes in the model and save as a temp model.
    String modelWithoutHoles = removePredicateBody(alloyProgram, module);
    TextFileWriter.writeText(modelWithoutHoles, TEMP_TEST_PATH, false);
    // Compile the newly generated test temp model.
    Module testModule = compileAlloyModule(TEMP_TEST_PATH);
    if (testModule == null) {
      throw new AlloySyntaxErrorException(
          "ASketch test translator cannot handle the test model." +
              "  The model might contains syntax errors besides hole notations."
      );
    }
    // Parse the temp Alloy model.
    String newModelText = TextFileReader.readText(TEMP_TEST_PATH);
    alloyProgram = ASketchParser.parse(newModelText);
    // Find signatures and fields.  The order is consistent with
    // predicate invocations created later.
    // Find signatures and fields as they are declared.
    List<Relation> sigsAndFields = findSigsAndFields(module);

    // Compute the arity for each fields.
    Map<String, Integer> relationArity = new HashMap<>();
    for (Sig sig : testModule.getAllSigs()) {
      for (Sig.Field relation : sig.getFields()) {
        String relationType = extractHTML(relation.getHTML(),
            Pattern.compile("<i>\\{(.*?)\\}</i>"));
        assert relationType != null;
        relationArity.put(relation.label, relationType.split("->").length);
      }
    }
    StringBuilder testSuite = new StringBuilder();
    // For each test predicate, translate it to test fact and add to the test suite.
    A4Options options = new A4Options();
    for (Func func : testModule.getAllFunc()) {
      if (func.isPred) {
        String predName = afterSubstring(func.label, SLASH, true);
        if (predName.toLowerCase().startsWith("test")) {
          List<PredCall> predCalls = new ArrayList<>();
          Set<Integer> lineNumberOfPredCalls = new HashSet<>();
          findAllPredCalls(alloyProgram, func, predCalls, lineNumberOfPredCalls);
          RowSpan predRowSpan = new RowSpan(func.pos().y - 1, func.pos().y2 - 1);
          String textValuation = beforeSubstring(
              afterSubstring(alloyProgram.getLinesExcept(predRowSpan, lineNumberOfPredCalls), "{",
                  false), "}", true);
          try {
            Expr valuationExpr = CompUtil.parseOneExpression_fromString(testModule, textValuation);
            Command runPred = new Command(false, opt.getScope(), opt.getIntegerBitWidth(), -1,
                valuationExpr);
            A4Solution valuation = TranslateAlloyToKodkod
                .execute_command(A4Reporter.NOP, testModule.getAllReachableSigs(), runPred,
                    options);
            testSuite.append(
                convertA4SolutionToTestFact(valuation, predName, predCalls, relationArity,
                    sigsAndFields));
          } catch (Err err) {
            err.printStackTrace();
          }
        }
      }
    }
    return testSuite.toString();
  }

  /**
   * Remove the body of predicate if the predicate contains holes.
   */
  private static String removePredicateBody(AlloyProgram alloyProgram, CompModule module) {
    StringBuilder modelWithoutHoles = new StringBuilder();
    Browsable preds = findSubnode(module, "pred");
    if (preds != null) {
      List<RowSpan> rowSpansToDelete = new ArrayList<>();
      for (int i = 0; i < preds.getSubnodes().size(); i++) {
        Func pred = (Func) preds.getSubnodes().get(i);
        RowSpan predRowSpan = new RowSpan(pred.pos().y - 1, pred.pos().y2 - 1);
        for (Hole hole : alloyProgram.getHoles()) {
          if (predRowSpan.containsLine(hole.getLineNumber())) {
            rowSpansToDelete.add(predRowSpan);
            break;
          }
        }
      }
      int last_line = 0;
      for (RowSpan rowSpan : rowSpansToDelete) {
        modelWithoutHoles
            .append(alloyProgram.getLines(new RowSpan(last_line, rowSpan.getBegin() - 1)));
        modelWithoutHoles.append(beforeSubstring(alloyProgram.getLines(rowSpan), "{", false))
            .append("{}\n");
        last_line = rowSpan.getEnd() + 1;
      }
      modelWithoutHoles.append(
          alloyProgram.getLines(new RowSpan(last_line, alloyProgram.totalLineNumber() - 1)));
    }
    return modelWithoutHoles.toString();
  }

  private static void findAllPredCalls(AlloyProgram alloyProgram, Browsable node,
      List<PredCall> predCalls, Set<Integer> lineNumberOfPredCalls) {
    for (Browsable child : node.getSubnodes()) {
      String bold = extractHTML(child.getHTML(), Pattern.compile("<b>(.*?)</b>"));
      if (bold == null) {
        findAllPredCalls(alloyProgram, child, predCalls, lineNumberOfPredCalls);
      } else if (bold.equals("field") || bold.equals("sig")) {
        ;
      } else if (bold.equals("call")) {
        String s = extractHTML(node.getHTML(), Pattern.compile("(.*?) <i>"));
        boolean isNegation = s != null && s.equals("!");
        String predName = afterSubstring(
            extractHTML(child.getHTML(), Pattern.compile("</b> (.*?) <i>")), SLASH, true);
        int row = child.pos().y - 1;
        int col = child.pos().x2 - 1;
        // The line that contains the pred call.
        String line = alloyProgram.getLine(row);
        int openBracketIdx = line.indexOf("[", col);
        int closeBracketIdx = line.indexOf("]", col);
        String paraString = "";
        if (openBracketIdx != -1 && closeBracketIdx != -1) {
          paraString = line.substring(openBracketIdx + 1, closeBracketIdx).replaceAll("\\s", "");
        }
        List<String> parameterList =
            paraString.equals("") ? new ArrayList<>() : Arrays.asList(paraString.split(","));
        // Construct predicate calls.
        PredCall predCall = new PredCall(predName, parameterList, isNegation);
        predCalls.add(predCall);
        // Ignore the line in the future.
        lineNumberOfPredCalls.add(row);
      } else {
        findAllPredCalls(alloyProgram, child, predCalls, lineNumberOfPredCalls);
      }
    }
  }

  private static String convertA4SolutionToTestFact(A4Solution valuation, String testPredName,
      List<PredCall> predCalls, Map<String, Integer> relationArity, List<Relation> sigsAndFields)
      throws TestValuationUnsatisfiableException {
    if (!valuation.satisfiable()) {
      throw new TestValuationUnsatisfiableException(
          "The test valuation " + testPredName + " is unsatisfiable." +
              "  Please provide a valid test valuation.");
    }
    StringBuilder someDisjSigs = new StringBuilder();
    String someDisjPrefix = "";
    StringBuilder letsDecl = new StringBuilder();
    StringBuilder closeBraceForLetsDecl = new StringBuilder();
    String[] valuationInLines = valuation.toString().split("\n");
    // We need to first record the skolem in the original pred
    // invocation, e.g. Contains[List0, Object2], and then map
    // the skolem to the correct value.  E.g. if $List0 = List$0
    // and $Object2 = Object$0, then we should generate
    // Contains[List0, Object0].
    Map<String, String> skolemToValue = new HashMap<>();
    for (String line : valuationInLines) {
      if (!line.startsWith("skolem")) {
        continue;
      }
      line = line.replaceAll("\\$", "");
      Pattern pattern = Pattern.compile(".*?(\\S+)=\\{(\\S+)\\}$");
      Matcher matcher = pattern.matcher(line);
      if (matcher.find()) {
        skolemToValue.put(matcher.group(1), matcher.group(2));
      }
    }
    // Remove outer skolems.  For example, if we have some s: S | some s: S' | ...
    // where S extends S'.  Then the second s becomes s' by default in Alloy.  We only
    // want to use the inner most variable.
    Set<String> toRemove = new HashSet<>();
    for (String oldName : skolemToValue.keySet()) {
      if (skolemToValue.containsKey(oldName + "'")) {
        toRemove.add(oldName);
      }
    }
    Map<String, String> skolemToValueFinal = new HashMap<>();
    for (String oldName : skolemToValue.keySet()) {
      if (toRemove.contains(oldName)) {
        continue;
      }
      if (oldName.endsWith("'")) {
        skolemToValueFinal.put(oldName.replaceAll("'+$", ""), skolemToValue.get(oldName));
      } else {
        skolemToValueFinal.put(oldName, skolemToValue.get(oldName));
      }
    }
    for (String line : valuationInLines) {
      if (!line.startsWith("this")) {
        continue;
      }
      String[] pair = parseValuationLine(line);
      String name = pair[0];
      String valueString = pair[1].substring(1, pair[1].length() - 1);
      List<String> values = valueString.equals("") ? new ArrayList<>()
          : Arrays.asList(valueString.replaceAll("[\\$\\s]", "").split(","));
      boolean isSig = false;
      if (!line.contains("<:")) { // Sigs
        isSig = true;
        // Create some disj ... if the type is not empty.
        if (values.size() > 0) {
          someDisjSigs.append(someDisjPrefix).append("some disj ").append(String.join(", ", values))
              .append(": ").append(name);
          someDisjPrefix = " | ";
        }
      }
      valueString = "";
      if (values.size() == 0) {
        String prefix = "";
        for (int arity = 0; arity < relationArity.getOrDefault(name, 1); arity++) {
          valueString += prefix + "none";
          prefix = " -> ";
        }
      } else {
        valueString = String.join(" + ", values);
      }
      letsDecl.append("let ").append(name).append(isSig ? "s" : "").append(" = ")
          .append(valueString).append(" {\n");
      closeBraceForLetsDecl.append("}");
    }
    String restArgs = String.join(", ", sigsAndFields.stream().map(relation -> (
        relation.getTypes().size() == 1 && relation.getValue()
            .equals(relation.getTypes().get(0).getGenType()) ?
            relation.getValue() + "s" : relation.getValue())).collect(Collectors.toList()));
    StringBuilder invocations = new StringBuilder();
    for (PredCall predCall : predCalls) {
      predCall.updateParameters(skolemToValueFinal);
      if (predCall.isNegation()) {
        invocations.append("!");
      }
      invocations.append(predCall.getName()).append("[");
      if (predCall.getParameters().size() == 0) {
        invocations.append(restArgs);
      } else {
        invocations.append(String.join(", ", predCall.getParameters())).append(", ")
            .append(restArgs);
      }
      invocations.append("]\n");
    }
    String bigOpenBrace = " {\n";
    String bigCloseBrace = "}\n";
    if (someDisjSigs.toString().equals("")) {
      bigOpenBrace = "";
      bigCloseBrace = "";
    }
    return "fact " + testPredName + " {\n" + someDisjSigs.toString() + bigOpenBrace + letsDecl
        .toString() + invocations.toString() + closeBraceForLetsDecl.toString() + bigCloseBrace
        + "}\n\n";
  }

  private static String[] parseValuationLine(String line) {
    String l = afterSubstring(line, SLASH, false);
    String fieldDelimiter = "<:";
    int fieldDelimiterIdx = l.indexOf(fieldDelimiter);
    if (fieldDelimiterIdx != -1) {
      l = l.substring(fieldDelimiterIdx + fieldDelimiter.length());
    }
    String[] pair = l.split("=");
    return pair;
  }
}
