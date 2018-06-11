package asketch.alloy.etc;

/**
 * This class is used to represent the begin row and end row of an Alloy fragment. It is mainly used
 * to locate part of the Alloy model, e.g. a sig declaration, or pred declaration.
 */
public class RowSpan {

  private int begin;
  private int end;

  public RowSpan(int begin, int end) {
    this.begin = begin;
    this.end = end;
  }

  public boolean containsLine(int lineNumber) {
    return begin <= lineNumber && lineNumber <= end;
  }

  public int getBegin() {
    return begin;
  }

  public int getEnd() {
    return end;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof RowSpan) {
      RowSpan that = (RowSpan) obj;
      return this.begin == that.begin && this.end == that.end;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return begin * 11 + end * 17;
  }
}
