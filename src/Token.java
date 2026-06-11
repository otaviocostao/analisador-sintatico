public class Token {
    public final TokenType type;
    public final String lexeme;
    public final Object value;
    public final int line;

    public Token(TokenType type, String lexeme, Object value, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.value = value;
        this.line = line;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, '%s')", type, lexeme);
    }
}