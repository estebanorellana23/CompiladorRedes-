package compilador.ast;

/**
 * Clase base abstracta para todos los nodos del AST.
 * Cada nodo guarda la posición (línea y columna) del token que lo originó.
 * Implementa el patrón Visitor mediante el método accept().
 */
public abstract class ASTNode {

    public final int line;
    public final int column;

    protected ASTNode(int line, int column) {
        this.line   = line;
        this.column = column;
    }

    /**
     * Acepta un visitante y delega la ejecución al método visitXxx correspondiente.
     * @param visitor el visitante a aplicar
     * @param <T>     tipo de retorno del visitante
     * @return resultado de la visita
     */
    public abstract <T> T accept(ASTVisitor<T> visitor);
}
