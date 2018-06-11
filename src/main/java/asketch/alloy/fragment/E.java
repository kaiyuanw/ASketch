package asketch.alloy.fragment;

import static asketch.alloy.etc.Constants.E;

import java.util.ArrayList;

/**
 * This class represents Expression holes.
 */
public class E extends Hole {

  public E(String content, String name, int lineNumber, int begin, int end) {
    super(content, name, lineNumber, begin, end);
    this.cands = new ArrayList<>();
  }

  @Override
  public void resetContent() {
    setContent(E);
  }
}
