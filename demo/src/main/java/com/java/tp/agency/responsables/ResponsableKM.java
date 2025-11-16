/**
 * Esta clase representa lo mismo que la clase Responsable pero ademas guarda los kilometros recorridos por cada responsable.
 * Es utilizada para no repetir el codigo para calcular los kilometros totales recorridos por cada responsable.
 */
package com.java.tp.agency.responsables;

public class ResponsableKM {
    private String dni;
    private float KmRecorridos;

    public ResponsableKM(String dni, float KmRecorridos){
        this.dni=dni;
        this.KmRecorridos=KmRecorridos;
    }
    //getters
    public String getDni() {return dni;}
    public float getKmRecorridos() {return KmRecorridos;}
}