package asketch.compiler;

import asketch.alloy.cand.Candidate;
import asketch.compiler.ast.Node;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

public class Compiler {

  public List<Candidate> compile(String inputString) {
    CharStream input = CharStreams.fromString(inputString);
    RegExprLexer lexer = new RegExprLexer(input);
    TokenStream tokens = new CommonTokenStream(lexer);
    RegExprParser parser = new RegExprParser(tokens);

    TreeBuilder treeBuilder = new TreeBuilder();
    parser.addParseListener(treeBuilder);
    parser.setErrorHandler(new ErrorHandler());

    parser.program();

    Node root = treeBuilder.getRoot();
    // Treat special character epsilon as empty string.
    return root.alternatives().stream().map(s -> s.replaceAll("ɛ", ""))
        .map(Candidate::new)
        .collect(Collectors.toList());
  }
}