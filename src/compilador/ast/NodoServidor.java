package compilador.ast;
import java.util.List;

public class NodoServidor extends NodoDispositivo {
    private String tipoServidor;

    public NodoServidor(String nombre, String tipoServidor, List<NodoInterfaz> interfaces) {
        super(nombre, interfaces);
        this.tipoServidor = tipoServidor;
    }

    public String getTipoServidor() { return tipoServidor; }

    @Override
    public String toPlantUML() {
        StringBuilder sb = new StringBuilder();
        if (tipoServidor.equals("web")) {
            sb.append("rectangle \"").append(nombre).append("\\nServer Web");
        } else if (tipoServidor.equals("database")) {
            sb.append("database \"").append(nombre).append("\\nServer Database");
        } else {
            sb.append("component \"").append(nombre).append("\\nServer App");
        }
        for (NodoInterfaz iface : interfaces) {
            sb.append("\\n").append(iface.toPlantUML());
        }
        sb.append("\" as ").append(nombre);
        return sb.toString();
    }
}
