package compilador.ast;

import java.util.List;

public abstract class NodoDispositivo {
    protected String nombre;
    protected List<NodoInterfaz> interfaces;

    public NodoDispositivo(String nombre, List<NodoInterfaz> interfaces) {
        this.nombre = nombre;
        this.interfaces = interfaces;
    }

    public String getNombre() { return nombre; }
    public List<NodoInterfaz> getInterfaces() { return interfaces; }
    
    public abstract String toPlantUML();
}
