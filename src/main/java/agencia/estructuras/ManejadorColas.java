/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agencia.estructuras;

/**
 *
 * @author User
 */
import agencia.modelos.TicketViaje;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class ManejadorColas {

    private static Queue<TicketViaje> colaGeneral = new ConcurrentLinkedQueue<>();
    private static Queue<TicketViaje> colaEspecial = new ConcurrentLinkedQueue<>();

    public static int getTamanoColaPrioritaria() {
        if (colaPrioritaria == null) {
            return 0; 
        }
        return colaPrioritaria.size();
    }
    public static int getTamanoColaGeneral() {
        if (colaGeneral == null) {
            return 0;
        }
        return colaGeneral.size();
    }
    public static int getTamanoColaEspecial() {
        if (colaEspecial == null) {
            return 0;
        }
        return colaEspecial.size();
    }
 
    private static PriorityBlockingQueue<TicketViaje> colaPrioritaria = new PriorityBlockingQueue<>();

    public static void encolar(TicketViaje ticket) {
        if (ticket == null) return;
        
        switch (ticket.getTipo().toUpperCase()) {
            case "GENERAL":
                colaGeneral.add(ticket);
                break;
            case "PRIORITARIO":
                colaPrioritaria.put(ticket);
                break;
            case "ESPECIAL":
                colaEspecial.add(ticket);
                break;
            default:
                System.err.println("Tipo de ticket desconocido: " + ticket.getTipo());
        }
        System.out.println("Encolado exitosamente: " + ticket.getNumeroTicket() + " en " + ticket.getTipo());
    }

    public static TicketViaje atenderGeneral() {
        return colaGeneral.poll();
    }

    public static TicketViaje atenderPrioritario() {
        return colaPrioritaria.poll();
    }

    public static TicketViaje atenderEspecial() {
        return colaEspecial.poll();
    }
}
