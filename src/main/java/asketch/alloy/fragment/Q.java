package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.Q;
import static asketch.alloy.etc.Operators.ALL;
import static asketch.alloy.etc.Operators.LONE;
import static asketch.alloy.etc.Operators.NO;
import static asketch.alloy.etc.Operators.ONE;
import static asketch.alloy.etc.Operators.SOME;

import java.util.ArrayList;

/**
 * This class represents Quantifier holes.
 */
public class Q extends Hole {

  public Q(String content, String name, int lineNumber, int begin, int end) {
    super(content, name, lineNumber, begin, end);
    this.cands = new ArrayList<>();
    this.cands.add(ALL);
    this.cands.add(NO);
    this.cands.add(SOME);
    this.cands.add(LONE);
    this.cands.add(ONE);
  }

  @Override
  public void resetContent() {
    setContent(Q);
  }
}
