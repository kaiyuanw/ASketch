package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.UO;
import static asketch.alloy.etc.Operators.LONE;
import static asketch.alloy.etc.Operators.NO;
import static asketch.alloy.etc.Operators.ONE;
import static asketch.alloy.etc.Operators.SOME;

import java.util.ArrayList;

/**
 * This class represents Unary Operator holes
 */
public class UO extends Hole {

  public UO(String content, String name, int lineNumber, int begin, int end) {
    super(content, name, lineNumber, begin, end);
    this.cands = new ArrayList<>();
    this.cands.add(NO);
    this.cands.add(SOME);
    this.cands.add(LONE);
    this.cands.add(ONE);
  }

  @Override
  public void resetContent() {
    setContent(UO);
  }
}
