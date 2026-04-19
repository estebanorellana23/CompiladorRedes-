package compilador.ast;

public class NodoInterfaz {
    private String nombre;
    private String direccionIP;  // null si no tiene IP
    private int linea;
    private int columna;

    public NodoInterfaz(String nombre, String direccionIP, int linea, int columna) {
        this.nombre = nombre;
        this.direccionIP = direccionIP;
        this.linea = linea;
        this.columna = columna;
    }

    public String getNombre()      { return nombre; }
    public String getDireccionIP() { return direccionIP; }
    public boolean tieneIP()       { return direccionIP != null; }
    public int getLinea()          { return linea; }
    public int getColumna()        { return columna; }

    @Override
    public String toString() {
        return tieneIP() ? nombre + " ip " + direccionIP : nombre;
    }
    
    public String toPlantUML() {
        return tieneIP() ? nombre + ": " + direccionIP : nombre;
    }
}
