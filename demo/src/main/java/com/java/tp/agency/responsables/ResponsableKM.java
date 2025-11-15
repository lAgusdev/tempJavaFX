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