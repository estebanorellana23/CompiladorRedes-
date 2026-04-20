package compilador.ast;

/**
 * Interfaz del patrón Visitor para recorrer el AST.
 *
 * Implementaciones:
 *   - SemanticAnalyzer  → ASTVisitor<Void>   (valida reglas semánticas)
 *   - PlantUMLGenerator → ASTVisitor<String> (genera código PlantUML)
 *
 * @param <T> tipo de valor que devuelve cada visita
 */
public interface ASTVisitor<T> {

    /** Visita el nodo raíz de la red */
    T visitNetwork(NetworkNode node);

    /** Visita un nodo de dispositivo */
    T visitDevice(DeviceNode node);

    /** Visita un nodo de interfaz */
    T visitInterface(InterfaceNode node);

    /** Visita un nodo de conexión */
    T visitConnection(ConnectionNode node);
}
