#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

echo "=========================================="
echo "    COMPILANDO DSL DE TOPOLOGÍAS DE RED   "
echo "=========================================="

if [ ! -f "lib/jflex-full-1.9.1.jar" ] && [ ! -f "lib/jflex-1.9.1.jar" ]; then
    echo "[!] ADVERTENCIA: No se encontró jflex en lib/"
fi

if [ ! -f "lib/java-cup-11b.jar" ]; then
    echo "[!] ADVERTENCIA: No se encontró java-cup-11b.jar en lib/"
fi

echo "[1/3] Generando Analizador Léxico (JFlex)..."
if [ -f "lib/jflex-full-1.9.1.jar" ]; then
    java -jar lib/jflex-full-1.9.1.jar -d src/compilador/lexer src/compilador/lexer/Lexer.flex
elif [ -f "lib/jflex-1.9.1.jar" ]; then
    java -jar lib/jflex-1.9.1.jar -d src/compilador/lexer src/compilador/lexer/Lexer.flex
fi

echo "[2/3] Generando Analizador Sintáctico (CUP)..."
if [ -f "lib/java-cup-11b.jar" ]; then
    cd src/compilador/parser
    java -jar ../../../lib/java-cup-11b.jar -parser Parser -symbols sym parser.cup
    cd ../../../
fi

echo "[3/3] Compilando archivos Java..."
mkdir -p bin

if [ -f "lib/java-cup-11b.jar" ]; then
    find src -name "*.java" > sources.txt
    javac -d bin -cp "lib/java-cup-11b.jar:src" @sources.txt
    rm sources.txt
    echo "=========================================="
    echo "¡COMPILACIÓN EXITOSA!"
    echo "Para correr el programa ejecuta:"
    echo "java -cp \"bin:lib/java-cup-11b.jar\" compilador.Main prueba1.red"
else
    echo "No se pudo compilar el código Java porque falta lib/java-cup-11b.jar"
fi
