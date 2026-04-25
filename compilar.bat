@echo off
echo ==========================================
echo     COMPILANDO DSL DE TOPOLOGIAS DE RED
echo ==========================================

echo [1/3] Generando Analizador Lexico (JFlex)...
java -jar lib\jflex-full-1.9.1.jar -d src\compilador\lexer src\compilador\lexer\Lexer.flex

echo [2/3] Generando Analizador Sintactico (CUP)...
cd src\compilador\parser
java -jar ..\..\..\lib\java-cup-11b.jar -parser Parser -symbols sym parser.cup
cd ..\..\..\ 

echo [3/3] Compilando archivos Java...
if not exist bin mkdir bin

dir /s /B src\*.java > sources.txt
javac -d bin -cp "lib\java-cup-11b.jar;src" @sources.txt
del sources.txt

if %ERRORLEVEL% EQU 0 (
    echo ==========================================
    echo COMPILACION EXITOSA
    echo.
    echo Modo terminal:
    echo   java -cp "bin;lib\java-cup-11b.jar" compilador.Main examples\empresa.red
    echo   java -cp "bin;lib\java-cup-11b.jar" compilador.Main examples\empresa.red --tokens --symbols
    echo   java -cp "bin;lib\java-cup-11b.jar" compilador.Main examples\errorred.red
    echo   java -cp "bin;lib\java-cup-11b.jar" compilador.Main examples\minired.red --ast
    echo.
    echo Modo GUI:
    echo   java -cp "bin;lib\java-cup-11b.jar" compilador.CompilerGUI
    echo ==========================================
) else (
    echo ==========================================
    echo ERROR EN LA COMPILACION
)
