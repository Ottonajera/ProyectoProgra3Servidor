/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agencia.modelos;

/**
 *
 * @author User
 */

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroAtencion implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDateTime fechaHoraAtencion;
    private String dpi;
    private String nombreApellido;
    private String motivoAtencion;
    private long duracionAtencion; 
    private long duracionTotal;    
    private String agenteAtencion;
    private String comentarios;

    public RegistroAtencion(String dpi, String nombreApellido, String motivoAtencion, 
                            long duracionAtencion, long duracionTotal, String agenteAtencion, String comentarios) {
        this.fechaHoraAtencion = LocalDateTime.now();
        this.dpi = dpi;
        this.nombreApellido = nombreApellido;
        this.motivoAtencion = motivoAtencion;
        this.duracionAtencion = duracionAtencion;
        this.duracionTotal = duracionTotal;
        this.agenteAtencion = agenteAtencion;
        this.comentarios = comentarios;
    }

    // --- Getters ---
    public LocalDateTime getFechaHoraAtencion() { return fechaHoraAtencion; }
    public String getDpi() { return dpi; }
    public String getNombreApellido() { return nombreApellido; }
    public String getMotivoAtencion() { return motivoAtencion; }
    public long getDuracionAtencion() { return duracionAtencion; }
    public long getDuracionTotal() { return duracionTotal; }
    public String getAgenteAtencion() { return agenteAtencion; }
    public String getComentarios() { return comentarios;}

    public String toCSV() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return fechaHoraAtencion.format(formatter) + "," +
               dpi + "," +
               nombreApellido + "," +
               motivoAtencion + "," +
               duracionAtencion + "," +
               duracionTotal + "," +
               agenteAtencion + ","+
                comentarios;
    }

    @Override
    public String toString() {
        return "RegistroAtencion{" +
               "DPI='" + dpi + '\'' +
               ", Nombre='" + nombreApellido + '\'' +
               ", Atendido por='" + agenteAtencion + '\'' +
               ", Duración Total=" + duracionTotal + " min}";
    }
}
