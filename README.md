# Compilador de Topologías de Red (DSL)

Este proyecto es un compilador de un lenguaje de dominio específico (DSL) diseñado para la descripción de topologías de red. Genera código PlantUML para la representación visual de dichas redes.

## Funcionalidades Actuales (Fase 1)
- Análisis Léxico (JFlex).
- Análisis Sintáctico (CUP).
- Generación de Árbol de Sintaxis Abstracta (AST).
- Generación de diagrama en formato PlantUML (`.puml`).

## Estructura del Proyecto
- `src/compilador/`: Código fuente en Java.
  - `lexer/`: Contiene `Lexer.flex` y el analizador generado.
  - `parser/`: Contiene `parser.cup` y el analizador generado.
  - `ast/`: Nodos del Árbol de Sintaxis Abstracta.
  - `generador/`: Generación a PlantUML.
- `lib/`: Contiene las librerías necesarias (`jflex-full-1.9.1.jar`, `java-cup-11b.jar`).
- `compilar.bat`: Script para compilar rápidamente en Windows (PowerShell/CMD).
- `compilar.sh`: Script equivalente para entornos Linux/Bash.

## Cómo compilar y ejecutar
Ejecuta el script incluido desde tu terminal para compilar el proyecto:

```bash
# En Windows
.\compilar.bat

# En Linux o WSL
./compilar.sh
```

Una vez compilado, puedes correr una prueba utilizando:
```bash
java -cp "bin;lib/java-cup-11b.jar" compilador.Main prueba1.red
```
Esto generará tu archivo `prueba1.red.puml` con el resultado gráfico.
