package asketch.alloy.util;

import static asketch.alloy.etc.Constants.BO;
import static asketch.alloy.etc.Constants.CO;
import static asketch.alloy.etc.Constants.E;
import static asketch.alloy.etc.Constants.LO;
import static asketch.alloy.etc.Constants.Q;
import static asketch.alloy.etc.Constants.SQ;
import static asketch.alloy.etc.Constants.UO;
import static asketch.alloy.etc.Constants.UOE;
import static asketch.alloy.etc.Constants.UOF;

import asketch.alloy.etc.ColSpan;
import asketch.alloy.etc.RowSpan;
import asketch.alloy.exception.HoleParsingException;
import asketch.alloy.fragment.AlloyFragment;
import asketch.alloy.fragment.BO;
import asketch.alloy.fragment.CO;
import asketch.alloy.fragment.E;
import asketch.alloy.fragment.Hole;
import asketch.alloy.fragment.LO;
import asketch.alloy.fragment.Q;
import asketch.alloy.fragment.SQ;
import asketch.alloy.fragment.UO;
import asketch.alloy.fragment.UOE;
import asketch.alloy.fragment.UOF;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class represents an Alloy program with holes
 */
public class AlloyProgram {

  /**
   * Represent Alloy model with holes.
   */
  private List<List<AlloyFragment>> program;
  /**
   * Store holes in the order they appear for later use.
   */
  private List<Hole> holes;

  public AlloyProgram() {
    this.program = new ArrayList<>();
    this.holes = new ArrayList<>();
  }

  public void computeHoleColSpan() {
    for (Hole hole : holes) {
      int start = 0;
      for (AlloyFragment fragment : program.get(hole.getLineNumber())) {
        if (fragment == hole) {
          hole.setColSpan(new ColSpan(start, start + hole.getContent().length()));
          break;
        }
        start += fragment.getContent().length();
      }
    }
  }

  public String getLine(Integer lineNumber) {
    StringBuilder sb = new StringBuilder();
    for (AlloyFragment fragment : program.get(lineNumber)) {
      sb.append(fragment.toString());
    }
    return sb.toString();
  }

  public String getLines(RowSpan rs) {
    assert rs.getBegin() <= rs.getEnd();
    StringBuilder sb = new StringBuilder();
    for (int i = rs.getBegin(); i <= rs.getEnd(); i++) {
      sb.append(getLine(i)).append("\n");
    }
    return sb.toString();
  }

  public String getLinesExcept(RowSpan rs, Collection<Integer> collection) {
    assert rs.getBegin() <= rs.getEnd();
    StringBuilder sb = new StringBuilder();
    for (int i = rs.getBegin(); i <= rs.getEnd(); i++) {
      if (collection.contains(i)) {
        continue;
      }
      sb.append(getLine(i)).append("\n");
    }
    return sb.toString();
  }

  public void addFragment(String content, Integer lineNumber) {
    AlloyFragment n = new AlloyFragment(content, lineNumber);
    if (lineNumber >= program.size()) { // Add new line
      program.add(new ArrayList<>());
    }
    // Add new fragment
    program.get(lineNumber).add(n);
  }

  public void addHole(String content, Integer lineNumber, int begin, int end)
      throws HoleParsingException {
    Hole hole = createHole(content, lineNumber, begin, end);
    if (lineNumber >= program.size()) { // Add new line
      program.add(new ArrayList<>());
    }
    List<AlloyFragment> fragments = program.get(lineNumber);
    for (int i = fragments.size() - 1; i >= 0; i--) {
      AlloyFragment prev = fragments.get(i);
      if (prev.getContent().trim().length() > 0) { // Non-empty fragment
        prev.setNext(hole);
        hole.setPrev(prev);
        break;
      }
    }
    fragments.add(hole);
    // Store the hole to fill in concrete value
    holes.add(hole);
  }

  private Hole createHole(String sketchTypeAndName, Integer lineNumber, Integer begin, Integer end)
      throws HoleParsingException {
    String[] typeAndName = sketchTypeAndName.substring(1, sketchTypeAndName.length() - 1)
        .split(",");
    String sketchType = typeAndName[0];
    String sketchName = typeAndName[1];
    switch (sketchType) {
      case SQ:
        return new SQ(sketchType, sketchName, lineNumber, begin, end);
      case Q:
        return new Q(sketchType, sketchName, lineNumber, begin, end);
      case LO:
        return new LO(sketchType, sketchName, lineNumber, begin, end);
      case CO:
        return new CO(sketchType, sketchName, lineNumber, begin, end);
      case UO:
        return new UO(sketchType, sketchName, lineNumber, begin, end);
      case UOF:
        return new UOF(sketchType, sketchName, lineNumber, begin, end);
      case UOE:
        return new UOE(sketchType, sketchName, lineNumber, begin, end);
      case BO:
        return new BO(sketchType, sketchName, lineNumber, begin, end);
      case E:
        return new E(sketchType, sketchName, lineNumber, begin, end);
      default:
        throw new HoleParsingException(sketchType + " is not supported.");
    }
  }

  public List<Hole> getHoles() {
    return holes;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < program.size(); i++) {
      List<AlloyFragment> fragments = program.get(i);
      for (int j = 0; j < fragments.size(); j++) {
        sb.append(fragments.get(j).toString());
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  public int totalLineNumber() {
    return program.size();
  }
}
