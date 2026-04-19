package compilador.ast;

public class NodoConexion {
    private String dispositivoOrigen;
    private String interfazOrigen;
    private String dispositivoDestino;
    private String interfazDestino;
    private int linea;
    private int columna;

    public NodoConexion(String dispositivoOrigen, String interfazOrigen, String dispositivoDestino, String interfazDestino, int linea, int columna) {
        this.dispositivoOrigen = dispositivoOrigen;
        this.interfazOrigen = interfazOrigen;
        this.dispositivoDestino = dispositivoDestino;
        this.interfazDestino = interfazDestino;
        this.linea = linea;
        this.columna = columna;
    }

    public String getDispositivoOrigen() { return dispositivoOrigen; }
    public String getInterfazOrigen() { return interfazOrigen; }
    public String getDispositivoDestino() { return dispositivoDestino; }
    public String getInterfazDestino() { return interfazDestino; }
    public int getLinea() { return linea; }
    public int getColumna() { return columna; }
}
