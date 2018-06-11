package asketch.compiler.ast;

import java.util.Collection;
import java.util.Deque;
import java.util.stream.Collectors;

public class OrNode implements Node {

  private Deque<Node> subNodes;

  public OrNode(Deque<Node> subNodes) {
    this.subNodes = subNodes;
  }

  public Deque<Node> getSubNodes() {
    return subNodes;
  }

  @Override
  public String toString() {
    return String.join("|", subNodes.stream().map(Object::toString).collect(Collectors.toList()));
  }

  @Override
  public Collection<String> alternatives() {
    return subNodes.stream()
        .map(Node::alternatives)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }
}
