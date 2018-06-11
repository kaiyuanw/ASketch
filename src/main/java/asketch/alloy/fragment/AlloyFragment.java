package asketch.alloy.fragment;

/**
 * This class represents an Alloy fragment, it is used to separate holes with the rest of program
 */
public class AlloyFragment {

  protected String content;
  protected int lineNumber;
  // Previous fragment
  protected AlloyFragment prev;
  // Next fragment
  protected AlloyFragment next;

  public AlloyFragment(String content, int lineNumber) {
    this.content = content;
    this.lineNumber = lineNumber;
    this.prev = null;
    this.next = null;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  public AlloyFragment getPrev() {
    return prev;
  }

  public void setPrev(AlloyFragment prev) {
    this.prev = prev;
  }

  public AlloyFragment getNext() {
    return next;
  }

  public void setNext(AlloyFragment next) {
    this.next = next;
  }

  @Override
  public String toString() {
    return content;
  }
}
