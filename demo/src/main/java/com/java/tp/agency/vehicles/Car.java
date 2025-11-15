package com.java.tp.agency.vehicles;
import jakarta.xml.bind.annotation. *;
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "auto")
public class Car extends Vehicles {

    public static final int CAPACIDAD = 4;
    
    private float valBase;
    private float valKm;

    public Car(String patente,int capacidad,float velPerH){
        super(patente,capacidad,velPerH);
        setCapacidad(4);
    }
    public Car(){
        super();
        setCapacidad(4);
    }
    //Getters
    @XmlElement
    public float getValBase() {return valBase;}
    @XmlElement
    public float getValKm(){return valKm;}
    //
    //Setters
    public void setValBase(float valBase){this.valBase=valBase;}
    public void setValKm(float valKm){this.valKm=valKm;}
    //
    @Override
    public float calculaCosto(float km, int cantpas){
        return valBase + valKm * km;
    }

}

