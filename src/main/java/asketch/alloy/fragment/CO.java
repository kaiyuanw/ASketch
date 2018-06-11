package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.CO;
import static asketch.alloy.etc.Operators.EQ;
import static asketch.alloy.etc.Operators.IN;
import static asketch.alloy.etc.Operators.NEQ;
import static asketch.alloy.etc.Operators.NIN;

import java.util.ArrayList;

/**
 * This class represents Compare Operator holes.
 */
public class CO extends Hole {

  public CO(String content, String name, int lineNumber, int begin, int end) {
    super(content, name, lineNumber, begin, end);
    this.cands = new ArrayList<>();
    this.cands.add(EQ);
    this.cands.add(IN);
    this.cands.add(NEQ);
    this.cands.add(NIN);
  }

  @Override
  public void resetContent() {
    setContent(CO);
  }
}
