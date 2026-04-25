package compilador.semantic;

import compilador.ast.DeviceNode;
import compilador.ast.DeviceType;
import compilador.ast.InterfaceNode;

import java.util.*;

/**
 * Tabla de Símbolos del compilador.
 *
 * Mantiene tres estructuras de datos:
 *
 *   1. devices           → Map<String, DeviceNode>
 *      Todos los dispositivos registrados, clave = nombre del dispositivo.
 *      Usada para: detectar S1 (duplicados), verificar S3/S4 (existencia en conexiones).
 *
 *   2. interfaceMap      → Map<String, Map<String, InterfaceNode>>
 *      Por cada dispositivo, sus interfaces.
 *      Clave exterior = nombre del dispositivo.
 *      Clave interior = nombre de la interfaz.
 *      Usada para: detectar S2 (interfaces duplicadas), verificar S5/S6 (existencia).
 *
 *   3. connectedIfaces   → Set<String>
 *      Interfaces ya utilizadas en alguna conexión.
 *      Formato de cada entrada: "NombreDispositivo.nombreInterfaz"
 *      Usada para: detectar S7 (interfaz conectada más de una vez).
 */
public class SymbolTable {

    // ── Estructura 1: dispositivos ───────────────────────────────────────────
    private final Map<String, DeviceNode> devices = new LinkedHashMap<>();

    // ── Estructura 2: interfaces por dispositivo ─────────────────────────────
    private final Map<String, Map<String, InterfaceNode>> interfaceMap = new LinkedHashMap<>();

    // ── Estructura 3: interfaces ya conectadas ───────────────────────────────
    private final Set<String> connectedIfaces = new LinkedHashSet<>();

    // ════════════════════════════════════════════════════════════════════════
    //  API de dispositivos
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Registra un dispositivo en la tabla.
     * También indexa todas sus interfaces en el mapa de interfaces.
     *
     * @param device el DeviceNode a registrar
     * @return true si fue registrado exitosamente, false si ya existía (duplicado → error S1)
     */
    public boolean registerDevice(DeviceNode device) {
        if (devices.containsKey(device.name)) {
            return false;   // Duplicado → el llamador emite error S1
        }
        devices.put(device.name, device);

        // Indexar interfaces de este dispositivo
        Map<String, InterfaceNode> ifMap = new LinkedHashMap<>();
        for (InterfaceNode iface : device.interfaces) {
            // Si hay duplicadas, solo registramos la primera; el llamador detecta S2
            if (!ifMap.containsKey(iface.name)) {
                ifMap.put(iface.name, iface);
            }
        }
        interfaceMap.put(device.name, ifMap);
        return true;
    }

    /**
     * Verifica si un dispositivo existe en la tabla.
     * Usada para validar las reglas S3 y S4.
     */
    public boolean deviceExists(String deviceName) {
        return devices.containsKey(deviceName);
    }

    /**
     * Obtiene el nodo de un dispositivo por nombre.
     * @return el DeviceNode, o null si no existe
     */
    public DeviceNode getDevice(String deviceName) {
        return devices.get(deviceName);
    }

    /**
     * Obtiene el tipo (DeviceType) de un dispositivo por nombre.
     * Usada por el SemanticAnalyzer para aplicar excepciones de la regla S7
     * (ACCESSPOINT y SWITCHL3 pueden tener múltiples conexiones en la misma interfaz).
     *
     * @param deviceName nombre del dispositivo
     * @return el DeviceType, o null si el dispositivo no existe
     */
    public DeviceType getDeviceType(String deviceName) {
        DeviceNode dev = devices.get(deviceName);
        return dev != null ? dev.deviceType : null;
    }

    /**
     * Retorna todos los dispositivos registrados (en orden de declaración).
     */
    public Collection<DeviceNode> getAllDevices() {
        return devices.values();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  API de interfaces
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Verifica si una interfaz existe dentro de un dispositivo.
     * Usada para validar las reglas S5 y S6.
     *
     * @param deviceName nombre del dispositivo
     * @param ifaceName  nombre de la interfaz
     * @return true si la interfaz existe en ese dispositivo
     */
    public boolean interfaceExists(String deviceName, String ifaceName) {
        Map<String, InterfaceNode> ifMap = interfaceMap.get(deviceName);
        return ifMap != null && ifMap.containsKey(ifaceName);
    }

    /**
     * Obtiene el nodo de una interfaz específica.
     * @return el InterfaceNode, o null si no existe
     */
    public InterfaceNode getInterface(String deviceName, String ifaceName) {
        Map<String, InterfaceNode> ifMap = interfaceMap.get(deviceName);
        return ifMap != null ? ifMap.get(ifaceName) : null;
    }

    /**
     * Detecta interfaces duplicadas dentro de un dispositivo.
     * Recorre todas las interfaces del DeviceNode y retorna los nombres repetidos.
     * Usada para validar la regla S2.
     *
     * @param device el DeviceNode a analizar
     * @return lista de nombres de interfaces duplicadas (puede ser vacía)
     */
    public List<String> getDuplicateInterfaces(DeviceNode device) {
        List<String> duplicates = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (InterfaceNode iface : device.interfaces) {
            if (!seen.add(iface.name)) {
                // add() retorna false si ya estaba → duplicado
                duplicates.add(iface.name);
            }
        }
        return duplicates;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  API de conexiones
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Marca una interfaz como "ya conectada".
     * Usada para validar la regla S7.
     *
     * @param deviceName nombre del dispositivo
     * @param ifaceName  nombre de la interfaz
     * @return true si fue marcada por primera vez, false si ya estaba conectada (error S7)
     */
    public boolean markConnected(String deviceName, String ifaceName) {
        String key = deviceName + "." + ifaceName;
        return connectedIfaces.add(key);  // false si ya existía
    }

    /**
     * Verifica si una interfaz ya fue conectada.
     */
    public boolean isConnected(String deviceName, String ifaceName) {
        return connectedIfaces.contains(deviceName + "." + ifaceName);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Representación para depuración (flag --symbols)
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════╗\n");
        sb.append("║              TABLA DE SÍMBOLOS               ║\n");
        sb.append("╠══════════════════════════════════════════════╣\n");

        for (Map.Entry<String, DeviceNode> entry : devices.entrySet()) {
            DeviceNode dev = entry.getValue();
            sb.append(String.format("║  %-20s  [%s]\n", dev.name, dev.deviceType));

            Map<String, InterfaceNode> ifMap = interfaceMap.get(dev.name);
            if (ifMap != null) {
                for (Map.Entry<String, InterfaceNode> ie : ifMap.entrySet()) {
                    InterfaceNode iface = ie.getValue();
                    String ip       = iface.hasIP() ? "  ip=" + iface.ipAddress : "";
                    String used     = isConnected(dev.name, iface.name) ? "  [CONECTADA]" : "";
                    sb.append(String.format("║    ↳ %-18s%s%s\n", iface.name, ip, used));
                }
            }
        }

        sb.append("╚══════════════════════════════════════════════╝\n");
        return sb.toString();
    }
}
