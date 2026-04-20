package compilador.ast;

/**
 * Nodo AST que representa una conexión entre dos interfaces de dispositivos.
 *
 * Corresponde a:
 *   conectar PC1.eth0 -> SW1.f0/1;
 *   conectar FW1.outside -> R1.g0/0;
 */
public class ConnectionNode extends ASTNode {

    /** Nombre del dispositivo de origen */
    public final String sourceDevice;

    /** Nombre de la interfaz de origen */
    public final String sourceInterface;

    /** Nombre del dispositivo de destino */
    public final String targetDevice;

    /** Nombre de la interfaz de destino */
    public final String targetInterface;

    public ConnectionNode(String sourceDevice,
                          String sourceInterface,
                          String targetDevice,
                          String targetInterface,
                          int line, int column) {
        super(line, column);
        this.sourceDevice    = sourceDevice;
        this.sourceInterface = sourceInterface;
        this.targetDevice    = targetDevice;
        this.targetInterface = targetInterface;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitConnection(this);
    }

    @Override
    public String toString() {
        return String.format("ConnectionNode(%s.%s -> %s.%s)",
                sourceDevice, sourceInterface, targetDevice, targetInterface);
    }
}
