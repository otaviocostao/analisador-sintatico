import java.io.PrintWriter;
import java.util.List;

public class HTMLGenerator {
    public static void export(List<Token> tokens, String nomeArquivo) {
        try (PrintWriter out = new PrintWriter(nomeArquivo)) {
            out.println("<html><head><style>");
            out.println("table {width: 100%; border-collapse: collapse;}");
            out.println("th, td {border: 1px solid #444; padding: 10px; text-align: left;}");
            out.println("th {background-color: #f2f2f2;}");
            out.println("</style></head><body>");
            out.println("<h1>Symbols Table - Lexer Analyser</h1>");
            out.println("<table><tr><th>Type</th><th>Lexeme</th><th>Line</th></tr>");

            for (Token t : tokens) {
                if (t.type != TokenType.EOF) {
                    out.println("<tr>");
                    out.println("<td>" + t.type + "</td>");
                    out.println("<td>" + t.lexeme.replace("<", "&lt;").replace(">", "&gt;") + "</td>");
                    out.println("<td>" + t.line + "</td>");
                    out.println("</tr>");
                }
            }

            out.println("</table></body></html>");
        } catch (Exception e) {
            System.err.println("Error generating the HTML: " + e.getMessage());
        }
    }
}