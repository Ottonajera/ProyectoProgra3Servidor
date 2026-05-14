/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agencia.estructuras;

/**
 *
 * @author User
 */
import java.util.ArrayList;
import java.util.List;

public class NodoBPlus {
    int orden;
    boolean esHoja;
    List<String> claves;        
    List<String> valores;       
    List<NodoBPlus> hijos;      
    NodoBPlus siguiente;        

    public NodoBPlus(int orden, boolean esHoja) {
        this.orden = orden;
        this.esHoja = esHoja;
        this.claves = new ArrayList<>();
        this.valores = new ArrayList<>();
        this.hijos = new ArrayList<>();
        this.siguiente = null;
    }
}
