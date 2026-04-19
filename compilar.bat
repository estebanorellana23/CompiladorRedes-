@echo off
echo ==========================================
echo     COMPILANDO DSL DE TOPOLOGIAS DE RED
echo ==========================================

if not exist lib\jflex-full-1.9.1.jar if not exist lib\jflex-1.9.1.jar (
    echo [!] ADVERTENCIA: No se encontro jflex en esta carpeta.
)
if not exist lib\java-cup-11b.jar (
    echo [!] ADVERTENCIA: No se encontro java-cup-11b.jar en esta carpeta.
)

echo [1/3] Generando Analizador Lexico (JFlex)...
if exist lib\jflex-full-1.9.1.jar (
    java -jar lib\jflex-full-1.9.1.jar -d src\compilador\lexer src\compilador\lexer\Lexer.flex
) else if exist lib\jflex-1.9.1.jar (
    java -jar lib\jflex-1.9.1.jar -d src\compilador\lexer src\compilador\lexer\Lexer.flex
)

echo [2/3] Generando Analizador Sintactico (CUP)...
if exist lib\java-cup-11b.jar (
    cd src\compilador\parser
    java -jar ..\..\..\lib\java-cup-11b.jar -parser Parser -symbols sym parser.cup
    cd ..\..\..\
)

echo [3/3] Compilando archivos Java...
if not exist bin mkdir bin

if exist lib\java-cup-11b.jar (
    dir /s /B src\*.java > sources.txt
    javac -d bin -cp "lib\java-cup-11b.jar;src" @sources.txt
    del sources.txt
    echo ==========================================
    echo COMPILACION EXITOSA
    echo Para correr el programa ejecuta:
    echo java -cp "bin;lib\java-cup-11b.jar" compilador.Main prueba1.red
) else (
    echo No se pudo compilar el codigo Java porque falta java-cup-11b.jar
)
