package compilador.ast;
import java.util.List;

public class NodoAccessPoint extends NodoDispositivo {
    public NodoAccessPoint(String nombre, List<NodoInterfaz> interfaces) {
        super(nombre, interfaces);
    }
    
    @Override
    public String toPlantUML() {
        StringBuilder sb = new StringBuilder();
        sb.append("node \"").append(nombre).append("\\nAccess Point");
        for (NodoInterfaz iface : interfaces) {
            sb.append("\\n").append(iface.toPlantUML());
        }
        sb.append("\" as ").append(nombre);
        return sb.toString();
    }
}
