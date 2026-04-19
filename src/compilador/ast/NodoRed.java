package compilador.ast;

import java.util.List;

public class NodoRed {
    private String nombre;
    private List<NodoDispositivo> dispositivos;
    private List<NodoConexion> conexiones;

    @SuppressWarnings("unchecked")
    public NodoRed(String nombre, Object dispositivos, Object conexiones) {
        this.nombre = nombre;
        this.dispositivos = (List<NodoDispositivo>) dispositivos;
        this.conexiones = (List<NodoConexion>) conexiones;
    }

    public String getNombre() { return nombre; }
    public List<NodoDispositivo> getDispositivos() { return dispositivos; }
    public List<NodoConexion> getConexiones() { return conexiones; }
}
