package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.UOF;
import static asketch.alloy.etc.Operators.EMPTY;
import static asketch.alloy.etc.Operators.NOT;

import java.util.ArrayList;

/**
 * This class represents Unary Operator for Formula holes.
 */
public class UOF extends Hole {

  public UOF(String content, String name, int lineNumber, int begin, int end) {
    super(content, name, lineNumber, begin, end);
    this.cands = new ArrayList<>();
    this.cands.add(NOT);
    this.cands.add(EMPTY);
  }

  @Override
  public void resetContent() {
    setContent(UOF);
  }
}
