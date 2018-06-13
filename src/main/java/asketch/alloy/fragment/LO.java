package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.LO;
import static asketch.alloy.etc.Operators.AND;
import static asketch.alloy.etc.Operators.BIIMPLY;
import static asketch.alloy.etc.Operators.IMPLY;
import static asketch.alloy.etc.Operators.OR;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents Logic Operator holes.
 */
public class LO extends Hole {

  public LO(String content, String name, int lineNumber, int begin, int end) {
    super(content, name, lineNumber, begin, end);
    this.cands = new ArrayList<>();
    this.cands.add(OR);
    this.cands.add(AND);
    this.cands.add(BIIMPLY);
    this.cands.add(IMPLY);
  }

  @Override
  public void resetContent() {
    setContent(LO);
  }
}
