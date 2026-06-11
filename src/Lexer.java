import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private static class TokenRule {
        final Pattern pattern;
        final TokenType type;

        TokenRule(String regex, TokenType type) {
            this.pattern = Pattern.compile("^(" + regex + ")");
            this.type = type;
        }
    }

    private final List<TokenRule> rules = new ArrayList<>();

    public Lexer() {
        addRule("//.*", null);
        addRule("\\s+", null);

        addRule("int\\b", TokenType.INT);
        addRule("float\\b", TokenType.FLOAT);
        addRule("string\\b", TokenType.STRING);
        addRule("if\\b", TokenType.IF);
        addRule("else\\b", TokenType.ELSE);
        addRule("while\\b", TokenType.WHILE);
        addRule("for\\b", TokenType.FOR);
        addRule("switch\\b", TokenType.SWITCH);
        addRule("case\\b", TokenType.CASE);
        addRule("default\\b", TokenType.DEFAULT);
        addRule("func\\b", TokenType.FUNC);
        addRule("proc\\b", TokenType.PROC);
        addRule("return\\b", TokenType.RETURN);
        addRule("break\\b", TokenType.BREAK);
        addRule("continue\\b", TokenType.CONTINUE);

        addRule("==", TokenType.EQUAL_EQUAL);
        addRule("!=", TokenType.NOT_EQUAL);
        addRule(">=", TokenType.GREATER_EQUAL);
        addRule("<=", TokenType.LESS_EQUAL);
        addRule(">", TokenType.GREATER);
        addRule("<", TokenType.LESS);

        addRule("&&", TokenType.AND);
        addRule("\\|\\|", TokenType.OR);
        addRule("!", TokenType.NOT);
        addRule("\\+", TokenType.PLUS);
        addRule("-", TokenType.MINUS);
        addRule("\\*", TokenType.MULTIPLY);
        addRule("/", TokenType.DIVIDE);
        addRule("%", TokenType.MODULO);
        addRule("=", TokenType.ASSIGN);

        addRule("\\(", TokenType.LPAREN);
        addRule("\\)", TokenType.RPAREN);
        addRule("\\{", TokenType.LBRACE);
        addRule("\\}", TokenType.RBRACE);
        addRule(";", TokenType.SEMICOLON);
        addRule(",", TokenType.COMMA);
        addRule(":", TokenType.COLON);

        addRule("\"[^\"]*\"", TokenType.STRING_LITERAL);
        addRule("\\d+\\.\\d+", TokenType.FLOAT_LITERAL);
        addRule("\\d+", TokenType.INT_LITERAL);
        addRule("[a-zA-Z_][a-zA-Z0-9_]*", TokenType.IDENTIFIER);
    }

    private void addRule(String regex, TokenType type) {
        rules.add(new TokenRule(regex, type));
    }

    public List<Token> scan(String input) {
        List<Token> tokens = new ArrayList<>();
        int line = 1;
        int pos = 0;

        while (pos < input.length()) {
            boolean matchFound = false;
            String remaining = input.substring(pos);

            if (remaining.startsWith("\n")) {
                line++;
                pos++;
                continue;
            }

            for (TokenRule rule : rules) {
                Matcher m = rule.pattern.matcher(remaining);
                if (m.find()) {
                    String lexeme = m.group(1);

                    if (rule.type != null) {
                        tokens.add(new Token(rule.type, lexeme, null, line));
                    }

                    pos += lexeme.length();
                    matchFound = true;
                    break;
                }
            }

            if (!matchFound) {
                char invalidChar = input.charAt(pos);
                if (!Character.isWhitespace(invalidChar)) {
                    System.err.println("Lexical error at line \" + line + \": Unexpected character'" + invalidChar + "'");
                }
                pos++;
            }
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }
}