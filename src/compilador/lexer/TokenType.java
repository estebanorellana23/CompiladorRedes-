package compilador.lexer;

/**
 * Enumeración de todos los tipos de token reconocidos por el Lexer.
 *
 * ORDEN IMPORTANTE en Lexer.flex:
 *   1. Palabras clave antes que ID
 *   2. switchL3 antes que switch
 *   3. IP_CIDR antes que IDENTIFIER
 */
public enum TokenType {

    // ── Palabras clave: estructura ────────────────────────────────
    RED,            // red

    // ── Palabras clave: dispositivos ─────────────────────────────
    ROUTER,         // router
    FIREWALL,       // firewall
    SWITCHL3,       // switchL3  ← debe ir ANTES de SWITCH en el .flex
    SWITCH,         // switch
    SERVER,         // server
    PC,             // pc
    LAPTOP,         // laptop
    MOBILE,         // mobile
    ACCESSPOINT,    // accesspoint
    INTERNET,       // internet

    // ── Palabras clave: lenguaje ──────────────────────────────────
    INTERFAZ,       // interfaz
    CONECTAR,       // conectar
    TIPO,           // tipo

    // ── Palabras clave: subtipos de servidor ──────────────────────
    WEB,            // web
    DATABASE,       // database
    APP,            // app

    // ── Literales ────────────────────────────────────────────────
    ID,             // identificador:  R1  SW1  eth0  g0/0  vlan10  wlan0
    IP_ADDRESS,     // IP/CIDR:  10.0.0.1/30   192.168.1.1/24

    // ── Símbolos ─────────────────────────────────────────────────
    LBRACE,         // {
    RBRACE,         // }
    SEMICOLON,      // ;
    ARROW,          // ->
    DOT,            // .

    // ── Especiales ───────────────────────────────────────────────
    EOF,
    ERROR
}
