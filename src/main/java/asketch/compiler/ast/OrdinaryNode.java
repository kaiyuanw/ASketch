package asketch.compiler.ast;

import java.util.Collection;

public class OrdinaryNode implements Node {

  private Node subNode;

  public OrdinaryNode(Node subNode) {
    this.subNode = subNode;
  }

  public Node getSubNode() {
    return subNode;
  }

  @Override
  public String toString() {
    return "(" + subNode.toString() + ")";
  }

  @Override
  public Collection<String> alternatives() {
    return subNode.alternatives();
  }
}
