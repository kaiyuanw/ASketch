package asketch.compiler;

import static asketch.opts.DefaultOptions.logger;

import asketch.compiler.ast.ConcatNode;
import asketch.compiler.ast.Node;
import asketch.compiler.ast.OptionalNode;
import asketch.compiler.ast.OrNode;
import asketch.compiler.ast.OrdinaryNode;
import asketch.compiler.ast.TermNode;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class TreeBuilder extends RegExprBaseListener {

  private String lastTerminal;
  private Stack<Node> startOrNodes;
  private Stack<Node> startConcatNodes;
  private Stack<Node> exprStack;
  private Node root;

  public TreeBuilder() {
    this.lastTerminal = null;
    this.startOrNodes = new Stack<>();
    this.startConcatNodes = new Stack<>();
    this.exprStack = new Stack<>();
  }

  public Node getRoot() {
    return root;
  }

  @Override
  public void enterRoot(RegExprParser.RootContext ctx) {
    logger.debug("Enter Root");
  }

  @Override
  public void exitRoot(RegExprParser.RootContext ctx) {
    logger.debug("Exit Root");
    root = exprStack.pop();
    assert exprStack.isEmpty();
  }

  @Override
  public void enterOptionalNode(RegExprParser.OptionalNodeContext ctx) {
    logger.debug("Enter Optional Node");
  }

  @Override
  public void exitOptionalNode(RegExprParser.OptionalNodeContext ctx) {
    logger.debug("Exit Optional Node");
    exprStack.push(new OptionalNode(exprStack.pop()));
  }

  @Override
  public void enterConcatNode(RegExprParser.ConcatNodeContext ctx) {
    logger.debug("Enter Concat Node");
    startConcatNodes.push(exprStack.peek());
  }

  @Override
  public void exitConcatNode(RegExprParser.ConcatNodeContext ctx) {
    logger.debug("Exit Concat Node");
    Deque<Node> queue = new LinkedList<>();
    Node stopNode = startConcatNodes.pop();
    boolean stop;
    do {
      stop = exprStack.peek() == stopNode;
      Node topNode = exprStack.pop();
      if (topNode instanceof ConcatNode) {
        Deque<Node> subNodes = ((ConcatNode) topNode).getSubNodes();
        while (!subNodes.isEmpty()) {
          queue.addFirst(subNodes.removeLast());
        }
      } else {
        queue.addFirst(topNode);
      }
    } while (!stop);
    exprStack.push(new ConcatNode(queue));
  }

  @Override
  public void enterTermNode(RegExprParser.TermNodeContext ctx) {
    logger.debug("Enter Term Node");
  }

  @Override
  public void exitTermNode(RegExprParser.TermNodeContext ctx) {
    logger.debug("Exit Term Node");
    exprStack.push(new TermNode(lastTerminal));
  }

  @Override
  public void enterOrdinaryNode(RegExprParser.OrdinaryNodeContext ctx) {
    logger.debug("Enter Ordinary Node");
  }

  @Override
  public void exitOrdinaryNode(RegExprParser.OrdinaryNodeContext ctx) {
    logger.debug("Exit Ordinary Node");
    // We can ignore processing ordinary node so it can potentially be flattened with other nodes.
    exprStack.push(new OrdinaryNode(exprStack.pop()));
  }

  @Override
  public void enterOrNode(RegExprParser.OrNodeContext ctx) {
    logger.debug("Enter Or Node");
    startOrNodes.push(exprStack.peek());
  }

  @Override
  public void exitOrNode(RegExprParser.OrNodeContext ctx) {
    logger.debug("Exit Or Node");
    Deque<Node> queue = new LinkedList<>();
    Node stopNode = startOrNodes.pop();
    boolean stop;
    do {
      stop = exprStack.peek() == stopNode;
      Node topNode = exprStack.pop();
      if (topNode instanceof OrNode) {
        Deque<Node> subNodes = ((OrNode) topNode).getSubNodes();
        while (!subNodes.isEmpty()) {
          queue.addFirst(subNodes.removeLast());
        }
      } else {
        queue.addFirst(topNode);
      }
    } while (!stop);
    exprStack.push(new OrNode(queue));
  }

  @Override
  public void visitTerminal(TerminalNode node) {
    logger.debug("Terminal: " + node.getText());
    lastTerminal = node.getText();
  }

  @Override
  public void visitErrorNode(ErrorNode node) {
    logger.debug("Error Node: " + node.getText());
  }
}
