/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agencia.estructuras;

import java.util.*;

/**
 *
 * @author User
 */
public class ArbolBPlus {
    private NodoBPlus raiz;
    private int orden;

    public ArbolBPlus(int orden) {
        this.orden = orden;
        this.raiz = new NodoBPlus(orden, true);
    }

    public String buscarPorDpi(String dpi) {
        NodoBPlus nodoActual = raiz;

        while (!nodoActual.esHoja) {
            boolean encontrado = false;
            for (int i = 0; i < nodoActual.claves.size(); i++) {
                if (dpi.compareTo(nodoActual.claves.get(i)) < 0) {
                    nodoActual = nodoActual.hijos.get(i);
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                nodoActual = nodoActual.hijos.get(nodoActual.claves.size());
            }
        }
        
        for (int i = 0; i < nodoActual.claves.size(); i++) {
            if (nodoActual.claves.get(i).equals(dpi)) {
                return nodoActual.valores.get(i); 
            }
        }
        
        return "No se encontró historial para el DPI: " + dpi;
    }

    public void insertar(String dpi, String valorCsv) {
        NodoBPlus hoja = buscarHojaParaInsertar(raiz, dpi);
        insertarEnHoja(hoja, dpi, valorCsv);
  
        if (hoja.claves.size() == orden) {
            dividirHoja(hoja);
        }
    }

    private NodoBPlus buscarHojaParaInsertar(NodoBPlus nodo, String dpi) {
        if (nodo.esHoja) {
            return nodo;
        }
        for (int i = 0; i < nodo.claves.size(); i++) {
            if (dpi.compareTo(nodo.claves.get(i)) < 0) {
                return buscarHojaParaInsertar(nodo.hijos.get(i), dpi);
            }
        }
        return buscarHojaParaInsertar(nodo.hijos.get(nodo.claves.size()), dpi);
    }

    private void insertarEnHoja(NodoBPlus hoja, String dpi, String valorCsv) {
    for (int i = 0; i < hoja.claves.size(); i++) {
        if (hoja.claves.get(i).equals(dpi)) {
            hoja.valores.set(i, valorCsv);
            System.out.println("Árbol B+: Historial actualizado para DPI: " + dpi);
            return; 
        }
    }
    int pos = 0;
    while (pos < hoja.claves.size() && hoja.claves.get(pos).compareTo(dpi) < 0) {
        pos++;
    }
    hoja.claves.add(pos, dpi);
    hoja.valores.add(pos, valorCsv);
    
    System.out.println("Árbol B+: Nuevo DPI registrado en hoja: " + dpi);
}

    private void dividirHoja(NodoBPlus hoja) {
        NodoBPlus nuevaHoja = new NodoBPlus(orden, true);
        int mid = orden / 2;

        List<String> clavesSubir = new ArrayList<>(hoja.claves.subList(mid, orden));
        List<String> valoresSubir = new ArrayList<>(hoja.valores.subList(mid, orden));

        nuevaHoja.claves.addAll(clavesSubir);
        nuevaHoja.valores.addAll(valoresSubir);

        hoja.claves.subList(mid, orden).clear();
        hoja.valores.subList(mid, orden).clear();

        nuevaHoja.siguiente = hoja.siguiente;
        hoja.siguiente = nuevaHoja;

        promoverPadre(hoja, nuevaHoja, nuevaHoja.claves.get(0));
    }

    private void promoverPadre(NodoBPlus hijoIzquierdo, NodoBPlus hijoDerecho, String clavePromovida) {
      
        if (hijoIzquierdo == raiz) {
            NodoBPlus nuevaRaiz = new NodoBPlus(orden, false);
            nuevaRaiz.claves.add(clavePromovida);
            nuevaRaiz.hijos.add(hijoIzquierdo);
            nuevaRaiz.hijos.add(hijoDerecho);
            raiz = nuevaRaiz;
        }
    }
}
