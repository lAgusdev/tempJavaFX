package com.java.tp.agency;
import com.java.tp.agency.travels.*;
import com.java.tp.agency.dataController.DataController;
import com.java.tp.agency.enums.Unoccupied;
import com.java.tp.agency.places.Place;
import com.java.tp.agency.responsables.*;
import com.java.tp.agency.vehicles.Vehicles;
import com.java.tp.agency.exceptions.*;
import java.util.*;
public class Agency {
    private  static  Agency instancia;
    private HashMap<String,Travel> viajes = new HashMap<>();    //ordenado por id hashmap
    private TreeMap<String,Place>destinos=new TreeMap<>();    //treemap por que el enunciado pide que esten ordenados alfabeticamente
    private HashMap<String,Vehicles>vehiculos=new HashMap<>(); //ordenado por patente
    private HashMap<String,Responsable>responsables=new HashMap<>(); //ordenado por dni
    private HashMap<String,Integer>cantViajes= new HashMap<>();
    private HashMap<String,Float>resKm= new HashMap<>(); //Hashmap para acumular, List para mostrar

    private Agency() {
        System.out.println("Se creo la instancia Singleton de agencia");
        try {
            new DataController().iniciaxml(this);
        } catch (Exception e) {
            System.out.println("Error al cargar datos en init(): " + e.getMessage());
        }
    }

    public static Agency getInstancia(){
        if (instancia==null) {
            instancia = new Agency();
        }
        return instancia;
    }

    public void ActualizaResKm(String dniResponsableABordo, float kmRecorridos){
        kmRecorridos+= resKm.getOrDefault(dniResponsableABordo, 0.0f);
        resKm.put(dniResponsableABordo, kmRecorridos); //remplaza el viejo por el nuevo
    }

    public ArrayList<ResponsableKM> obtenerRankingPorKm(HashMap<String, Float> resKm) {
        ArrayList<ResponsableKM> Ranking = new ArrayList<>();
        for (HashMap.Entry<String,Float> objeto : resKm.entrySet()) {
            String dni = objeto.getKey();
            Float km = objeto.getValue();
            ResponsableKM resumen = new ResponsableKM(dni, km);
            Ranking.add(resumen);
        }
        Ranking.sort(Comparator.comparing(ResponsableKM::getKmRecorridos).reversed());
        return Ranking;
    }

    //getters
    public HashMap<String,Responsable> getResponsables() {return responsables;}
    public HashMap<String, Integer> getCantViajes() {return cantViajes;}
    public HashMap<String, Travel> getViajes() {return viajes;}
    public TreeMap<String,Place> getDestinos(){return destinos;}
    public HashMap<String,Vehicles> getVehiculos(){return vehiculos;}
    //fin getter

    public HashMap<String, Vehicles> VehiculosDisponibles(){ //devuelve una lista con los vehiculos disponibles
        HashMap<String, Vehicles> aux=new HashMap<>();
        for(Vehicles v: vehiculos.values()){
            if(v.getEstado()== Unoccupied.DISPONIBLE){
                aux.put(v.getPatente(),v);
            }
        }
        if (aux.isEmpty()) {
            throw new SinVehiculosDisponiblesException("No hay vehículos disponibles");
        }
        return aux;

    }
    public HashMap<String, Responsable> ResponsablesDisponibles(){ //devuelve una lista con los responsables disponibles
        HashMap<String, Responsable> aux=new HashMap<>();
        for(Responsable r: responsables.values()){
            if(r.getEstado()== Unoccupied.DISPONIBLE){
                aux.put(r.getDni(),r);
            }
        }
        if (aux.isEmpty()) {
            throw new SinResponsablesDisponiblesException("No hay responsables disponibles");
        }
        return aux;
    }
    public void muestraViajes(){
        for(Travel v: viajes.values()){
            System.out.println(v.getId()+" || "+v.getIdDestino() + " || " + v.getEstado());
        }
    }
    public void crearViaje(String destino,String patVeh ,int pasajeros,float kmRec, TreeSet <String>res){
        Travel viajenuevo;
        Place desAct=destinos.get(destino);
        String id=creaIdViaje(destino);
        System.out.println("=== DEBUG crearViaje ===");
        if(desAct.getKm()<100){//viaje corta
            viajenuevo =new ShortDis(id,patVeh,destino,pasajeros,kmRec);
            System.out.println("=== viaje corta ===");
        }else{
            if(res.isEmpty()){
                throw new SinResLargaDisException("la lista de responsables esta vacia");
            }
            viajenuevo =new LongDis(id,patVeh,destino,pasajeros,kmRec,res);
        }

        viajes.put(id,viajenuevo);
        Vehicles v=vehiculos.get(patVeh);
        v.setEstado(Unoccupied.OCUPADO);
        if (res != null) {
            for (String r : res) {
                responsables.get(r).setEstado(Unoccupied.OCUPADO);
            }
        }
    }
    
    public void inciaCV(){// el contador de destinos empieza en 1 osea "Mardelplata-1" "Mardelplata-2"...
        String destinoaux;
        for(Travel v: viajes.values()){
            destinoaux=v.getId();
            if(!cantViajes.containsKey(destinoaux)){ //no contiene el destino
                cantViajes.put(destinoaux,1);
                System.out.println("se creo el destino :"+destinoaux);
            }else{ //contiene el destino
                cantViajes.put(destinoaux, cantViajes.get(destinoaux) + 1);
            }
        }
    }

    public String creaIdViaje(String destino){
    // Ensure we have a counter for this destination. If absent, start at 1.
        int contador = cantViajes.getOrDefault(destino, 1);
        String id = destino + "-" + contador;
        // Store next counter value
        cantViajes.put(destino, contador + 1);
        return id;
    }

    public void saveTravelsData() {
        try {
            new DataController().serializaViajes();
        } catch (Exception e) {
            System.out.println("Error al guardar datos de viajes: " + e.getMessage());
        }
    }
}