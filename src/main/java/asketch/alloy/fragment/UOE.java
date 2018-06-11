package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.UOE;
import static asketch.alloy.etc.Operators.CARET;
import static asketch.alloy.etc.Operators.STAR;
import static asketch.alloy.etc.Operators.TILDE;

import java.util.ArrayList;

/**
 * This class represents Unary Operator for Expression holes.
 */
public class UOE extends Hole {

  public UOE(String content, String name, int lineNumber, int begin, int end) {
    super(content, name, lineNumber, begin, end);
    this.cands = new ArrayList<>();
    this.cands.add(TILDE);
    this.cands.add(STAR);
    this.cands.add(CARET);
  }

  @Override
  public void resetContent() {
    setContent(UOE);
  }
}
