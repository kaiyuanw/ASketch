package asketch.opts;

import static asketch.etc.Names.CHECK_FILE_PATH;
import static asketch.etc.Names.COLON_EQUAL;
import static asketch.etc.Names.NEW_LINE;
import static asketch.util.StringUtil.cutStringByDelimiter;
import static asketch.util.StringUtil.extractPattern;

import asketch.alloy.cand.Candidate;
import asketch.alloy.util.AlloyUtil;
import asketch.compiler.Compiler;
import asketch.util.FileUtil;
import asketch.util.TextFileWriter;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.A4Tuple;
import edu.mit.csail.sdg.translator.A4TupleSet;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import parser.ast.nodes.ModelUnit;
import parser.ast.visitor.PrettyStringVisitor;
import parser.etc.Names;

public class ASketchOpt {

  private String modelPath;
  private String testPath;
  private Map<String, List<Candidate>> varToExprs;
  private Map<String, Integer> varToArity;
  private int scope;
  private int solNum;
  private int integerBitWidth;
  private A4Options options;

  public ASketchOpt(String modelPath, String fragmentPath, String testPath, int scope, int solNum) {
    this.modelPath = modelPath;
    this.varToExprs = readVariableToExpressionMapping(fragmentPath);
    this.testPath = testPath;
    this.scope = scope;
    // Set default integer bit width to 5.
    this.integerBitWidth = 5;
    this.solNum = solNum;
    this.options = new A4Options();
  }

  private Map<String, List<Candidate>> readVariableToExpressionMapping(String exprPath) {
    String[] lines = FileUtil.readText(exprPath).split(NEW_LINE);
    Compiler compiler = new Compiler();
    Map<String, List<Candidate>> varToExprs = new HashMap<>();
    for (String line : lines) {
      String[] pair = cutStringByDelimiter(line, COLON_EQUAL);
      String regexRepr = extractPattern(pair[1], "\\{\\|\\s*(.*)\\s*\\|\\}");
      varToExprs.put(pair[0], compiler.compile(regexRepr));
    }
    return varToExprs;
  }

  public void computeVariableArity(CompModule module) {
    this.varToArity = new HashMap<>();
    ModelUnit modelUnit = new ModelUnit(null, module);
    PrettyStringVisitor psv = new PrettyStringVisitor() {
      @Override
      public String visit(ModelUnit n, Object arg) {
        String moduleDecl = n.getModuleDecl().accept(this, arg);
        String openDecls = String.join(Names.NEW_LINE,
            n.getOpenDeclList().stream().map(openDecl -> openDecl.accept(this, arg))
                .collect(Collectors.toList()));
        String sigDecls = String.join(Names.NEW_LINE,
            n.getSigDeclList().stream().map(signature -> signature.accept(this, arg))
                .collect(Collectors.toList()));
        return String.join(Names.NEW_LINE,
            Arrays.<CharSequence>asList(moduleDecl, openDecls, sigDecls, "run {} for 3"));
      }
    };
    TextFileWriter.writeText(modelUnit.accept(psv, null), CHECK_FILE_PATH, false);
    CompModule simpleModule = AlloyUtil.compileAlloyModule(CHECK_FILE_PATH);
    assert simpleModule != null;
    Command runEmpty = simpleModule.getAllCommands().get(0);
    try {
      A4Solution valuation = TranslateAlloyToKodkod
          .execute_command(A4Reporter.NOP, simpleModule.getAllReachableSigs(), runEmpty, options);
      varToExprs.forEach((varName, candidates) -> {
        try {
          Object obj = valuation.eval(
              CompUtil.parseOneExpression_fromString(simpleModule, candidates.get(0).getValue()));
          if (obj instanceof A4TupleSet) {
            varToArity.put(varName, ((A4TupleSet) obj).arity());
          } else { // Integer and Boolean have arity 1.
            varToArity.put(varName, 1);
          }
        } catch (Err err) {
          // Cannot parse candidates for operator holes or the candidate value contains local variable.
          // In the latter case, we assume an arity of 1.  This is really a hack.  Should use type analysis in the future.
          varToArity.put(varName, 1);
        }
      });
    } catch (Err err) {
      err.printStackTrace();
    }
  }

  public Map<String, List<Candidate>> getVarToExprs() {
    return varToExprs;
  }

  public Map<String, Integer> getVarToArity() {
    return varToArity;
  }

  public String getModelPath() {
    return modelPath;
  }

  public String getTestPath() {
    return testPath;
  }

  public int getScope() {
    return scope;
  }

  public int getIntegerBitWidth() {
    return integerBitWidth;
  }

  public int getSolNum() {
    return solNum;
  }

  public A4Options getA4Options() {
    return options;
  }
}
