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
import java.io.*;

public class GestorArchivos {
public static void guardarRegistro(Object registro) {
    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("historial_atencion.txt", true)))) {
        out.println(registro.toString());
        out.flush(); 
        System.out.println("[FÍSICO] Línea escrita en el archivo.");
    } catch (IOException e) {
        System.err.println("Error físico de escritura: " + e.getMessage());
    }
}
    public static String buscarHistorialDirecto(String dpiBusqueda) {
    StringBuilder historialAcumulado = new StringBuilder();
    boolean encontrado = false;

    try (BufferedReader br = new BufferedReader(new FileReader("historial_atencion.txt"))) {
        String linea;
        while ((linea = br.readLine()) != null) {
            if (linea.trim().isEmpty()) continue;
            
            String[] datos = linea.split(",");
            if (datos.length >= 2 && datos[1].trim().equals(dpiBusqueda.trim())) {
                if (encontrado) {
                    historialAcumulado.append("\n------------------------------------------\n");
                }
                historialAcumulado.append(linea);
                encontrado = true;
            }
        }
    } catch (Exception e) {
        System.err.println("Error leyendo historial directo: " + e.getMessage());
    }

    return encontrado ? historialAcumulado.toString() : "No se encontró historial para el DPI: " + dpiBusqueda;
}
    public static void cargarArchivoAlArbol(String ruta, ArbolBPlus arbol) {
    File archivo = new File(ruta);
    System.out.println("Buscando archivo para cargar en: " + archivo.getAbsolutePath());

    if (!archivo.exists()) {
        System.out.println("El archivo no existe en esa ruta. Creando uno nuevo...");
        try { archivo.createNewFile(); } catch (IOException e) {}
        return;
    }

    try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
        String linea;
        int contador = 0;
        while ((linea = br.readLine()) != null) {
            if (linea.trim().isEmpty()) continue;
            String[] datos = linea.split(",");
            if (datos.length >= 2) {
                String dpi = datos[1].trim();
                String previo = arbol.buscarPorDpi(dpi);
                String contenido = (previo != null && !previo.contains("No se encontró")) 
                                   ? previo + "\n------------------------------------------\n" + linea 
                                   : linea;
                arbol.insertar(dpi, contenido);
                contador++;
            }
        }
        System.out.println("¡EXITO! Se cargaron " + contador + " registros antiguos.");
    } catch (IOException e) {
        System.err.println("Error al cargar: " + e.getMessage());
    }
}
}