/**
 * Esta subclase representa las Combis Sin-Cama con su patente, capacidad, velocidad promedio por hora y estado.
 * Es utilizada para que el parser JAXB pueda mapear los datos XML a objetos Java. Lo que permite obtener los diferentes datos de cada destino.
 * Y a su vez contiene el metodo abstracto calculaCosto con la implementación específica para Sin-Cama.
 */
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
