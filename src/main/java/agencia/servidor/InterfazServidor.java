    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agencia.servidor;

/**
 *
 * @author User
 */
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterfazServidor extends JFrame {
    
    private JPanel panelContenedorPCs;
    private HashMap<String, CajitaPC> mapaCajitas;
    private HashMap<String, javax.swing.Timer> timersDesconexion;
    private JTextArea areaConsola;

    public InterfazServidor() {
        try {
            setTitle("Dashboard Servidor Central - Viajes Globales");
            
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setSize(1200, 700); 
            
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null); 
            
            mapaCajitas = new HashMap<>();
            timersDesconexion = new HashMap<>();
            
            JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
            panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            panelPrincipal.setBackground(new Color(25, 25, 25));
            
            JLabel lblTitulo = new JLabel("Panel de Control y Monitoreo en Tiempo Real", SwingConstants.CENTER);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
            lblTitulo.setForeground(new Color(0, 153, 255)); 
            panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

            panelContenedorPCs = new JPanel();
            panelContenedorPCs.setLayout(new BoxLayout(panelContenedorPCs, BoxLayout.Y_AXIS));
            panelContenedorPCs.setBackground(new Color(30, 30, 30));
            
            JScrollPane scrollIzquierdo = new JScrollPane(panelContenedorPCs);
            scrollIzquierdo.setBorder(null);
            
            JPanel panelIzquierdo = new JPanel(new BorderLayout());
            panelIzquierdo.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(70, 70, 70)), 
                    " Estado de la Red ", 
                    javax.swing.border.TitledBorder.LEFT, 
                    javax.swing.border.TitledBorder.TOP, 
                    new Font("Consolas", Font.BOLD, 16), 
                    Color.WHITE));
            panelIzquierdo.setPreferredSize(new Dimension(450, 0)); 
            panelIzquierdo.setBackground(new Color(30, 30, 30));
            panelIzquierdo.add(scrollIzquierdo, BorderLayout.CENTER);
            
            panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);

            JPanel panelDerecho = new JPanel(new BorderLayout());
            panelDerecho.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(70, 70, 70)), 
                    " Registro de Actividad ", 
                    javax.swing.border.TitledBorder.LEFT, 
                    javax.swing.border.TitledBorder.TOP, 
                    new Font("Consolas", Font.BOLD, 16), 
                    Color.WHITE));
            panelDerecho.setBackground(new Color(30, 30, 30));
            
            areaConsola = new JTextArea();
            areaConsola.setEditable(false); 
            areaConsola.setBackground(new Color(15, 15, 15)); 
            areaConsola.setForeground(new Color(0, 255, 0));  
            areaConsola.setFont(new Font("Consolas", Font.PLAIN, 18)); 
            areaConsola.setLineWrap(true);       
            areaConsola.setWrapStyleWord(true);
            areaConsola.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JScrollPane scrollDerecho = new JScrollPane(areaConsola);
            scrollDerecho.setBorder(null);
            panelDerecho.add(scrollDerecho, BorderLayout.CENTER);
            
            panelPrincipal.add(panelDerecho, BorderLayout.CENTER);
            
            add(panelPrincipal);
            
            inicializarCajasFijas();
            
            registrarLog("Servidor inicializado. Sistema B+ cargado correctamente.");
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void inicializarCajasFijas() {
        CajitaPC cajaServidor = new CajitaPC("SERVIDOR CENTRAL", "127.0.0.1");
        cajaServidor.setEstado(true); 
        cajaServidor.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 153, 255), 2), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panelContenedorPCs.add(cajaServidor);
        panelContenedorPCs.add(Box.createRigidArea(new Dimension(0, 15)));
        
        agregarCajaFija("192.168.1.22", "PC2 - Registro");
        agregarCajaFija("172.20.10.6", "PC3 - Agente General"); 
        agregarCajaFija("172.20.10.12", "PC4 - Agente Prioritario");
        
        agregarCajaFija("IP_DINAMICA_PC5", "PC5 - Agente Inge (Esperando...)"); 
    }

    private void agregarCajaFija(String ip, String nombrePC) {
        CajitaPC nuevaCajita = new CajitaPC(nombrePC, ip);
        nuevaCajita.setEstado(false); 
        mapaCajitas.put(ip, nuevaCajita);
        panelContenedorPCs.add(nuevaCajita);
        panelContenedorPCs.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    public void agregarConexion(String idTerminal) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (idTerminal.contains("Servidor Activo") || idTerminal.contains("===")) return;

            String ipLimpia = extraerIP(idTerminal);
            
            if (timersDesconexion.containsKey(ipLimpia)) {
                timersDesconexion.get(ipLimpia).stop();
                timersDesconexion.remove(ipLimpia);
            }
            if (mapaCajitas.containsKey(ipLimpia)) {
                mapaCajitas.get(ipLimpia).setEstado(true);
            } else {
                if (mapaCajitas.containsKey("IP_DINAMICA_PC5")) {
                    CajitaPC cajaIngeniero = mapaCajitas.remove("IP_DINAMICA_PC5");
                    
                    cajaIngeniero.setTitulo("PC5 - Agente Inge", ipLimpia);
                    cajaIngeniero.setEstado(true);
                    
                    mapaCajitas.put(ipLimpia, cajaIngeniero);
                    
                } else {
                    agregarCajaFija(ipLimpia, "Terminal Invitada");
                    mapaCajitas.get(ipLimpia).setEstado(true);
                    panelContenedorPCs.revalidate();
                }
            }
        });
    }

    public void desconexionInmediata(String idTerminal) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            String ipLimpia = extraerIP(idTerminal);
            if (timersDesconexion.containsKey(ipLimpia)) {
                timersDesconexion.get(ipLimpia).stop();
                timersDesconexion.remove(ipLimpia);
            }
            if (mapaCajitas.containsKey(ipLimpia)) {
                mapaCajitas.get(ipLimpia).setEstado(false);
            }
        });
    }

    public void quitarConexion(String idTerminal) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            String ipLimpia = extraerIP(idTerminal);
            
            if (mapaCajitas.containsKey(ipLimpia)) {
                int tiempoDeGracia = 15000; 
                if (ipLimpia.equals("192.168.231.1") || mapaCajitas.get(ipLimpia).getNombrePC().contains("Monitor")) {
                    tiempoDeGracia = 4000; 
                }

                javax.swing.Timer timer = new javax.swing.Timer(tiempoDeGracia, e -> {
                    mapaCajitas.get(ipLimpia).setEstado(false); 
                    timersDesconexion.remove(ipLimpia);
                });
                timer.setRepeats(false);
                timer.start();
                timersDesconexion.put(ipLimpia, timer);
            }
        });
    }
    
    public void registrarLog(String mensaje) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
                areaConsola.append("[" + hora + "] " + mensaje + "\n");
                areaConsola.setCaretPosition(areaConsola.getDocument().getLength());
            } catch (Exception e) {}
        });
    }
    
    private String extraerIP(String cadena) {
        if (cadena == null) return "Unknown";
        Pattern p = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");
        Matcher m = p.matcher(cadena);
        if (m.find()) {
            return m.group(1);
        }
        return cadena.trim(); 
    }

    class CajitaPC extends JPanel {
        private boolean conectado;
        private JLabel lblEstado;
        private JLabel lblTitulo; 
        private String nombreBase;

        public CajitaPC(String nombrePC, String ip) {
            this.conectado = false;
            this.nombreBase = nombrePC;
            
            setLayout(new BorderLayout(15, 10));
            setBackground(new Color(45, 48, 56)); 
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));
            
            setMaximumSize(new Dimension(420, 80));
            setPreferredSize(new Dimension(420, 80));

            JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 5));
            pnlInfo.setOpaque(false);
            
            String icono = nombrePC.contains("SERVIDOR") ? "🛡️ " : "🖥️ ";
            
            lblTitulo = new JLabel(icono + nombrePC + (ip.contains("IP_DINAMICA") ? "" : " (" + ip + ")"));
            lblTitulo.setForeground(Color.WHITE);
            lblTitulo.setFont(new Font("Consolas", Font.BOLD, 15));

            lblEstado = new JLabel("Desconectado");
            lblEstado.setForeground(new Color(255, 60, 60));
            lblEstado.setFont(new Font("Consolas", Font.PLAIN, 14));

            pnlInfo.add(lblTitulo);
            pnlInfo.add(lblEstado);

            JPanel pnlCirculo = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (conectado) {
                        g2.setColor(new Color(0, 255, 100, 50));
                        g2.fillOval(2, 12, 24, 24);
                        g2.setColor(new Color(0, 255, 100));
                        g2.fillOval(5, 15, 18, 18);
                    } else {
                        g2.setColor(new Color(255, 60, 60)); 
                        g2.fillOval(5, 15, 18, 18);
                    }
                }
            };
            pnlCirculo.setPreferredSize(new Dimension(30, 50));
            pnlCirculo.setOpaque(false);

            add(pnlInfo, BorderLayout.CENTER);
            add(pnlCirculo, BorderLayout.EAST);
        }

        public void setTitulo(String nuevoNombre, String ipReal) {
            this.nombreBase = nuevoNombre;
            lblTitulo.setText("🖥️ " + nuevoNombre + " (" + ipReal + ")");
            repaint();
        }
        
        public String getNombrePC() {
            return this.nombreBase;
        }

        public void setEstado(boolean activo) {
            this.conectado = activo;
            if (activo) {
                lblEstado.setText("Conectado y Operativo");
                lblEstado.setForeground(new Color(0, 255, 100));
            } else {
                lblEstado.setText("Desconectado");
                lblEstado.setForeground(new Color(255, 60, 60));
            }
            repaint(); 
        }
    }
}