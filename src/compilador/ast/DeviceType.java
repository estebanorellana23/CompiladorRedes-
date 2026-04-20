package compilador.ast;

/**
 * Tipos de dispositivo de red soportados por el DSL.
 * SERVER tiene tres subtipos según la cláusula "tipo web|database|app".
 */
public enum DeviceType {

    ROUTER,
    FIREWALL,
    SWITCH,
    SWITCHL3,
    SERVER_WEB,
    SERVER_DATABASE,
    SERVER_APP,
    PC,
    LAPTOP,
    MOBILE,
    ACCESSPOINT,
    INTERNET;

    /**
     * Convierte el keyword del lexer al DeviceType correspondiente.
     *
     * @param keyword    texto del token de dispositivo (router, firewall, etc.)
     * @param serverType subtipo para SERVER (web | database | app), null si no es SERVER
     * @return DeviceType correspondiente
     */
    public static DeviceType fromString(String keyword, String serverType) {
        switch (keyword.toLowerCase()) {
            case "router":      return ROUTER;
            case "firewall":    return FIREWALL;
            case "switch":      return SWITCH;
            case "switchl3":    return SWITCHL3;
            case "server":
                if (serverType == null) return SERVER_WEB;
                switch (serverType.toLowerCase()) {
                    case "database": return SERVER_DATABASE;
                    case "app":      return SERVER_APP;
                    default:         return SERVER_WEB;
                }
            case "pc":          return PC;
            case "laptop":      return LAPTOP;
            case "mobile":      return MOBILE;
            case "accesspoint": return ACCESSPOINT;
            case "internet":    return INTERNET;
            default:
                throw new IllegalArgumentException("Tipo de dispositivo desconocido: " + keyword);
        }
    }
}
