package compilador;

import compilador.ast.NetworkNode;
import compilador.generador.PlantUMLGenerator;
import compilador.errors.CompilerError;
import compilador.lexer.Token;
import compilador.lexer.TokenType;
import compilador.lexer.Lexico;
import compilador.parser.Parser;
import compilador.semantic.SemanticAnalyzer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class CompilerGUI extends JFrame {

    private static final Color BG_DARK     = new Color(30,  30,  30);
    private static final Color BG_PANEL    = new Color(40,  40,  40);
    private static final Color BG_EDITOR   = new Color(25,  25,  25);
    private static final Color FG_TEXT     = new Color(220, 220, 220);
    private static final Color FG_DIM      = new Color(150, 150, 150);
    private static final Color COLOR_OK    = new Color(80,  200, 120);
    private static final Color COLOR_ERROR = new Color(255, 90,  90);
    private static final Color COLOR_WARN  = new Color(255, 200, 60);
    private static final Color COLOR_ACC   = new Color(86,  156, 214);
    private static final Color BTN_COMPILE = new Color(0,   122, 204);
    private static final Color BTN_OPEN    = new Color(60,  60,  60);
    private static final Color BTN_DIAGRAM = new Color(40,  140, 80);
    private static final Color BTN_EX1     = new Color(120, 60,  160);
    private static final Color BTN_EX2     = new Color(160, 100, 20);
    private static final Color BTN_EX3     = new Color(160, 40,  40);

    private JTextArea editorArea;
    private JTextArea outputArea;
    private JLabel    statusLabel;
    private JLabel    fileLabel;
    private JButton   btnCompile;
    private JButton   btnOpen;
    private JButton   btnDiagram;
    private JButton   btnSave;
    private JButton   btnEx1;
    private JButton   btnEx2;
    private JButton   btnEx3;

    private String lastPumlCode = "";
    private File   currentFile  = null;

    // â”€â”€ Ejemplos DSL â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private static final String EXAMPLE_EMPRESA =
        "red Empresa {\n\n" +
        "    router R1 {\n" +
        "        interfaz g0/0 ip 10.0.0.1/30;\n" +
        "        interfaz g0/1 ip 192.168.1.1/24;\n" +
        "    }\n\n" +
        "    firewall FW1 {\n" +
        "        interfaz inside  ip 192.168.1.254/24;\n" +
        "        interfaz outside ip 10.0.0.2/30;\n" +
        "    }\n\n" +
        "    switchL3 CORE {\n" +
        "        interfaz g1/0/1;\n" +
        "        interfaz g1/0/2;\n" +
        "        interfaz vlan10 ip 192.168.10.1/24;\n" +
        "        interfaz vlan20 ip 192.168.20.1/24;\n" +
        "    }\n\n" +
        "    switch SW1 {\n" +
        "        interfaz f0/1;\n" +
        "        interfaz f0/2;\n" +
        "        interfaz f0/3;\n" +
        "    }\n\n" +
        "    server WEB1 tipo web {\n" +
        "        interfaz eth0 ip 192.168.10.10/24;\n" +
        "    }\n\n" +
        "    server DB1 tipo database {\n" +
        "        interfaz eth0 ip 192.168.20.10/24;\n" +
        "    }\n\n" +
        "    pc PC1 {\n" +
        "        interfaz eth0 ip 192.168.10.20/24;\n" +
        "    }\n\n" +
        "    pc PC2 {\n" +
        "        interfaz eth0 ip 192.168.20.21/24;\n" +
        "    }\n\n" +
        "    laptop L1 {\n" +
        "        interfaz wlan0 ip 192.168.10.30/24;\n" +
        "    }\n\n" +
        "    mobile M1 {\n" +
        "        interfaz wifi0 ip 192.168.10.40/24;\n" +
        "    }\n\n" +
        "    accesspoint AP1 {\n" +
        "        interfaz wlan1;\n" +
        "        interfaz eth0;\n" +
        "    }\n\n" +
        "    internet NET {\n" +
        "        interfaz wan0;\n" +
        "    }\n\n" +
        "    conectar PC1.eth0    -> SW1.f0/1;\n" +
        "    conectar PC2.eth0    -> SW1.f0/2;\n" +
        "    conectar L1.wlan0   -> AP1.wlan1;\n" +
        "    conectar M1.wifi0   -> AP1.wlan1;\n" +
        "    conectar AP1.eth0   -> SW1.f0/3;\n" +
        "    conectar WEB1.eth0  -> CORE.g1/0/1;\n" +
        "    conectar DB1.eth0   -> CORE.g1/0/2;\n" +
        "    conectar CORE.g1/0/2 -> FW1.inside;\n" +
        "    conectar FW1.outside -> R1.g0/0;\n" +
        "    conectar R1.g0/1    -> NET.wan0;\n\n" +
        "}";

    private static final String EXAMPLE_MINIRED =
        "red MiniRed {\n\n" +
        "    router R1 {\n" +
        "        interfaz g0/0 ip 10.0.0.1/30;\n" +
        "        interfaz g0/1 ip 10.0.0.5/30;\n" +
        "    }\n\n" +
        "    firewall FW1 {\n" +
        "        interfaz outside ip 10.0.0.2/30;\n" +
        "        interfaz inside  ip 192.168.1.1/24;\n" +
        "    }\n\n" +
        "    switch SW1 {\n" +
        "        interfaz f0/1;\n" +
        "        interfaz f0/2;\n" +
        "    }\n\n" +
        "    pc PC1 {\n" +
        "        interfaz eth0 ip 192.168.1.10/24;\n" +
        "    }\n\n" +
        "    internet NET {\n" +
        "        interfaz wan0;\n" +
        "    }\n\n" +
        "    conectar PC1.eth0    -> SW1.f0/1;\n" +
        "    conectar SW1.f0/2    -> FW1.inside;\n" +
        "    conectar FW1.outside -> R1.g0/0;\n" +
        "    conectar R1.g0/1     -> NET.wan0;\n\n" +
        "}";

    private static final String EXAMPLE_ERRORRED =
        "red ErrorRed {\n\n" +
        "    router R1 {\n" +
        "        interfaz g0/0 ip 10.0.0.1/30;\n" +
        "        interfaz g0/0 ip 192.168.1.1/24;\n" +    // interfaz duplicada
        "    }\n\n" +
        "    switch SW1 {\n" +
        "        interfaz f0/1;\n" +
        "    }\n\n" +
        "    pc PC1 {\n" +
        "        interfaz eth0 ip 192.168.1.10/24;\n" +
        "    }\n\n" +
        "    pc PC1 {\n" +                                 // dispositivo duplicado
        "        interfaz eth1 ip 192.168.1.11/24;\n" +
        "    }\n\n" +
        "    firewall FW1 {\n" +
        "        interfaz inside ip 192.168.1.254/24;\n" +
        "    }\n\n" +
        "    conectar PC1.eth0   -> SW1.f0/2;\n" +        // interfaz inexistente
        "    conectar PC2.eth0   -> SW1.f0/1;\n" +        // dispositivo inexistente
        "    conectar FW1.outside -> R1.g0/0;\n" +        // interfaz outside inexistente
        "    conectar R1.g0/0   -> SW1.f0/1;\n" +
        "    conectar R1.g0/0   -> FW1.inside;\n" +       // conexion multiple
        "    conectar SW1.f0/1  -> FW1.inside;\n\n" +
        "}";

    public CompilerGUI() {
        super("Compilador DSL - Topologias de Red");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        buildUI();
        loadExample(EXAMPLE_MINIRED);   // carga MiniRed por defecto
    }

    private void buildUI() {
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));
        add(buildToolbar(),   BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(new Color(37, 37, 38));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)));

        btnOpen    = makeButton("Abrir",        BTN_OPEN);
        btnSave    = makeButton("Guardar",      BTN_OPEN);
        btnCompile = makeButton("Compilar",     BTN_COMPILE);
        btnDiagram = makeButton("Ver diagrama", BTN_DIAGRAM);
        btnDiagram.setEnabled(false);

        // Botones de ejemplos
        btnEx1 = makeButton("Ejemplo 1: Empresa",  BTN_EX1);
        btnEx2 = makeButton("Ejemplo 2: MiniRed",  BTN_EX2);
        btnEx3 = makeButton("Ejemplo 3: ErrorRed", BTN_EX3);

        btnOpen   .addActionListener(e -> openFile());
        btnSave   .addActionListener(e -> saveFile());
        btnCompile.addActionListener(e -> compile());
        btnDiagram.addActionListener(e -> openDiagram());

        btnEx1.addActionListener(e -> loadExample(EXAMPLE_EMPRESA));
        btnEx2.addActionListener(e -> loadExample(EXAMPLE_MINIRED));
        btnEx3.addActionListener(e -> loadExample(EXAMPLE_ERRORRED));

        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "compile");
        getRootPane().getActionMap().put("compile", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { compile(); }
        });

        fileLabel = new JLabel("  sin archivo");
        fileLabel.setForeground(FG_DIM);
        fileLabel.setFont(new Font("Consolas", Font.PLAIN, 12));

        bar.add(btnOpen);
        bar.add(btnSave);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(makeSeparator());
        bar.add(Box.createHorizontalStrut(8));
        bar.add(btnCompile);
        JLabel hint = new JLabel("  Ctrl+Enter");
        hint.setForeground(FG_DIM);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bar.add(hint);
        bar.add(Box.createHorizontalStrut(8));
        bar.add(makeSeparator());
        bar.add(Box.createHorizontalStrut(8));
        bar.add(btnDiagram);
        bar.add(Box.createHorizontalStrut(16));
        bar.add(makeSeparator());
        bar.add(Box.createHorizontalStrut(8));

        // Etiqueta de ejemplos
        JLabel exLabel = new JLabel("Ejemplos:");
        exLabel.setForeground(FG_DIM);
        exLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bar.add(exLabel);
        bar.add(btnEx1);
        bar.add(btnEx2);
        bar.add(btnEx3);

        bar.add(Box.createHorizontalStrut(16));
        bar.add(fileLabel);
        return bar;
    }

    private JSplitPane buildMainPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BG_PANEL);
        leftPanel.setBorder(titledBorder("Editor  (.red)"));
        editorArea = makeTextArea(new Font("Consolas", Font.PLAIN, 14), BG_EDITOR, FG_TEXT);
        editorArea.setTabSize(4);
        leftPanel.add(styledScroll(editorArea), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(BG_PANEL);
        rightPanel.setBorder(titledBorder("Salida del compilador"));
        outputArea = makeTextArea(new Font("Consolas", Font.PLAIN, 13), BG_EDITOR, FG_TEXT);
        outputArea.setEditable(false);
        rightPanel.add(styledScroll(outputArea), BorderLayout.CENTER);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        mainSplit.setDividerLocation(520);
        mainSplit.setDividerSize(4);
        mainSplit.setBackground(BG_DARK);
        return mainSplit;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        bar.setBackground(new Color(0, 122, 204));
        statusLabel = new JLabel("Listo");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bar.add(statusLabel);
        return bar;
    }

    private void compile() {
        String source = editorArea.getText().trim();
        if (source.isEmpty()) { setStatus("El editor esta vacio.", COLOR_WARN); return; }

        File tmp;
        try {
            tmp = File.createTempFile("dsl_compile_", ".red");
            tmp.deleteOnExit();
            Files.write(tmp.toPath(), source.getBytes("UTF-8"));
        } catch (IOException ex) {
            setStatus("Error al crear archivo temporal.", COLOR_ERROR);
            return;
        }

        btnCompile.setEnabled(false);
        btnDiagram.setEnabled(false);
        setStatus("Compilando...", COLOR_ACC);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            String output   = "";
            String pumlText = "";
            boolean success = false;

            @Override
            protected Void doInBackground() {
                StringBuilder sb = new StringBuilder();
                boolean hasErrors = false;

                sb.append("========================================================\n");
                sb.append("  Compilador DSL - Topologias de Red\n");
                sb.append("========================================================\n\n");

                try {
                    // FASE 1 - Lexico
                    sb.append("[FASE 1] Analisis Lexico...\n");

                    Lexico lexer = new Lexico(new FileReader(tmp));
                    List<Token> allTokens = new ArrayList<>();
                    Token t;
                    while ((t = lexer.yylex()) != null) allTokens.add(t);

                    List<CompilerError> lexErrors = lexer.getErrors();
                    long tokenCount = allTokens.stream().filter(x -> x.type != TokenType.EOF).count();

                    if (lexErrors.isEmpty()) {
                        sb.append("  OK Sin errores lexicos. Tokens reconocidos: ").append(tokenCount).append("\n");
                    } else {
                        sb.append("  ERROR ").append(lexErrors.size()).append(" error(es) lexico(s):\n");
                        for (CompilerError e : lexErrors)
                            sb.append("    [LEXICO] Linea ").append(e.line)
                              .append(", Columna ").append(e.column)
                              .append(": ").append(e.message).append("\n");
                        hasErrors = true;
                    }

                    sb.append("\n  --- TOKENS (").append(tokenCount).append(") ---\n");
                    sb.append(String.format("  %-20s  %-25s  %s%n", "TOKEN", "VALOR", "POSICION"));
                    sb.append("  ").append("-".repeat(58)).append("\n");
                    for (Token tk : allTokens) {
                        if (tk.type != TokenType.EOF) {
                            sb.append(String.format("  %-20s  %-25s  L%d:C%d%n",
                                tk.type, "\"" + tk.value + "\"", tk.line, tk.column));
                        }
                    }

                    // FASE 2 - Sintactico
                    sb.append("\n[FASE 2] Analisis Sintactico...\n");

                    Lexico lexer2 = new Lexico(new FileReader(tmp));
                    Parser parser = new Parser(new Main.LexerAdapter(lexer2));
                    NetworkNode ast = null;
                    try { ast = (NetworkNode) parser.parse().value; } catch (Exception ignored) {}

                    List<CompilerError> synErrors = parser.getParseErrors();
                    if (synErrors.isEmpty()) {
                        sb.append("  OK Sin errores sintacticos.\n");
                    } else {
                        sb.append("  ERROR ").append(synErrors.size()).append(" error(es) sintactico(s):\n");
                        for (CompilerError e : synErrors)
                            sb.append("    [SINTACTICO] Linea ").append(e.line)
                              .append(", Columna ").append(e.column)
                              .append(": ").append(e.message).append("\n");
                        hasErrors = true;
                    }

                    if (ast == null) {
                        sb.append("\n  El AST no pudo construirse. Compilacion detenida.\n");
                        output = sb.toString();
                        return null;
                    }

                    // FASE 3 - Semantico
                    sb.append("\n[FASE 3] Analisis Semantico...\n");

                    SemanticAnalyzer semantic = new SemanticAnalyzer();
                    semantic.analyze(ast);
                    List<CompilerError> semErrors = semantic.getErrors();

                    if (semErrors.isEmpty()) {
                        sb.append("  OK Sin errores semanticos.\n");
                    } else {
                        sb.append("  ERROR ").append(semErrors.size()).append(" error(es) semantico(s):\n");
                        for (CompilerError e : semErrors)
                            sb.append("    [SEMANTICO] Linea ").append(e.line)
                              .append(", Columna ").append(e.column)
                              .append(": ").append(e.message).append("\n");
                        hasErrors = true;
                    }

                    sb.append("\n").append(semantic.getSymbolTable().toString()).append("\n");

                    // FASE 4 - PlantUML
                    sb.append("[FASE 4] Generacion de codigo PlantUML...\n");

                    if (!hasErrors) {
                        PlantUMLGenerator gen = new PlantUMLGenerator();
                        pumlText = gen.generate(ast);
                        lastPumlCode = pumlText;

                        if (currentFile != null) {
                            String outPath = currentFile.getAbsolutePath()
                                .replaceAll("(?i)\\.red$", "") + ".puml";
                            Files.write(Paths.get(outPath), pumlText.getBytes("UTF-8"));
                            sb.append("  OK Codigo PlantUML generado: ").append(outPath).append("\n");
                        } else {
                            sb.append("  OK Codigo PlantUML generado.\n");
                        }

                        sb.append("\n  --- CODIGO PLANTUML ---\n");
                        sb.append(pumlText).append("\n");
                        sb.append("  ").append("-".repeat(58)).append("\n");
                        success = true;
                    } else {
                        sb.append("  ERROR Generacion omitida (hay errores en fases anteriores).\n");
                    }

                    sb.append("\n========================================================\n");
                    if (!hasErrors) {
                        sb.append("  Compilacion exitosa.\n");
                    } else {
                        sb.append("  Compilacion con errores.\n");
                        sb.append("\n  --- RESUMEN DE ERRORES ---\n");
                        List<CompilerError> all = new ArrayList<>();
                        all.addAll(lexErrors);
                        all.addAll(synErrors);
                        all.addAll(semErrors);
                        all.sort(Comparator.comparingInt((CompilerError e) -> e.line)
                                           .thenComparingInt(e -> e.column));
                        for (CompilerError e : all)
                            sb.append("    [").append(e.type).append("] Linea ")
                              .append(e.line).append(", Columna ").append(e.column)
                              .append(": ").append(e.message).append("\n");
                    }
                    sb.append("========================================================\n");

                } catch (Exception ex) {
                    sb.append("\nError inesperado: ").append(ex.getMessage()).append("\n");
                }

                output = sb.toString();
                return null;
            }

            @Override
            protected void done() {
                outputArea.setText(output);
                outputArea.setCaretPosition(0);

                if (success) {
                    btnDiagram.setEnabled(true);
                    setStatus("Compilacion exitosa.", COLOR_OK);
                } else {
                    setStatus("Compilacion con errores.", COLOR_ERROR);
                }
                btnCompile.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void openDiagram() {
        if (lastPumlCode.isEmpty()) return;
        try {
            String encoded = encodePlantUML(lastPumlCode);
            URI uri = new URI("https://www.plantuml.com/plantuml/png/" + encoded);
            Desktop.getDesktop().browse(uri);
            setStatus("Diagrama abierto en el navegador.", COLOR_OK);
        } catch (Exception ex) {
            setStatus("No se pudo abrir el navegador: " + ex.getMessage(), COLOR_ERROR);
        }
    }

    private String encodePlantUML(String text) throws Exception {
        byte[] data = text.getBytes("UTF-8");
        java.util.zip.Deflater deflater = new java.util.zip.Deflater(
                java.util.zip.Deflater.BEST_COMPRESSION, true);
        deflater.setInput(data);
        deflater.finish();
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        while (!deflater.finished()) { int n = deflater.deflate(buf); baos.write(buf, 0, n); }
        deflater.end();
        return encode64(baos.toByteArray());
    }

    private static final String PLANTUML_ALPHABET =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_";

    private String encode64(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < data.length) {
            int b0 = data[i++] & 0xFF;
            int b1 = i < data.length ? data[i++] & 0xFF : 0;
            int b2 = i < data.length ? data[i++] & 0xFF : 0;
            sb.append(PLANTUML_ALPHABET.charAt(b0 >> 2));
            sb.append(PLANTUML_ALPHABET.charAt(((b0 & 0x3) << 4) | (b1 >> 4)));
            sb.append(PLANTUML_ALPHABET.charAt(((b1 & 0xF) << 2) | (b2 >> 6)));
            sb.append(PLANTUML_ALPHABET.charAt(b2 & 0x3F));
        }
        return sb.toString();
    }

    private void openFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Archivos DSL (*.red)", "red"));
        if (currentFile != null) fc.setCurrentDirectory(currentFile.getParentFile());
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = fc.getSelectedFile();
            try {
                String content = new String(Files.readAllBytes(currentFile.toPath()), "UTF-8");
                editorArea.setText(content);
                editorArea.setCaretPosition(0);
                fileLabel.setText("  " + currentFile.getName());
                outputArea.setText("");
                btnDiagram.setEnabled(false);
                setStatus("Archivo cargado: " + currentFile.getName(), COLOR_OK);
            } catch (IOException ex) {
                setStatus("Error al leer el archivo.", COLOR_ERROR);
            }
        }
    }

    private void saveFile() {
        if (currentFile == null) {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Archivos DSL (*.red)", "red"));
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            currentFile = fc.getSelectedFile();
            if (!currentFile.getName().endsWith(".red"))
                currentFile = new File(currentFile.getAbsolutePath() + ".red");
        }
        try {
            Files.write(currentFile.toPath(), editorArea.getText().getBytes("UTF-8"));
            fileLabel.setText("  " + currentFile.getName());
            setStatus("Guardado: " + currentFile.getName(), COLOR_OK);
        } catch (IOException ex) {
            setStatus("Error al guardar.", COLOR_ERROR);
        }
    }

    /**
     * Carga un ejemplo en el editor y limpia el estado previo.
     */
    private void loadExample(String dslText) {
        editorArea.setText(dslText);
        editorArea.setCaretPosition(0);
        outputArea.setText("");
        lastPumlCode = "";
        btnDiagram.setEnabled(false);
        currentFile = null;
        fileLabel.setText("  sin archivo");

        String label = "ejemplo";
        if (dslText == EXAMPLE_EMPRESA)   label = "Empresa";
        else if (dslText == EXAMPLE_MINIRED)  label = "MiniRed";
        else if (dslText == EXAMPLE_ERRORRED) label = "ErrorRed";
        setStatus("Ejemplo cargado: " + label + ". Presiona Compilar o Ctrl+Enter.", COLOR_ACC);
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(" " + msg);
        statusLabel.getParent().setBackground(color.darker());
        statusLabel.setForeground(Color.WHITE);
    }

    private JTextArea makeTextArea(Font font, Color bg, Color fg) {
        JTextArea ta = new JTextArea();
        ta.setFont(font); ta.setBackground(bg); ta.setForeground(fg);
        ta.setCaretColor(Color.WHITE); ta.setLineWrap(false);
        ta.setMargin(new Insets(6, 8, 6, 8));
        return ta;
    }

    private JScrollPane styledScroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_EDITOR);
        return sp;
    }

    private TitledBorder titledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)), title,
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.PLAIN, 12), FG_DIM);
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        btn.addMouseListener(new MouseAdapter() {
            Color original = bg;
            public void mouseEntered(MouseEvent e)  { btn.setBackground(original.brighter()); }
            public void mouseExited(MouseEvent e)   { btn.setBackground(original); }
            public void mousePressed(MouseEvent e)  { btn.setBackground(original.darker()); }
            public void mouseReleased(MouseEvent e) { btn.setBackground(original.brighter()); }
        });
        return btn;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 24));
        sep.setForeground(new Color(80, 80, 80));
        return sep;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new CompilerGUI().setVisible(true));
    }
}

