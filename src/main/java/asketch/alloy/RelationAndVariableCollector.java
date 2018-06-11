package asketch.alloy;

import static asketch.alloy.util.AlloyUtil.compileAlloyModule;
import static asketch.alloy.util.AlloyUtil.findParametersAndVariables;
import static asketch.alloy.util.AlloyUtil.findSigsAndFields;
import static asketch.etc.Names.CHECK_FILE_PATH;

import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.Relation;
import asketch.alloy.exception.AlloySyntaxErrorException;
import asketch.alloy.fragment.E;
import asketch.alloy.fragment.Hole;
import asketch.alloy.util.AlloyProgram;
import asketch.util.TextFileWriter;
import edu.mit.csail.sdg.parser.CompModule;
import java.util.List;
import java.util.Map;

/**
 * This class is used to fill concrete value in holes and invoke Alloy analyzer to parse the model
 * to collect signatures and relations in the model.
 */
public class RelationAndVariableCollector {

  public static CompModule collect(AlloyProgram alloyProgram) throws AlloySyntaxErrorException {
    return collect(alloyProgram, null);
  }

  public static CompModule collect(AlloyProgram alloyProgram, Map<String, String> inheritanceMap)
      throws AlloySyntaxErrorException {
    CompModule module = sketchHoles(alloyProgram, 0);
    // If ASketch cannot make Alloy module compile, report error
    if (module == null) {
      throw new AlloySyntaxErrorException(
          "ASketch cannot handle the model." +
              "  The model might contains syntax errors besides hole notations."
      );
    }
    // Find all relations
    List<Relation> relations = findSigsAndFields(module, inheritanceMap);

    // At this point the program compiles, so we remove candidate expressions
    // associated with expression holes and try to add relations (including sigs)
    // in a better way.
    for (Hole hole : alloyProgram.getHoles()) {
      if (!(hole instanceof E)) {
        continue;
      }
      E exprHole = (E) hole;
      exprHole.removeAllCands();
      exprHole.addPrimaryRelations(relations);
    }

    // Find all parameters and variables, and add them to related expression holes
    findParametersAndVariables(alloyProgram, module, "pred");
    findParametersAndVariables(alloyProgram, module, "fun");
    findParametersAndVariables(alloyProgram, module, "fact");
    findParametersAndVariables(alloyProgram, module, "assert");

    return module;
  }

  /**
   * Recursively fill holes with concrete values until the model compiles.  Return true if it
   * compiles, false otherwise.
   */
  private static CompModule sketchHoles(AlloyProgram alloyProgram, int depth) {
    if (depth == alloyProgram.getHoles().size()) {
      String program = alloyProgram.toString();
      TextFileWriter.writeText(program, CHECK_FILE_PATH, false);
      return compileAlloyModule(CHECK_FILE_PATH);
    }
    Hole hole = alloyProgram.getHoles().get(depth);
    for (Candidate cand : hole.getCands()) {
      hole.setContent(cand.getValue());
      CompModule module = sketchHoles(alloyProgram, depth + 1);
      if (module != null) {
        return module;
      }
    }
    return null;
  }
}
