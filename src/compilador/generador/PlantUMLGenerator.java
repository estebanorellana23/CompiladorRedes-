package compilador.generador;

import compilador.ast.NodoRed;
import compilador.ast.NodoDispositivo;
import compilador.ast.NodoConexion;

public class PlantUMLGenerator {
    
    public String generar(NodoRed red) {
        StringBuilder sb = new StringBuilder();
        sb.append("@startuml\n");
        sb.append("title Topología de Red - ").append(red.getNombre()).append("\n\n");
        
        for (NodoDispositivo d : red.getDispositivos()) {
            sb.append(traducirDispositivo(d)).append("\n");
        }
        sb.append("\n");
        for (NodoConexion c : red.getConexiones()) {
            sb.append(traducirConexion(c)).append("\n");
        }
        sb.append("@enduml\n");
        return sb.toString();
    }
    
    private String traducirDispositivo(NodoDispositivo d) {
        return d.toPlantUML();
    }
    
    private String traducirConexion(NodoConexion c) {
        return c.getDispositivoOrigen() + " -- " + c.getDispositivoDestino()
             + " : " + c.getInterfazOrigen() + " \\u2194 " + c.getInterfazDestino();
    }
}
