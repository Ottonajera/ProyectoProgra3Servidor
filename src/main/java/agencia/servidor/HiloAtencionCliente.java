/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agencia.servidor;

/**
 *
 * @author User
 */
import agencia.estructuras.ArbolBPlus;
import agencia.estructuras.ManejadorColas;
import agencia.modelos.TicketViaje;
import agencia.modelos.RegistroAtencion;

import java.net.Socket;
import java.io.*;

public class HiloAtencionCliente implements Runnable {
    private Socket socket;
    private InterfazServidor interfaz;
    private ArbolBPlus arbol;
    private String idConexion;

    public HiloAtencionCliente(Socket socket, InterfazServidor interfaz, ArbolBPlus arbol) {
        this.socket = socket;
        this.interfaz = interfaz;
        this.arbol = arbol;
        this.idConexion = "IP: " + socket.getInetAddress().getHostAddress() + " - Puerto: " + socket.getPort();
    }

    @Override
    public void run() {
        try {
            interfaz.agregarConexion("Conectado -> " + idConexion);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                String comando = (String) in.readObject();

                switch (comando) {
                    case "REGISTRAR":
                        TicketViaje nuevoTicket = (TicketViaje) in.readObject();
                        ManejadorColas.encolar(nuevoTicket);
                        ServidorCentral.ventanaServidor.registrarLog("Nuevo pasajero registrado y esperando en la cola con ticekt: [" + nuevoTicket.getNumeroTicket() + "]");
                        out.writeObject("Ticket " + nuevoTicket.getNumeroTicket() + " encolado con éxito.");
                        break;
                    case "SOLICITAR_GENERAL":
                    Object atendidoG = ManejadorColas.atenderGeneral();

                    if (atendidoG != null) {
                    String textoTicket = "";
        
                    if (atendidoG instanceof agencia.modelos.TicketViaje) {
                    textoTicket = ((agencia.modelos.TicketViaje) atendidoG).getNumeroTicket();
                    } else {
                    textoTicket = atendidoG.toString(); // Por si tu cola devuelve Strings o Nodos
                    }
        
                    ServidorCentral.ventanaServidor.registrarLog("Agente Rodrigo Ovando atendiendo ticket: [" + textoTicket + "]");
                    out.writeObject(atendidoG); // Le mandamos a la PC el objeto original sin alterarlo
                } else {
                ServidorCentral.ventanaServidor.registrarLog("Agente Rodrigo intentó atender, pero la cola está vacía.");
                out.writeObject(null);
        }
        out.flush();
        break;

        case "SOLICITAR_PRIORITARIO":
            Object atendidoP = ManejadorColas.atenderPrioritario();
    
        if (atendidoP != null) {
            String textoTicket = "";
        if (atendidoP instanceof agencia.modelos.TicketViaje) {
            textoTicket = ((agencia.modelos.TicketViaje) atendidoP).getNumeroTicket();
        } else {
            textoTicket = atendidoP.toString();
        }
        
        ServidorCentral.ventanaServidor.registrarLog("Agente Jorge atendiendo ticket Prioritario: [" + textoTicket + "]");
        out.writeObject(atendidoP);
        } else {
        ServidorCentral.ventanaServidor.registrarLog("Agente Jorge intentó atender, pero la cola Prioritaria está vacía.");
        out.writeObject(null);
        }
        out.flush();
        break;

    case "SOLICITAR_ESPECIAL":
    Object atendidoE = ManejadorColas.atenderEspecial();
    
    if (atendidoE != null) {
        String textoTicket = "";
        if (atendidoE instanceof agencia.modelos.TicketViaje) {
            textoTicket = ((agencia.modelos.TicketViaje) atendidoE).getNumeroTicket();
        } else {
            textoTicket = atendidoE.toString();
        }
        
        ServidorCentral.ventanaServidor.registrarLog("Agente Inge atendiendo ticket Especial: [" + textoTicket + "]");
        out.writeObject(atendidoE);
    } else {
        ServidorCentral.ventanaServidor.registrarLog("Agente Inge intentó atender, pero la cola Especial está vacía.");
        out.writeObject(null);
    }
    out.flush();
    break;
                    case "FINALIZAR_ATENCION":
    try {
        Object recibido = in.readObject();
        if (recibido == null) break;

        String datosCsv = recibido.toString();
        String[] p = datosCsv.split(",");
        String dpiLimpio = (p.length >= 2) ? p[1].trim() : "S/D";
        GestorArchivos.guardarRegistro(datosCsv);
        String historialPrevio = ServidorCentral.arbolHistorial.buscarPorDpi(dpiLimpio);
        String nuevoHistorial;

        if (historialPrevio != null && !historialPrevio.contains("No se encontro")) {
            nuevoHistorial = historialPrevio + "\n------------------------------------------\n" + datosCsv;
        } else {
            nuevoHistorial = datosCsv;
        }

        ServidorCentral.arbolHistorial.insertar(dpiLimpio, nuevoHistorial);
        ServidorCentral.ventanaServidor.registrarLog("Disco actualizado | DPI: " + dpiLimpio);
        out.writeObject("Registro guardado correctamente.");
        out.flush();

    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        try { out.writeObject("Error en servidor"); out.flush(); } catch (Exception ex) {}
    }
    break;
                case "TAMANO_COLA_PRIORITARIA":
                int tamano = agencia.estructuras.ManejadorColas.getTamanoColaPrioritaria();
                
                out.writeObject(tamano);
                out.flush();
                break;
                case "TAMANO_COLA_GENERAL":
                int tamanoG = agencia.estructuras.ManejadorColas.getTamanoColaGeneral();
                
                out.writeObject(tamanoG);
                out.flush();
                break;
                case "TAMANO_COLA_ESPECIAL":
                int tamanoE = agencia.estructuras.ManejadorColas.getTamanoColaEspecial();
                
                out.writeObject(tamanoE);
                out.flush();
                break;
                case "BUSCAR_HISTORIAL_DPI":
    try {
        Object objDpi = in.readObject();
        if (objDpi == null) break;
        
        String dpiBusqueda = objDpi.toString().trim();
        String resultado = GestorArchivos.buscarHistorialDirecto(dpiBusqueda);
        if (resultado != null && !resultado.contains("No se encontro")) {
            System.out.println("[OK] Se encontró historial para el DPI: " + dpiBusqueda);
            ServidorCentral.ventanaServidor.registrarLog("🔍 Historial enviado para DPI: " + dpiBusqueda);
        } else {
            System.out.println("[VACÍO] El árbol no tiene nada para el DPI: " + dpiBusqueda);
            resultado = "No se encontró historial para el DPI: " + dpiBusqueda;
        }
        out.writeObject(resultado);
        out.flush(); 

    } catch (Exception e) {
        System.err.println("Error en búsqueda: " + e.getMessage());
        try {
            out.writeObject("Error en el servidor al buscar.");
            out.flush();
        } catch (IOException ex) {}
    }
    break;
    case "DESCONEXION_REAL":
    // Sacamos la IP de la compu que nos está avisando que se apaga
    String ipQueSeVa = socket.getInetAddress().getHostAddress();
    
    // Llamamos al método fulminante que acabamos de crear
    ServidorCentral.ventanaServidor.desconexionInmediata(ipQueSeVa);
    ServidorCentral.ventanaServidor.registrarLog("Terminal " + ipQueSeVa + " ha cerrado el sistema.");
    break;
    
                }
            }

        } catch (EOFException e) {
           // System.out.println("La estación " + idConexion + " se ha desconectado limpiamente.");
        } catch (Exception e) {
            //System.err.println("Se perdió la conexión con " + idConexion);
            //System.out.println("Se perdió la conexión...");
    e.printStackTrace();
        } finally {
            interfaz.quitarConexion("Conectado -> " + idConexion);
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}