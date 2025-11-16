/**
 * Esta subclase que es heredada de Vehicles y representa las Combis con su ID, patente del vehículo, destino, cantidad de pasajeros, estado y kilómetros recorridos.
 * Es utilizada para determinar cantidad de los asientos. Y la informacion obtenida del parser JAXB.
 * Y a su vez contiene el metodo calculaCosto con la implementación específica para combis.
 */
package com.java.tp.agency.vehicles;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "combi")
public class MiniBus extends Vehicles {
    
    public static final int CAPACIDAD = 16;
    
    private float valBase;
    private float valPasajero;

    public MiniBus(String patente,int capacidad,float velPerH) {
        super(patente,capacidad,velPerH);
        setCapacidad(16);
    }
    public MiniBus(){
        super();
        setCapacidad(16);
    }

    //Geters
    @XmlElement
    public float getValPasajero() {return valPasajero;}
    @XmlElement
    public float getValBase() {return valBase;}
    //
    //Setters
    public void setValBase(float valBase) {this.valBase = valBase;}
    public void setValPasajero(float valPasajero) {this.valPasajero = valPasajero;}
    //
    @Override
    public float calculaCosto(float km, int pasajeros) {
        return valBase + km * valPasajero * pasajeros;
    }

}