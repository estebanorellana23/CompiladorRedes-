package compilador.ast;
import java.util.List;

public class NodoRouter extends NodoDispositivo {
    public NodoRouter(String nombre, List<NodoInterfaz> interfaces) {
        super(nombre, interfaces);
    }
    
    @Override
    public String toPlantUML() {
        StringBuilder sb = new StringBuilder();
        sb.append("database \"Router ").append(nombre);
        for (NodoInterfaz iface : interfaces) {
            sb.append("\\n").append(iface.toPlantUML());
        }
        sb.append("\" as ").append(nombre);
        return sb.toString();
    }
}
