package compilador.semantic;

import compilador.ast.*;
import compilador.errors.CompilerError;
import compilador.errors.ErrorType;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Analizador Semántico — 10 reglas implementadas.
 *
 * Implementa ASTVisitor<Void> y recorre el AST en DOS PASADAS:
 *
 *   Pasada 1 — visitNetwork → visitDevice (para cada dispositivo):
 *     S1. No se puede repetir el nombre de un dispositivo.
 *     S2. No se puede repetir una interfaz dentro del mismo dispositivo.
 *     S8. Una IP debe cumplir el formato d.d.d.d/mask (octetos 0-255, mask 0-32).
 *     S9. Un switch normal no puede tener interfaces VLAN con dirección IP.
 *
 *   Pasada 2 — visitNetwork → visitConnection (para cada conexión):
 *     S3. El dispositivo ORIGEN de la conexión debe existir.
 *     S4. El dispositivo DESTINO de la conexión debe existir.
 *     S5. La interfaz ORIGEN debe existir en el dispositivo origen.
 *     S6. La interfaz DESTINO debe existir en el dispositivo destino.
 *     S7. Una interfaz no puede conectarse más de una vez.
 *    S10. Un dispositivo no puede conectarse a sí mismo.
 */
public class SemanticAnalyzer implements ASTVisitor<Void> {

    private final List<CompilerError> errors      = new ArrayList<>();
    private final SymbolTable         symbolTable = new SymbolTable();

    /** Patrón para validar IP/CIDR en la regla S8 */
    private static final Pattern IP_PATTERN = Pattern.compile(
        "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})/(\\d{1,2})$"
    );

    // ── Acceso externo ──────────────────────────────────────────────────────

    public List<CompilerError> getErrors()    { return errors; }
    public SymbolTable         getSymbolTable() { return symbolTable; }
    public boolean             hasErrors()    { return !errors.isEmpty(); }

    /**
     * Punto de entrada principal.
     * Lanza el análisis desde el nodo raíz.
     */
    public void analyze(NetworkNode network) {
        network.accept(this);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  VISITOR: NetworkNode
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public Void visitNetwork(NetworkNode node) {
        // ── PASADA 1: registrar dispositivos y validar reglas sobre dispositivos ──
        for (DeviceNode device : node.devices) {
            device.accept(this);
        }

        // ── PASADA 2: validar conexiones (la tabla ya está completa) ──────────
        for (ConnectionNode conn : node.connections) {
            conn.accept(this);
        }

        return null;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  VISITOR: DeviceNode
    //  Aplica: S1, S2, S8, S9
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public Void visitDevice(DeviceNode node) {

        // ── S1: Dispositivo duplicado ────────────────────────────────────────
        if (!symbolTable.registerDevice(node)) {
            error("Dispositivo duplicado: '" + node.name + "'", node.line, node.column);
        }

        // ── S2: Interfaces duplicadas dentro del mismo dispositivo ───────────
        List<String> dupIfaces = symbolTable.getDuplicateInterfaces(node);
        for (String dupName : dupIfaces) {
            int dupLine = node.line;
            int dupCol  = node.column;
            boolean foundFirst = false;
            for (InterfaceNode iface : node.interfaces) {
                if (iface.name.equals(dupName)) {
                    if (!foundFirst) { foundFirst = true; }
                    else { dupLine = iface.line; dupCol = iface.column; break; }
                }
            }
            error("Interfaz duplicada '" + dupName + "' dentro del dispositivo '" + node.name + "'",
                  dupLine, dupCol);
        }

        // ── S8 + S9: validar cada interfaz ───────────────────────────────────
        for (InterfaceNode iface : node.interfaces) {
            iface.accept(this);
            validateDomainRules(node, iface);
        }

        return null;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  VISITOR: InterfaceNode
    //  Aplica: S8 (formato de IP)
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public Void visitInterface(InterfaceNode node) {
        if (node.hasIP()) {
            validateIP(node.ipAddress, node.line, node.column);
        }
        return null;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  VISITOR: ConnectionNode
    //  Aplica: S3, S4, S5, S6, S7, S10
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public Void visitConnection(ConnectionNode node) {

        // ── S10: Auto-conexión ───────────────────────────────────────────────
        if (node.sourceDevice.equals(node.targetDevice)) {
            error("Un dispositivo no puede conectarse a sí mismo: '"
                  + node.sourceDevice + "'", node.line, node.column);
            return null;
        }

        boolean srcDevOk = true;
        boolean dstDevOk = true;

        // ── S3: Dispositivo ORIGEN inexistente ───────────────────────────────
        if (!symbolTable.deviceExists(node.sourceDevice)) {
            error("Dispositivo origen inexistente: '" + node.sourceDevice + "'",
                  node.line, node.column);
            srcDevOk = false;
        }

        // ── S4: Dispositivo DESTINO inexistente ──────────────────────────────
        if (!symbolTable.deviceExists(node.targetDevice)) {
            error("Dispositivo destino inexistente: '" + node.targetDevice + "'",
                  node.line, node.column);
            dstDevOk = false;
        }

        // ── S5: Interfaz ORIGEN inexistente ──────────────────────────────────
        if (srcDevOk && !symbolTable.interfaceExists(node.sourceDevice, node.sourceInterface)) {
            error("Interfaz origen inexistente: '" + node.sourceDevice + "."
                  + node.sourceInterface + "'", node.line, node.column);
        }

        // ── S6: Interfaz DESTINO inexistente ─────────────────────────────────
        if (dstDevOk && !symbolTable.interfaceExists(node.targetDevice, node.targetInterface)) {
            error("Interfaz destino inexistente: '" + node.targetDevice + "."
                  + node.targetInterface + "'", node.line, node.column);
        }

        // ── S7: Interfaz ORIGEN ya conectada ─────────────────────────────────
        if (srcDevOk && symbolTable.interfaceExists(node.sourceDevice, node.sourceInterface)) {
            if (!symbolTable.markConnected(node.sourceDevice, node.sourceInterface)) {
                error("La interfaz '" + node.sourceDevice + "." + node.sourceInterface
                      + "' ya fue utilizada en otra conexión", node.line, node.column);
            }
        }

        // ── S7: Interfaz DESTINO ya conectada ────────────────────────────────
        if (dstDevOk && symbolTable.interfaceExists(node.targetDevice, node.targetInterface)) {
            if (!symbolTable.markConnected(node.targetDevice, node.targetInterface)) {
                error("La interfaz '" + node.targetDevice + "." + node.targetInterface
                      + "' ya fue utilizada en otra conexión", node.line, node.column);
            }
        }

        return null;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Validaciones de dominio
    // ════════════════════════════════════════════════════════════════════════

    /** S9: Un switch capa 2 no puede tener interfaces VLAN con IP asignada. */
    private void validateDomainRules(DeviceNode device, InterfaceNode iface) {
        if (device.deviceType == DeviceType.SWITCH) {
            if (iface.name.toLowerCase().startsWith("vlan") && iface.hasIP()) {
                error("Un switch normal no puede tener interfaces VLAN con IP. "
                      + "Use switchL3 para eso (dispositivo: '" + device.name
                      + "', interfaz: '" + iface.name + "')",
                      iface.line, iface.column);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Validación de IP  (S8)
    // ════════════════════════════════════════════════════════════════════════

    private void validateIP(String ip, int line, int col) {
        java.util.regex.Matcher m = IP_PATTERN.matcher(ip);
        if (!m.matches()) {
            error("Formato de IP inválido: '" + ip + "'. Formato esperado: d.d.d.d/mask",
                  line, col);
            return;
        }

        for (int i = 1; i <= 4; i++) {
            int octet = Integer.parseInt(m.group(i));
            if (octet < 0 || octet > 255) {
                error("Octeto inválido en la IP '" + ip + "': "
                      + octet + " (debe ser 0-255)", line, col);
                return;
            }
        }

        int mask = Integer.parseInt(m.group(5));
        if (mask < 0 || mask > 32) {
            error("Máscara inválida en la IP '" + ip + "': /"
                  + mask + " (debe ser 0-32)", line, col);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Helper
    // ════════════════════════════════════════════════════════════════════════

    private void error(String message, int line, int col) {
        errors.add(new CompilerError(ErrorType.SEMANTICO, message, line, col));
    }
}
