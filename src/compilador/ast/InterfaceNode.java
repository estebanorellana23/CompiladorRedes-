package compilador.ast;

/**
 * Nodo AST que representa una declaración de interfaz dentro de un dispositivo.
 *
 * Corresponde a:
 *   interfaz g0/0;                       (sin IP)
 *   interfaz g0/0 ip 10.0.0.1/30;       (con IP)
 *   interfaz vlan10 ip 192.168.10.1/24;  (VLAN — solo válida en switchL3)
 */
public class InterfaceNode extends ASTNode {

    /** Nombre de la interfaz: g0/0, eth0, wlan0, f0/1, vlan10, wan0, etc. */
    public final String name;

    /**
     * Dirección IP en formato CIDR asignada a la interfaz.
     * Es null si la interfaz no tiene IP asignada.
     */
    public final String ipAddress;

    public InterfaceNode(String name, String ipAddress, int line, int column) {
        super(line, column);
        this.name      = name;
        this.ipAddress = ipAddress;
    }

    /** Retorna true si esta interfaz tiene una dirección IP asignada */
    public boolean hasIP() {
        return ipAddress != null;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitInterface(this);
    }

    @Override
    public String toString() {
        if (ipAddress != null) {
            return String.format("InterfaceNode(name='%s', ip='%s')", name, ipAddress);
        }
        return String.format("InterfaceNode(name='%s')", name);
    }
}
