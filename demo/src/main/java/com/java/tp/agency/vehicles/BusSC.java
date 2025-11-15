package com.java.tp.agency.vehicles;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "colectivoSC")
public class BusSC extends Vehicles{
    
    public static final int CAPACIDAD = 40;
    
    private float valPasajero;

    public BusSC(String patente,int capacidad,float velPerH){
        super(patente,capacidad,velPerH);
        setCapacidad(40);
    }
    public BusSC(){
        super();
        setCapacidad(40);
    }
    //Getters
    @XmlElement
    public float getValPasajero() {return valPasajero;}
    //
    //Seters
    public void setValPasajero(float valPasajero) {this.valPasajero = valPasajero;}
    //
    @Override
    public float calculaCosto(float km, int cantPas){
        return valPasajero*km*cantPas;
    }
}
