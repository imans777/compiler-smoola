import java.io.IOException;

import org.antlr.v4.runtime.*;

import ast.VisitorImpl;
import ast.VisitorJasmin;
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
    } catch (Exception e) {
      System.out.println("A VERY BAD ERROR OCCURRED: " + e.toString());
    }
    vis.show();

    if (vis.hasError()) {
      System.out.println("-> Fix Smoola code errors first before Java Bytecode production.\n");
      return;
    }

    VisitorJasmin jh = new VisitorJasmin(vis);
    p.accept(jh);

    // not run the app (from "run.sh")
  }
}