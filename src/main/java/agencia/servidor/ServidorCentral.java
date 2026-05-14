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
import agencia.configuracion.ConfiguracionRed;
import agencia.configuracion.TemaAgencia;
import agencia.servidor.chat.ManejadorChat;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorCentral {
    
    public static ArbolBPlus arbolHistorial = new ArbolBPlus(4);
    public static InterfazServidor ventanaServidor;

    public static void main(String[] args) {
        TemaAgencia.aplicar();

        GestorArchivos.cargarArchivoAlArbol("historial_atencion.txt", arbolHistorial);

        ventanaServidor = new InterfazServidor();
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            ventanaServidor.setVisible(true);
        });

        ConfiguracionRed config = new ConfiguracionRed();
        int puerto = config.getPuerto();

        Thread hiloChat = new Thread(new ManejadorChat());
        hiloChat.start();

        ExecutorService pool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            ventanaServidor.agregarConexion("=== Servidor Activo (Puerto: " + puerto + ") ===");

            while (true) {
                Socket socketCliente = serverSocket.accept();
                pool.execute(new HiloAtencionCliente(socketCliente, ventanaServidor, arbolHistorial));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}