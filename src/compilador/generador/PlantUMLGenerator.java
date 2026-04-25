package compilador.generador;

import compilador.ast.*;

/**
 * Generador de código PlantUML.
 *
 * Implementa ASTVisitor<String> y recorre el AST para producir
 * el código @startuml ... @enduml completo.
 *
 * Mapeo DeviceType → elemento PlantUML:
 *   ROUTER          → database   (cilindro visual)
 *   FIREWALL        → rectangle
 *   SWITCH          → component
 *   SWITCHL3        → component  (etiqueta "Switch L3")
 *   SERVER_WEB      → rectangle
 *   SERVER_DATABASE → database
 *   SERVER_APP      → component
 *   PC              → note
 *   LAPTOP          → note
 *   MOBILE          → note
 *   ACCESSPOINT     → node
 *   INTERNET        → cloud
 */
public class PlantUMLGenerator implements ASTVisitor<String> {

    // ════════════════════════════════════════════════════════════════════════
    //  Punto de entrada
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Genera el código PlantUML completo para una red.
     * @param network nodo raíz del AST
     * @return cadena con el código @startuml ... @enduml
     */
    public String generate(NetworkNode network) {
        return network.accept(this);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  VISITOR: NetworkNode  — produce el documento PlantUML completo
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public String visitNetwork(NetworkNode node) {
        StringBuilder sb = new StringBuilder();

        // ── Encabezado ────────────────────────────────────────────────────
        sb.append("@startuml\n");
        sb.append("title Topología de Red - ").append(node.name).append("\n\n");

        // ── Declaración de cada dispositivo ──────────────────────────────
        for (DeviceNode device : node.devices) {
            sb.append(device.accept(this)).append("\n");
        }

        sb.append("\n");

        // ── Declaración de cada conexión ──────────────────────────────────
        for (ConnectionNode conn : node.connections) {
            sb.append(conn.accept(this)).append("\n");
        }

        // ── Cierre ────────────────────────────────────────────────────────
        sb.append("\n@enduml\n");

        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  VISITOR: DeviceNode  — produce una línea de declaración PlantUML
    //  Ejemplo: database "Router R1\ng0/0: 10.0.0.1/30" as R1
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public String visitDevice(DeviceNode node) {
        String pumlType = getPumlType(node.deviceType);
        String label    = buildLabel(node);
        return pumlType + " " + label + " as " + node.name;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  VISITOR: InterfaceNode  — texto de la interfaz para la etiqueta
    //  Con IP:   "g0/0: 10.0.0.1/30"
    //  Sin IP:   "f0/1"
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public String visitInterface(InterfaceNode node) {
        if (node.hasIP()) {
            return node.name + ": " + node.ipAddress;
        }
        return node.name;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  VISITOR: ConnectionNode  — línea de relación PlantUML
    //  Ejemplo: PC1 -- SW1 : eth0 ↔ f0/1
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public String visitConnection(ConnectionNode node) {
        return node.sourceDevice + " -- " + node.targetDevice
             + " : " + node.sourceInterface + " \u2194 " + node.targetInterface;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Helpers privados
    // ════════════════════════════════════════════════════════════════════════

    /** Devuelve el tipo PlantUML correspondiente al DeviceType. */
    private String getPumlType(DeviceType type) {
        switch (type) {
            case ROUTER:          return "database";
            case FIREWALL:        return "rectangle";
            case SWITCH:          return "component";
            case SWITCHL3:        return "component";
            case SERVER_WEB:      return "rectangle";
            case SERVER_DATABASE: return "database";
            case SERVER_APP:      return "component";
            case PC:              return "note";
            case LAPTOP:          return "note";
            case MOBILE:          return "note";
            case ACCESSPOINT:     return "node";
            case INTERNET:        return "cloud";
            default:              return "rectangle";
        }
    }

    /**
     * Construye la etiqueta completa del dispositivo.
     * Formato: "EncabezadoDispositivo\nInterfaz1\nInterfaz2\n..."
     */
    private String buildLabel(DeviceNode node) {
        StringBuilder label = new StringBuilder("\"");

        // Encabezado según el tipo
        label.append(getHeader(node));

        // Una línea por cada interfaz
        for (InterfaceNode iface : node.interfaces) {
            label.append("\\n");
            label.append(iface.accept(this));
        }

        label.append("\"");
        return label.toString();
    }

    /** Devuelve el texto de encabezado de la etiqueta del dispositivo. */
    private String getHeader(DeviceNode node) {
        switch (node.deviceType) {
            case ROUTER:          return "Router " + node.name;
            case FIREWALL:        return "Firewall " + node.name;
            case SWITCH:          return "Switch " + node.name;
            case SWITCHL3:        return "Switch L3 " + node.name;
            case SERVER_WEB:      return node.name + "\\nServer Web";
            case SERVER_DATABASE: return node.name + "\\nServer Database";
            case SERVER_APP:      return node.name + "\\nServer App";
            case PC:              return node.name;
            case LAPTOP:          return node.name;
            case MOBILE:          return node.name;
            case ACCESSPOINT:     return node.name + "\\nAccess Point";
            case INTERNET:        return "Internet " + node.name;
            default:              return node.name;
        }
    }
}
