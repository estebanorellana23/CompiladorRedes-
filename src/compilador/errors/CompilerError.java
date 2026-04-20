package compilador.errors;

/**
 * Representa un error emitido por cualquier fase del compilador.
 * Contiene el tipo de error, el mensaje descriptivo, la línea y la columna
 * del token o carácter que causó el error.
 */
public class CompilerError {

    public final ErrorType type;
    public final String    message;
    public final int       line;
    public final int       column;

    public CompilerError(ErrorType type, String message, int line, int column) {
        this.type    = type;
        this.message = message;
        this.line    = line;
        this.column  = column;
    }

    @Override
    public String toString() {
        return String.format("[%s] Línea %d, Columna %d: %s",
                type, line, column, message);
    }
}
