import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import ast.VisitorImpl;
import ast.node.Program;

public class MySmoola {
  static String SMOOLA_CODE_FILE = "in.sml";

  public static void main(String[] args) throws IOException {
    if (args.length > 0) {
      SMOOLA_CODE_FILE = args[0];
    }
    CharStream reader = CharStreams.fromFileName(SMOOLA_CODE_FILE);
    SmoolaLexer lexer = new SmoolaLexer(reader);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    SmoolaParser parser = new SmoolaParser(tokens);
    Program p = parser.program().p;

    VisitorImpl vis = new VisitorImpl();
    try {
      p.accept(vis);
    } catch (StackOverflowError sofe) {
      System.out.println("STACK OVERFLOW EXCEPTION");
    }
    vis.show();
  }
}