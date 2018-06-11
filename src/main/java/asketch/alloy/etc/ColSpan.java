package asketch.alloy.etc;

/**
 * This class represents the begin column and end column of an AlloyFragment, start inclusive and
 * end exclusive.
 */
public class ColSpan {

  private int begin;
  private int end;

  public ColSpan(int begin, int end) {
    this.begin = begin;
    this.end = end;
  }

  public ColSpan() {
    this.begin = 0;
    this.end = 0;
  }

  public int getBegin() {
    return begin;
  }

  public void setBegin(int begin) {
    this.begin = begin;
  }

  public int getEnd() {
    return end;
  }

  public void setEnd(int end) {
    this.end = end;
  }

  @Override
  public String toString() {
    return "<from: " + begin + ", to: " + end + ">";
  }
}
