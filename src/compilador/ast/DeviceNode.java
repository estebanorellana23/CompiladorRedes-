package compilador.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Nodo AST que representa un dispositivo de red.
 *
 * Corresponde a construcciones como:
 *   router   R1   { ... }
 *   switch   SW1  { ... }
 *   server   WEB1 tipo web { ... }
 */
public class DeviceNode extends ASTNode {

    /** Tipo exacto del dispositivo (ROUTER, SWITCH, SERVER_WEB, etc.) */
    public final DeviceType deviceType;

    /** Nombre del dispositivo (el ID que le sigue al keyword) */
    public final String name;

    /** Interfaces declaradas dentro del bloque del dispositivo */
    public final List<InterfaceNode> interfaces;

    public DeviceNode(DeviceType deviceType,
                      String name,
                      List<InterfaceNode> interfaces,
                      int line, int column) {
        super(line, column);
        this.deviceType = deviceType;
        this.name       = name;
        this.interfaces = interfaces != null ? interfaces : new ArrayList<>();
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitDevice(this);
    }

    @Override
    public String toString() {
        return String.format("DeviceNode(type=%s, name='%s', interfaces=%d)",
                deviceType, name, interfaces.size());
    }
}
