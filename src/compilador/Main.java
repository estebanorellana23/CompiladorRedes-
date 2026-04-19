package compilador;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import compilador.ast.NodoRed;
import compilador.lexer.Lexico;
import compilador.parser.Parser;
import compilador.generador.PlantUMLGenerator;

public class Main {
    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java compilador.Main <archivo.red>");
            return;
        }

        try {
            System.out.println("Analizando archivo: " + args[0]);
            Lexico lexer = new Lexico(new FileReader(args[0]));
            Parser parser = new Parser(lexer);

            // Iniciar el analisis sintactico
            Object result = parser.parse().value;

            if (result instanceof NodoRed) {
                NodoRed red = (NodoRed) result;
                System.out.println("¡Análisis completado exitosamente!");
                System.out.println("Topología detectada: " + red.getNombre());
                
                // Generar código PlantUML
                PlantUMLGenerator generador = new PlantUMLGenerator();
                String pumlCode = generador.generar(red);
                
                // Escribir a archivo
                String outputFile = args[0] + ".puml";
                PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
                writer.print(pumlCode);
                writer.close();
                
                System.out.println("Archivo PlantUML generado: " + outputFile);
            } else {
                System.out.println("El análisis terminó, pero no se generó un AST válido.");
            }

        } catch (Exception e) {
            System.err.println("Error durante la compilación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
