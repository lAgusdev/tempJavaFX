/**
 * Esta clase representa un vehiculo con su patente, capacidad, velocidad promedio por hora y estado.
 * Es utilizada para que el parser JAXB pueda mapear los datos XML a objetos Java. Lo que permite obtener los diferentes datos de cada destino.
 * Y a su vez contiene el metodo abstracto calculaCosto que lo implementan las subclases dependiendo del tipo de vehiculo.
 */

package com.java.tp.agency.vehicles;
import com.java.tp.agency.exceptions.VehiculoInvalidoException;
import com.java.tp.agency.enums.Unoccupied;
import jakarta.xml.bind.annotation. *;
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlSeeAlso({Car.class, MiniBus.class, BusSC.class, BusCC.class})
public abstract class Vehicles {
    private String patente;
    private int capacidad; // no lo usa json
    private float velPerH; //velocidad promedio por hora
    private Unoccupied estado; //enum no lo usa json

    public Vehicles(String patente,int capacidad,float velPerH){
        this.estado=Unoccupied.DISPONIBLE; //un auto creado siempre esta disponible
        if(!patente.matches("[0-9]{3}[A-Z]{3}") || !patente.matches("[0-9]{2}[A-Z]{3}[0-9]{2}")){
            throw new VehiculoInvalidoException("patente invalida"); //estos errores se controlan para el json esto desde el crea viaje no se puede
        }
        if(capacidad<=0){
            throw new VehiculoInvalidoException("capacidad por debajo de 1");
        }
        if(velPerH<=0){
            throw new VehiculoInvalidoException("velocidad invalida");
        }
        this.capacidad=capacidad;
        this.patente=patente;
        this.velPerH=velPerH;
    }
    public Vehicles(){
        this.estado=Unoccupied.DISPONIBLE;
    }
    //getters
    @XmlTransient
    public Unoccupied getEstado() {return estado;}
    @XmlElement
    public String getPatente(){return patente;}
    @XmlElement
    public float getVelPerH() {return velPerH;}
    @XmlTransient
    public int getCapacidad() {return capacidad;}

    //
    //setters
    public void setPatente(String patente) {this.patente = patente;}
    public void setVelPerH(float velPerH) {this.velPerH = velPerH;}
    public void setCapacidad(int capacidad) {this.capacidad = capacidad;}
    public void setEstado(Unoccupied disponibilidad){this.estado=disponibilidad;}
    //

    public abstract float calculaCosto(float km, int pasajeros);

}

