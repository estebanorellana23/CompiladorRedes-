/* =========================================================================
 *  Lexer.flex  —  Especificación JFlex para el DSL de Topologías de Red
 *
 *  Para generar Lexico.java ejecutar:
 *      java -jar lib/jflex-full-1.9.1.jar src/compilador/lexer/Lexer.flex -d src/compilador/lexer/
 *
 *  La clase generada se llama Lexico y su método principal es yylex().
 *  Cada llamada a yylex() devuelve el siguiente Token o null al llegar al EOF.
 * ========================================================================= */

package compilador.lexer;

import compilador.errors.CompilerError;
import compilador.errors.ErrorType;
import java.util.ArrayList;
import java.util.List;

%%

/* ── Opciones de la clase generada ──────────────────────────────────── */
%class    Lexico
%unicode
%line                   /* activa la variable yyline  (0-indexed) */
%column                 /* activa la variable yycolumn (0-indexed) */
%type     Token
%public

/* ── Código que se inserta dentro de la clase generada ──────────────── */
%{
    /** Lista donde se acumulan los errores léxicos */
    private List<CompilerError> errors = new ArrayList<>();

    /** Acceso a la lista de errores desde el Main */
    public List<CompilerError> getErrors() { return errors; }

    /** Crea un Token con línea y columna correctas (1-indexed) */
    private Token tok(TokenType t, String v) {
        return new Token(t, v, yyline + 1, yycolumn + 1);
    }

    /** Registra un error léxico y devuelve un token ERROR */
    private Token lexError(String lexema) {
        String msg = "Carácter no reconocido: '" + lexema + "'";
        errors.add(new CompilerError(ErrorType.LEXICO, msg, yyline + 1, yycolumn + 1));
        return tok(TokenType.ERROR, lexema);
    }
%}

/* ── Definiciones de patrones ────────────────────────────────────────── */

/* Componentes básicos */
DIGIT        = [0-9]
LETTER       = [a-zA-Z]
ID_CHAR      = [a-zA-Z0-9_/\-]

/* Identificador: empieza con letra, seguido de ID_CHAR*
   Cubre nombres como: R1  SW1  eth0  g0/0  g1/0/1  vlan10  wlan0  f0/1  wan0 */
IDENTIFIER   = {LETTER}({ID_CHAR})*

/* Dirección IP con máscara CIDR: ddd.ddd.ddd.ddd/mm
   IMPORTANTE: debe ir ANTES que IDENTIFIER en las reglas */
OCTET        = ([0-9])|([1-9][0-9])|(1[0-9][0-9])|(2[0-4][0-9])|(25[0-5])
MASK         = ([0-9])|([12][0-9])|(3[012])
IP_CIDR      = {OCTET}"."{OCTET}"."{OCTET}"."{OCTET}"/"{MASK}

/* Comentario de línea: comienza con '  hasta fin de línea */
LINE_COMMENT = "'"[^\r\n]*

/* Espacios en blanco (incluyendo saltos de línea) */
WHITESPACE   = [ \t\r\n]+

%%

/* =========================================================================
 *  REGLAS LÉXICAS
 *  Orden importa: más específico primero.
 *  JFlex aplica la regla más larga que coincida; en empate, la primera.
 * ========================================================================= */

/* Ignorar espacios en blanco y comentarios */
{WHITESPACE}        { /* ignorar */ }
{LINE_COMMENT}      { /* ignorar */ }

/* ── Palabra clave de estructura ─────────────────────────────────────── */
"red"               { return tok(TokenType.RED,         yytext()); }

/* ── Palabras clave de dispositivos ──────────────────────────────────── */
"router"            { return tok(TokenType.ROUTER,      yytext()); }
"firewall"          { return tok(TokenType.FIREWALL,    yytext()); }
"switchL3"          { return tok(TokenType.SWITCHL3,    yytext()); }  /* ANTES de "switch" */
"switch"            { return tok(TokenType.SWITCH,      yytext()); }
"server"            { return tok(TokenType.SERVER,      yytext()); }
"pc"                { return tok(TokenType.PC,          yytext()); }
"laptop"            { return tok(TokenType.LAPTOP,      yytext()); }
"mobile"            { return tok(TokenType.MOBILE,      yytext()); }
"accesspoint"       { return tok(TokenType.ACCESSPOINT, yytext()); }
"internet"          { return tok(TokenType.INTERNET,    yytext()); }

/* ── Palabras clave del lenguaje ─────────────────────────────────────── */
"interfaz"          { return tok(TokenType.INTERFAZ,    yytext()); }
"conectar"          { return tok(TokenType.CONECTAR,    yytext()); }
"tipo"              { return tok(TokenType.TIPO,        yytext()); }

/* ── Subtipos de servidor ────────────────────────────────────────────── */
"web"               { return tok(TokenType.WEB,         yytext()); }
"database"          { return tok(TokenType.DATABASE,    yytext()); }
"app"               { return tok(TokenType.APP,         yytext()); }

/* ── IP/CIDR — ANTES que IDENTIFIER ─────────────────────────────────── */
{IP_CIDR}           { return tok(TokenType.IP_ADDRESS,  yytext()); }

/* ── Identificadores generales ───────────────────────────────────────── */
{IDENTIFIER}        { return tok(TokenType.ID,          yytext()); }

/* ── Símbolos ─────────────────────────────────────────────────────────── */
"{"                 { return tok(TokenType.LBRACE,      yytext()); }
"}"                 { return tok(TokenType.RBRACE,      yytext()); }
";"                 { return tok(TokenType.SEMICOLON,   yytext()); }
"->"                { return tok(TokenType.ARROW,       yytext()); }
"."                 { return tok(TokenType.DOT,         yytext()); }

/* ── Error léxico: cualquier carácter no reconocido ──────────────────── */
.                   { return lexError(yytext()); }
