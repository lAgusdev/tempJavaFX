package com.java.tp.agency.responsables;
import com.java.tp.agency.exceptions.ResponsableInvalidoException;
import com.java.tp.agency.enums.Unoccupied;
import jakarta.xml.bind.annotation.*;


@XmlRootElement(name = "responsable")
public class Responsable {
    private String nombre;
    private String dni;
    private float salario;
    @XmlTransient
    private Unoccupied estado;

    public Responsable(String inDni, String inNombre,float inSalario){
        if(inDni.length()!=8 || !inDni.matches("[0-9]{8}")){
            throw new ResponsableInvalidoException("dni invalido");
        }
        if(inSalario<=0){
            throw new ResponsableInvalidoException("salario negativo");
        }
        salario=inSalario;
        dni = inDni;
        nombre = inNombre;
    }

    public Responsable() {this.estado=Unoccupied.DISPONIBLE;}
    //getters
    @XmlElement
    public float getSalario() {return salario;}
    @XmlElement
    public String getDni(){return dni;}
    @XmlElement
    public String getNombre() {return nombre;}
    public Unoccupied getEstado(){return estado;}
    //
    //setters
    public void setSalario(float salario) { this.salario = salario; }
    public void setDni(String dni) { this.dni = dni; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    @XmlTransient
    public void setEstado(Unoccupied disponibilidad){this.estado=disponibilidad;}
    //
}