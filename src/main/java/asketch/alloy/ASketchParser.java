package asketch.alloy;

import static asketch.alloy.etc.Constants.UNIV;
import static asketch.util.StringUtil.beforeSubstring;

import asketch.alloy.cand.Relation;
import asketch.alloy.exception.HoleParsingException;
import asketch.alloy.fragment.E;
import asketch.alloy.fragment.Hole;
import asketch.alloy.util.AlloyProgram;
import java.util.Arrays;
import java.util.List;

public class ASketchParser {

  public static AlloyProgram parse(String modelText) throws HoleParsingException {
    AlloyProgram program = new AlloyProgram();
    String[] lines = modelText.split("\n");
    for (int i = 0; i < lines.length; i++) {
      if (lines[i].trim().contains("//")) {
        lines[i] = beforeSubstring(lines[i], "//", false);
      }
      if (lines[i].trim().contains("--")) {
        lines[i] = beforeSubstring(lines[i], "--", false);
      }
      int begin = 0;
      int lastEnd = 0;
      while (begin < lines[i].length()) {
        if (lines[i].charAt(begin) == '\\') {
          int end = begin + 1;
          while (end < lines[i].length()) {
            if (lines[i].charAt(end) == '\\') {
              program.addFragment(lines[i].substring(lastEnd, begin), i);
              program.addHole(lines[i].substring(begin, end + 1), i, begin, end + 1);
              lastEnd = end + 1;
              begin = end + 1;
              break;
            } else {
              end += 1;
            }
          }
          continue;
        }
        begin += 1;
      }
      program.addFragment(lines[i].substring(lastEnd), i);
    }
    // Add candidate sigs to each expression hole
    List<Relation> primSigs = Arrays
        .asList(new Relation(UNIV, 1), new Relation("(" + UNIV + "->" + UNIV + ")", 2),
            new Relation("(" + UNIV + "->" + UNIV + "->" + UNIV + ")", 3), new Relation("0", 1));
    for (Hole hole : program.getHoles()) {
      if (!(hole instanceof E)) {
        continue;
      }
      E exprHole = (E) hole;
      exprHole.addCandidates(primSigs);
    }
    return program;
  }
}
