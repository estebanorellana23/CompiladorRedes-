package compilador.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Nodo raíz del AST.
 * Representa la construcción:
 *   red NombreRed {
 *       <dispositivos>
 *       <conexiones>
 *   }
 */
public class NetworkNode extends ASTNode {

    /** Nombre de la red (el ID que sigue a la keyword 'red') */
    public final String name;

    /** Lista de todos los dispositivos declarados dentro de la red */
    public final List<DeviceNode> devices;

    /** Lista de todas las conexiones declaradas dentro de la red */
    public final List<ConnectionNode> connections;

    public NetworkNode(String name,
                       List<DeviceNode> devices,
                       List<ConnectionNode> connections,
                       int line, int column) {
        super(line, column);
        this.name        = name;
        this.devices     = devices     != null ? devices     : new ArrayList<>();
        this.connections = connections != null ? connections : new ArrayList<>();
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitNetwork(this);
    }

    @Override
    public String toString() {
        return String.format("NetworkNode(name='%s', devices=%d, connections=%d)",
                name, devices.size(), connections.size());
    }
}
