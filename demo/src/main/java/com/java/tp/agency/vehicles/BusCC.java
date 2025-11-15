package com.java.tp.agency.vehicles;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "colectivoCC")
public class BusCC extends Vehicles{
    private float valPasajeroCama;
    private  float valPasajeroAsiento;

    public BusCC(String patente,int capacidad,float velPerH){
        super(patente,capacidad,velPerH);
        setCapacidad(32); //pasajeros-cantCamas <= 6, porque solo 6 plazas son comunes
    }
    @Override
    public float calculaCosto(float km, int pasajeros, int cantCamas) {
        return (valPasajeroAsiento * pasajeros * km) + (valPasajeroCama * cantCamas * km);
    }
    public BusCC(){
        super();
        setCapacidad(32);
    }
    //Getters
    @XmlElement
    public float getValPasajeroAsiento() {return valPasajeroAsiento;}
    @XmlElement
    public float getValPasajeroCama() {return valPasajeroCama;}
    //
    //Setters
    public void setValPasajeroAsiento(float valPasajeroAsiento) {this.valPasajeroAsiento = valPasajeroAsiento;}
    public void setValPasajeroCama(float valPasajeroCama) {this.valPasajeroCama = valPasajeroCama;}
    //
}