package compilador.ast;
import java.util.List;

public class NodoSwitch extends NodoDispositivo {
    public NodoSwitch(String nombre, List<NodoInterfaz> interfaces) {
        super(nombre, interfaces);
    }
    
    @Override
    public String toPlantUML() {
        StringBuilder sb = new StringBuilder();
        sb.append("component \"Switch ").append(nombre);
        for (NodoInterfaz iface : interfaces) {
            sb.append("\\n").append(iface.toPlantUML());
        }
        sb.append("\" as ").append(nombre);
        return sb.toString();
    }
}
