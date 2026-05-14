/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agencia.configuracion;

/**
 *
 * @author User
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfiguracionRed {
    
    private String ipServidor;
    private int puerto;

    public ConfiguracionRed() {
        Properties propiedades = new Properties();
        
        try (FileInputStream entrada = new FileInputStream("config.properties")) {
            propiedades.load(entrada);
            
            this.ipServidor = propiedades.getProperty("IP_SERVIDOR", "127.0.0.1");
            
            String puertoStr = propiedades.getProperty("PUERTO_SERVIDOR", 
                               propiedades.getProperty("PUERTO", "5000"));
            
            this.puerto = Integer.parseInt(puertoStr);
            
        } catch (IOException ex) {
            System.err.println("Advertencia: No se encontró 'config.properties'.");
            System.err.println("Aplicando configuración por defecto de red local (127.0.0.1 : 5000).");
            this.ipServidor = "127.0.0.1";
            this.puerto = 5000;
        } catch (NumberFormatException ex) {
            System.err.println("Error: El puerto definido en config.properties no es un número válido.");
            this.ipServidor = "127.0.0.1";
            this.puerto = 5000;
        }
    }

    
    public String getIpServidor() {
        return ipServidor;
    }

    public int getPuerto() {
        return puerto;
    }
}
