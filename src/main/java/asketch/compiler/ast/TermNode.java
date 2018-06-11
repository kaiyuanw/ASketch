package asketch.compiler.ast;

import java.util.Collection;
import java.util.Collections;

public class TermNode implements Node {

  private String value;

  public TermNode(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public Collection<String> alternatives() {
    return Collections.singletonList(value.replaceAll("\\\\", ""));
  }
}
