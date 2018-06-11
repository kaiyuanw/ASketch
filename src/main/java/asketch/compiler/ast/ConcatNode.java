package asketch.compiler.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class ConcatNode implements Node {

  private Deque<Node> subNodes;

  public ConcatNode(Deque<Node> subNodes) {
    this.subNodes = subNodes;
  }

  public Deque<Node> getSubNodes() {
    return subNodes;
  }

  @Override
  public String toString() {
    return String.join("", subNodes.stream().map(Object::toString).collect(Collectors.toList()));
  }

  @Override
  public Collection<String> alternatives() {
    List<Collection<String>> listOfAlternatives = subNodes.stream()
        .map(Node::alternatives).collect(Collectors.toList());
    List<String> res = new ArrayList<>();
    backtrack(res, new Stack<>(), listOfAlternatives, 0);
    return res;
  }

  private void backtrack(List<String> res, Stack<String> path,
      List<Collection<String>> listOfAlternatives, int start) {
    if (start == listOfAlternatives.size()) {
      res.add(String.join("", path));
      return;
    }
    for (String alternative : listOfAlternatives.get(start)) {
      path.push(alternative);
      backtrack(res, path, listOfAlternatives, start + 1);
      path.pop();
    }
  }
}
