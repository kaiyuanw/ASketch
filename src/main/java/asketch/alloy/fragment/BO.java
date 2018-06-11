package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.BO;
import static asketch.alloy.etc.Operators.AMP;
import static asketch.alloy.etc.Operators.MINUS;
import static asketch.alloy.etc.Operators.PLUS;

import java.util.ArrayList;

/**
 * This class represents Binary Operator holes
 */
public class BO extends Hole {

  public BO(String content, String name, int lineNumber, int begin, int end) {
    super(content, name, lineNumber, begin, end);
    this.cands = new ArrayList<>();
    this.cands.add(AMP);
    this.cands.add(PLUS);
    this.cands.add(MINUS);
  }

  @Override
  public void resetContent() {
    setContent(BO);
  }
}
