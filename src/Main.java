import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] filesToTest = {
                "code.txt"
        };

        Lexer lexer = new Lexer();

        for (String fileName : filesToTest) {
            try {
                System.out.println("==================================================");
                System.out.println("ANALISANDO ARQUIVO: " + fileName);
                System.out.println("==================================================");

                String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(fileName)));
                List<Token> tokens = lexer.scan(content);

                HTMLGenerator.export(tokens, "table_" + fileName + ".html");

                System.out.println("\nTABELA DE TOKENS:");
                System.out.format("%-22s %-14s %s\n", "Tipo", "Valor", "Linha");
                System.out.println("──────────────────────────────────────────────────");
                for (Token t : tokens) {
                    System.out.format("%-22s %-14s %s\n", t.type, t.lexeme, t.line);
                }

                System.out.println("\nANÁLISE SINTÁTICA LL(1):");
                Parser.parse(tokens, "ast_" + fileName.replace(".txt", "") + ".json");

            } catch (Exception e) {
                System.err.println("Error: Please check if the input file exists. Details: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}