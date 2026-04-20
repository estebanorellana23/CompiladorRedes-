package compilador;

import compilador.ast.*;
import compilador.errors.CompilerError;
import compilador.errors.ErrorType;
import compilador.lexer.Token;
import compilador.lexer.TokenType;
import compilador.lexer.Lexico;
import compilador.parser.Parser;
import compilador.parser.sym;
import compilador.semantic.SemanticAnalyzer;
import compilador.generador.PlantUMLGenerator;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Compilador principal del DSL de Topologías de Red.
 *
 * Orquesta las cuatro fases:
 *   Fase 1 — Análisis Léxico     (Lexico — generado por JFlex desde Lexer.flex)
 *   Fase 2 — Análisis Sintáctico (Parser — generado por CUP desde parser.cup)
 *   Fase 3 — Análisis Semántico  (SemanticAnalyzer — 10 reglas)
 *   Fase 4 — Generación PlantUML (PlantUMLGenerator)
 *
 * Uso:
 *   java -cp "bin;lib/java-cup-11b.jar" compilador.Main archivo.red
 *   java -cp "bin;lib/java-cup-11b.jar" compilador.Main archivo.red --tokens
 *   java -cp "bin;lib/java-cup-11b.jar" compilador.Main archivo.red --ast
 *   java -cp "bin;lib/java-cup-11b.jar" compilador.Main archivo.red --symbols
 */
public class Main {

    // ════════════════════════════════════════════════════════════════════════
    //  MAIN
    // ════════════════════════════════════════════════════════════════════════

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {

        printBanner();

        if (args.length == 0) {
            printUsage();
            return;
        }

        String  inputFile   = args[0];
        boolean showTokens  = hasFlag(args, "--tokens");
        boolean showAST     = hasFlag(args, "--ast");
        boolean showSymbols = hasFlag(args, "--symbols");

        if (!new File(inputFile).exists()) {
            System.err.println("ERROR: No se encontró el archivo '" + inputFile + "'");
            return;
        }

        System.out.println("Compilando: " + inputFile);
        System.out.println("═".repeat(56));

        boolean hasErrors = false;

        // ════════════════════════════════════════════════════════════════════
        //  FASE 1 — ANÁLISIS LÉXICO
        // ════════════════════════════════════════════════════════════════════
        System.out.println("\n[FASE 1] Análisis Léxico...");

        Lexico lexer1 = new Lexico(new FileReader(inputFile));
        List<Token> allTokens = new ArrayList<>();
        try {
            Token t;
            while ((t = lexer1.yylex()) != null) {
                allTokens.add(t);
            }
        } catch (Exception e) {
            System.err.println("Error inesperado en el Lexer: " + e.getMessage());
        }

        List<CompilerError> lexErrors = lexer1.getErrors();

        if (lexErrors.isEmpty()) {
            long nonEof = allTokens.stream().filter(t -> t.type != TokenType.EOF).count();
            System.out.println("  ✓ Sin errores léxicos. Tokens reconocidos: " + nonEof);
        } else {
            System.out.println("  ✗ " + lexErrors.size() + " error(es) léxico(s):");
            printErrors(lexErrors);
            hasErrors = true;
        }

        if (showTokens) {
            System.out.println("\n  ─── TOKENS ───────────────────────────────────────────");
            for (Token t : allTokens) {
                if (t.type != TokenType.EOF) {
                    System.out.printf("  %-18s  %-25s  L%d:C%d%n",
                            t.type, "\"" + t.value + "\"", t.line, t.column);
                }
            }
            System.out.println("  ──────────────────────────────────────────────────────");
        }

        // ════════════════════════════════════════════════════════════════════
        //  FASE 2 — ANÁLISIS SINTÁCTICO
        // ════════════════════════════════════════════════════════════════════
        System.out.println("\n[FASE 2] Análisis Sintáctico...");

        // Segunda pasada del lexer para dárselo al Parser
        Lexico lexer2 = new Lexico(new FileReader(inputFile));
        Parser parser = new Parser(new LexerAdapter(lexer2));

        NetworkNode ast = null;
        try {
            ast = (NetworkNode) parser.parse().value;
        } catch (Exception e) {
            // El error ya fue registrado por syntax_error() en parser.cup
        }

        List<CompilerError> syntaxErrors = parser.getParseErrors();

        if (syntaxErrors.isEmpty()) {
            System.out.println("  ✓ Sin errores sintácticos.");
        } else {
            System.out.println("  ✗ " + syntaxErrors.size() + " error(es) sintáctico(s):");
            printErrors(syntaxErrors);
            hasErrors = true;
        }

        if (ast == null) {
            System.out.println("\n✗ El AST no pudo construirse. Compilación detenida.");
            printSummary(hasErrors, lexErrors.size() + syntaxErrors.size());
            return;
        }

        if (showAST) {
            System.out.println("\n  ─── AST ──────────────────────────────────────────────");
            printAST(ast);
            System.out.println("  ──────────────────────────────────────────────────────");
        }

        // ════════════════════════════════════════════════════════════════════
        //  FASE 3 — ANÁLISIS SEMÁNTICO
        // ════════════════════════════════════════════════════════════════════
        System.out.println("\n[FASE 3] Análisis Semántico...");

        SemanticAnalyzer semantic = new SemanticAnalyzer();
        semantic.analyze(ast);
        List<CompilerError> semErrors = semantic.getErrors();

        if (semErrors.isEmpty()) {
            System.out.println("  ✓ Sin errores semánticos.");
        } else {
            System.out.println("  ✗ " + semErrors.size() + " error(es) semántico(s):");
            printErrors(semErrors);
            hasErrors = true;
        }

        if (showSymbols) {
            System.out.println();
            System.out.println(semantic.getSymbolTable());
        }

        // ════════════════════════════════════════════════════════════════════
        //  FASE 4 — GENERACIÓN DE CÓDIGO PLANTUML
        // ════════════════════════════════════════════════════════════════════
        System.out.println("\n[FASE 4] Generación de código PlantUML...");

        if (hasErrors) {
            System.out.println("  ✗ Generación omitida — hay errores en fases anteriores.");
            System.out.println("\n  ─── RESUMEN DE ERRORES ───────────────────────────────");
            List<CompilerError> all = new ArrayList<>();
            all.addAll(lexErrors);
            all.addAll(syntaxErrors);
            all.addAll(semErrors);
            all.sort(Comparator.comparingInt((CompilerError e) -> e.line)
                               .thenComparingInt(e -> e.column));
            printErrors(all);
            System.out.println("  ──────────────────────────────────────────────────────");
        } else {
            PlantUMLGenerator generator = new PlantUMLGenerator();
            String pumlCode = generator.generate(ast);

            // Guardar el .puml reemplazando la extensión .red
            String outFile = inputFile.replaceAll("(?i)\\.red$", "") + ".puml";
            Files.write(Paths.get(outFile), pumlCode.getBytes("UTF-8"));

            System.out.println("  ✓ Código PlantUML generado: " + outFile);
            System.out.println("\n  ─── CÓDIGO PLANTUML ──────────────────────────────────");
            System.out.println(pumlCode);
            System.out.println("  ──────────────────────────────────────────────────────");
        }

        printSummary(hasErrors, lexErrors.size() + syntaxErrors.size() + semErrors.size());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ADAPTADOR Lexico → Scanner de CUP
    //  CUP requiere que el Lexer implemente java_cup.runtime.Scanner
    // ════════════════════════════════════════════════════════════════════════

    static class LexerAdapter implements java_cup.runtime.Scanner {

        private final Lexico lexer;

        LexerAdapter(Lexico lexer) {
            this.lexer = lexer;
        }

        @Override
        public java_cup.runtime.Symbol next_token() throws Exception {
            Token t = lexer.yylex();

            if (t == null || t.type == TokenType.EOF) {
                return new java_cup.runtime.Symbol(sym.EOF, 0, 0, null);
            }

            int cupSym = tokenTypeToCupSymbol(t.type);
            return new java_cup.runtime.Symbol(cupSym, t.line, t.column, t);
        }

        private int tokenTypeToCupSymbol(TokenType type) {
            switch (type) {
                case RED:         return sym.RED;
                case ROUTER:      return sym.ROUTER;
                case FIREWALL:    return sym.FIREWALL;
                case SWITCHL3:    return sym.SWITCHL3;
                case SWITCH:      return sym.SWITCH;
                case SERVER:      return sym.SERVER;
                case PC:          return sym.PC;
                case LAPTOP:      return sym.LAPTOP;
                case MOBILE:      return sym.MOBILE;
                case ACCESSPOINT: return sym.ACCESSPOINT;
                case INTERNET:    return sym.INTERNET;
                case INTERFAZ:    return sym.INTERFAZ;
                case CONECTAR:    return sym.CONECTAR;
                case TIPO:        return sym.TIPO;
                case WEB:         return sym.WEB;
                case DATABASE:    return sym.DATABASE;
                case APP:         return sym.APP;
                case ID:          return sym.ID;
                case IP_ADDRESS:  return sym.IP_ADDRESS;
                case LBRACE:      return sym.LBRACE;
                case RBRACE:      return sym.RBRACE;
                case SEMICOLON:   return sym.SEMICOLON;
                case ARROW:       return sym.ARROW;
                case DOT:         return sym.DOT;
                case ERROR:       return sym.error;
                default:          return sym.EOF;
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Helpers de impresión
    // ════════════════════════════════════════════════════════════════════════

    private static boolean hasFlag(String[] args, String flag) {
        for (String a : args) if (a.equals(flag)) return true;
        return false;
    }

    private static void printErrors(List<CompilerError> errors) {
        for (CompilerError e : errors) {
            System.out.println("    " + e);
        }
    }

    private static void printAST(NetworkNode net) {
        System.out.println("  Network: " + net.name);
        for (DeviceNode dev : net.devices) {
            System.out.printf("    Device: %-20s [%s]%n", dev.name, dev.deviceType);
            for (InterfaceNode iface : dev.interfaces) {
                String ip = iface.hasIP() ? "  ip=" + iface.ipAddress : "";
                System.out.println("      Interface: " + iface.name + ip);
            }
        }
        for (ConnectionNode conn : net.connections) {
            System.out.println("    Connection: " + conn.sourceDevice + "."
                    + conn.sourceInterface + " -> "
                    + conn.targetDevice + "." + conn.targetInterface);
        }
    }

    private static void printBanner() {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║       Compilador DSL — Topologías de Red               ║");
        System.out.println("║  Fases: Léxico (JFlex) · Sintáctico (CUP) ·            ║");
        System.out.println("║         Semántico · Generación PlantUML                ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printUsage() {
        System.out.println("Uso:");
        System.out.println("  java -cp \"bin;lib/java-cup-11b.jar\" compilador.Main <archivo.red> [opciones]");
        System.out.println();
        System.out.println("Opciones:");
        System.out.println("  --tokens    Muestra la lista completa de tokens reconocidos");
        System.out.println("  --ast       Muestra el Árbol de Sintaxis Abstracta");
        System.out.println("  --symbols   Muestra la Tabla de Símbolos semántica");
        System.out.println();
        System.out.println("Ejemplos:");
        System.out.println("  java ... compilador.Main examples/empresa.red");
        System.out.println("  java ... compilador.Main examples/empresa.red --tokens --symbols");
        System.out.println("  java ... compilador.Main examples/errorred.red");
    }

    private static void printSummary(boolean hasErrors, int errorCount) {
        System.out.println();
        System.out.println("═".repeat(56));
        if (!hasErrors) {
            System.out.println("✓  Compilación exitosa.");
        } else {
            System.out.println("✗  Compilación con " + errorCount + " error(es).");
        }
    }
}
