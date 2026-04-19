package compilador.semantico;

import compilador.ast.NodoDispositivo;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

public class TablaSimbolos {
    // clave: nombre del dispositivo
    private HashMap<String, NodoDispositivo> dispositivos;
    // clave: "NombreDispositivo.NombreInterfaz"
    private HashSet<String> interfazesUsadas;
    private ArrayList<String> errores;
    
    public TablaSimbolos() {
        this.dispositivos = new HashMap<>();
        this.interfazesUsadas = new HashSet<>();
        this.errores = new ArrayList<>();
    }

    public void registrarDispositivo(NodoDispositivo d) {
        dispositivos.put(d.getNombre(), d);
    }
    
    public NodoDispositivo getDispositivo(String nombre) {
        return dispositivos.get(nombre);
    }
    
    public boolean existeDispositivo(String nombre) {
        return dispositivos.containsKey(nombre);
    }
    
    public boolean existeInterfaz(String dispositivo, String interfaz) {
        NodoDispositivo d = getDispositivo(dispositivo);
        if (d == null) return false;
        return d.getInterfaces().stream().anyMatch(i -> i.getNombre().equals(interfaz));
    }
    
    public void marcarInterfazUsada(String dispositivo, String interfaz) {
        interfazesUsadas.add(dispositivo + "." + interfaz);
    }
    
    public boolean estaInterfazUsada(String dispositivo, String interfaz) {
        return interfazesUsadas.contains(dispositivo + "." + interfaz);
    }

    public void agregarError(String error) {
        errores.add(error);
    }

    public ArrayList<String> getErrores() {
        return errores;
    }
}
