/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agencia.servidor.chat;

/**
 *
 * @author User
 */
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ManejadorChat implements Runnable {
    private static final int PUERTO_CHAT = 5001;
    private static List<ObjectOutputStream> clientesActivos = new CopyOnWriteArrayList<>();

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO_CHAT)) {
            System.out.println("=== Servidor de Chat Interno activo en el puerto " + PUERTO_CHAT + " ===");
            
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new HiloReceptorMensajes(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void transmitirATodos(String mensaje) {
        for (ObjectOutputStream out : clientesActivos) {
            try {
                out.writeObject(mensaje);
                out.flush();
            } catch (Exception e) {
                clientesActivos.remove(out);
            }
        }
    }

    private static class HiloReceptorMensajes implements Runnable {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public HiloReceptorMensajes(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                clientesActivos.add(out);
                
                in = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    String mensaje = (String) in.readObject();
                    ManejadorChat.transmitirATodos(mensaje);
                }
            } catch (Exception e) {
                clientesActivos.remove(out); 
            }
        }
    }
}
