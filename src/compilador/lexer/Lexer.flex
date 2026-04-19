package compilador.lexer;
import java_cup.runtime.Symbol;
import compilador.parser.sym;

%%
%class Lexico
%public
%line
%column
%cup
%unicode

/* Definiciones */
ESPACIO      = [ \t\r\n]+
IDENTIFICADOR = [a-zA-Z][a-zA-Z0-9_/]*
DIRECCION_IP = [0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\/[0-9]{1,2}
COMENTARIO   = \'[^\n]*

%%

{ESPACIO}      { /* ignorar */ }
{COMENTARIO}   { /* ignorar comentarios */ }

"red"          { return new Symbol(sym.RED,         yyline+1, yycolumn+1, yytext()); }
"router"       { return new Symbol(sym.ROUTER,      yyline+1, yycolumn+1, yytext()); }
"firewall"     { return new Symbol(sym.FIREWALL,    yyline+1, yycolumn+1, yytext()); }
"switch"       { return new Symbol(sym.SWITCH,      yyline+1, yycolumn+1, yytext()); }
"switchL3"     { return new Symbol(sym.SWITCHL3,    yyline+1, yycolumn+1, yytext()); }
"server"       { return new Symbol(sym.SERVER,      yyline+1, yycolumn+1, yytext()); }
"pc"           { return new Symbol(sym.PC,          yyline+1, yycolumn+1, yytext()); }
"laptop"       { return new Symbol(sym.LAPTOP,      yyline+1, yycolumn+1, yytext()); }
"mobile"       { return new Symbol(sym.MOBILE,      yyline+1, yycolumn+1, yytext()); }
"accesspoint"  { return new Symbol(sym.ACCESSPOINT, yyline+1, yycolumn+1, yytext()); }
"internet"     { return new Symbol(sym.INTERNET,    yyline+1, yycolumn+1, yytext()); }
"interfaz"     { return new Symbol(sym.INTERFAZ,    yyline+1, yycolumn+1, yytext()); }
"ip"           { return new Symbol(sym.IP_KW,       yyline+1, yycolumn+1, yytext()); }
"conectar"     { return new Symbol(sym.CONECTAR,    yyline+1, yycolumn+1, yytext()); }
"tipo"         { return new Symbol(sym.TIPO,        yyline+1, yycolumn+1, yytext()); }
"web"          { return new Symbol(sym.WEB,         yyline+1, yycolumn+1, yytext()); }
"database"     { return new Symbol(sym.DATABASE,    yyline+1, yycolumn+1, yytext()); }
"app"          { return new Symbol(sym.APP,         yyline+1, yycolumn+1, yytext()); }

"->"           { return new Symbol(sym.FLECHA,      yyline+1, yycolumn+1, yytext()); }
"{"            { return new Symbol(sym.LLAVE_A,     yyline+1, yycolumn+1, yytext()); }
"}"            { return new Symbol(sym.LLAVE_C,     yyline+1, yycolumn+1, yytext()); }
";"            { return new Symbol(sym.PYC,         yyline+1, yycolumn+1, yytext()); }
"."            { return new Symbol(sym.PUNTO,       yyline+1, yycolumn+1, yytext()); }

{DIRECCION_IP} { return new Symbol(sym.DIRECCION_IP, yyline+1, yycolumn+1, yytext()); }

{IDENTIFICADOR} { return new Symbol(sym.IDENTIFICADOR, yyline+1, yycolumn+1, yytext()); }

.              { System.err.println("ERROR léxico: '" + yytext()
                     + "' en línea " + (yyline+1) + ", columna " + (yycolumn+1));
                 return new Symbol(sym.error, yyline+1, yycolumn+1, yytext()); }
