package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.SQ;
import static asketch.alloy.etc.Operators.EMPTY;
import static asketch.alloy.etc.Operators.LONE;
import static asketch.alloy.etc.Operators.ONE;
import static asketch.alloy.etc.Operators.SOME;

import java.util.ArrayList;

/**
 * This class represents Signature Quantifier holes.
 */
public class SQ extends Hole {

  public SQ(String content, String name, int lineNumber, int begin, int end) {
    super(content, name, lineNumber, begin, end);
    this.cands = new ArrayList<>();
    this.cands.add(LONE);
    this.cands.add(ONE);
    this.cands.add(SOME);
    this.cands.add(EMPTY);
  }

  @Override
  public void resetContent() {
    setContent(SQ);
  }
}
