/**
 * Esta subclase que es heredada de Vehicles y representa los Colectivos Coche-Cama con su ID, patente del vehículo, destino, cantidad de pasajeros, estado y kilómetros recorridos.
 * Es utilizada para determinar la cantidad de ambos tipos de asientos, y la informacion obtenida del parser JAXB.
 * A su vez contiene el metodo calculaCosto con la implementación específica para Colectivos Coche-Cama.
 * Y al mismo tiempo tienen el metodo calcularCamasOptimas que determina la cantidad de pasajeros en camas y asientos normales.
 */
package com.java.tp.agency.vehicles;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "colectivoCC")
public class BusCC extends Vehicles{
    
    public static final int CAPACIDAD = 32;
    public static final int CAMAS_DISPONIBLES = 26;
    public static final int ASIENTOS_COMUNES_MAX = 6;
    private float valPasajeroAsiento;
    private float valPasajeroCama;

    public BusCC(String patente,int capacidad,float velPerH){
        super(patente,capacidad,velPerH);
        setCapacidad(32); //pasajeros-cantCamas <= 6, porque solo 6 plazas son comunes
    }
    @Override
    public float calculaCosto(float km, int pasajeros) {
        if(pasajeros <= CAMAS_DISPONIBLES) {
            return (valPasajeroAsiento * pasajeros * km) + (valPasajeroCama * pasajeros * km);
        } else
            return (valPasajeroAsiento * pasajeros * km) + (valPasajeroCama * CAMAS_DISPONIBLES * km);
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
    
    public static int calcularCamasOptimas(int pasajeros) {
        // Calcular camas: máximo 26, pero asegurando que queden máximo 6 asientos comunes
        int cantCamas = Math.min(pasajeros, CAMAS_DISPONIBLES);
        
        // Si los asientos comunes superan 6, ajustar las camas
        int asientosComunes = pasajeros - cantCamas;
        if (asientosComunes > ASIENTOS_COMUNES_MAX) {
            cantCamas = pasajeros - ASIENTOS_COMUNES_MAX;
        }
        
        return cantCamas;
    }
}