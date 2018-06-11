package asketch.compiler.ast;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class OptionalNode implements Node {

  private Node subNode;

  public OptionalNode(Node subNode) {
    this.subNode = subNode;
  }

  public Node getSubNode() {
    return subNode;
  }

  @Override
  public String toString() {
    return subNode.toString() + "?";
  }

  @Override
  public Collection<String> alternatives() {
    Set<String> set = new HashSet<>(subNode.alternatives());
    set.add("");
    return set;
  }
}
