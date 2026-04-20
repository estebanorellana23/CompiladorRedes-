package compilador.lexer;

/**
 * Representa un token producido por el Lexer.
 * Cada token tiene:
 *   type   → categoría del token (enum TokenType)
 *   value  → texto original del lexema
 *   line   → número de línea donde aparece (1-indexed)
 *   column → número de columna donde empieza (1-indexed)
 */
public class Token {

    public final TokenType type;
    public final String    value;
    public final int       line;
    public final int       column;

    public Token(TokenType type, String value, int line, int column) {
        this.type   = type;
        this.value  = value;
        this.line   = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return String.format("Token(%-15s \"%s\"  L%d:C%d)",
                type, value, line, column);
    }
}
