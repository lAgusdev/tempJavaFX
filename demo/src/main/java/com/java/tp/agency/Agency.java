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
    
    /**
     * Retorna un mapa con las distancias de todos los destinos
     */
    public Map<String, Integer> getDistancias() {
        Map<String, Integer> distancias = new TreeMap<>();
        for (Place destino : destinos.values()) {
            distancias.put(destino.getId(), (int) destino.getKm());
        }
        return distancias;
    }
    
    /**
     * Retorna una lista de vehículos disponibles para viajes de larga distancia
     * (Combi, Semi-Cama, Coche-Cama)
     */
    public List<String> getVehiculosParaLargaDistancia() {
        List<String> vehiculosLarga = new ArrayList<>();
        HashMap<String, Vehicles> disponibles = VehiculosDisponibles();
        
        for (Vehicles vehiculo : disponibles.values()) {
            int capacidad = vehiculo.getCapacidad();
            String patente = vehiculo.getPatente();
            String descripcion;
            
            switch (capacidad) {
                case 32: // Coche Cama
                    descripcion = "Colectivo Coche Cama: " + patente;
                    vehiculosLarga.add(descripcion);
                    break;
                case 40: // Semi Cama
                    descripcion = "Colectivo Semi Cama: " + patente;
                    vehiculosLarga.add(descripcion);
                    break;
                case 16: // Minibus
                    descripcion = "Combi: " + patente;
                    vehiculosLarga.add(descripcion);
                    break;
            }
        }
        
        return vehiculosLarga;
    }
    
    /**
     * Retorna una lista de vehículos disponibles para viajes de corta distancia
     * (Auto, Combi, Semi-Cama)
     */
    public List<String> getVehiculosParaCortaDistancia() {
        List<String> vehiculosCorta = new ArrayList<>();
        HashMap<String, Vehicles> disponibles = VehiculosDisponibles();
        
        for (Vehicles vehiculo : disponibles.values()) {
            int capacidad = vehiculo.getCapacidad();
            String patente = vehiculo.getPatente();
            String descripcion;
            
            switch (capacidad) {
                case 4: // Auto
                    descripcion = "Auto: " + patente;
                    vehiculosCorta.add(descripcion);
                    break;
                case 16: // Minibus
                    descripcion = "Combi: " + patente;
                    vehiculosCorta.add(descripcion);
                    break;
                case 40: // Semi Cama
                    descripcion = "Colectivo Semi Cama: " + patente;
                    vehiculosCorta.add(descripcion);
                    break;
            }
        }
        
        return vehiculosCorta;
    }
    
    /**
     * Extrae la patente de un string con formato "Tipo: Patente"
     */
    public static String extraerPatente(String vehiculoSeleccionado) {
        if (vehiculoSeleccionado == null) {
            return null;
        }
        
        if (vehiculoSeleccionado.contains(": ")) {
            return vehiculoSeleccionado.substring(vehiculoSeleccionado.indexOf(": ") + 2);
        }
        
        return vehiculoSeleccionado;
    }
    
    /**
     * Calcula el costo de un viaje sin crearlo
     * @param destinoId ID del destino
     * @param vehiculoDescripcion Descripción del vehículo (puede incluir "Tipo: Patente")
     * @param pasajeros Cantidad de pasajeros
     * @param dniResponsables DNIs de los responsables (puede ser null para viajes cortos)
     * @return El costo calculado del viaje
     */
    public double calcularCostoViaje(String destinoId, String vehiculoDescripcion, int pasajeros, TreeSet<String> dniResponsables) {
        // Extraer patente
        String patente = extraerPatente(vehiculoDescripcion);
        
        // Obtener objetos de negocio
        Vehicles vehiculo = vehiculos.get(patente);
        Place destino = destinos.get(destinoId);
        
        if (vehiculo == null || destino == null) {
            return 0.0;
        }
        
        // Determinar tipo de viaje
        boolean esLargaDistancia = destino.getKm() >= 100;
        
        // Calcular camas si es coche cama
        int cantCamas = 0;
        if (vehiculo.getCapacidad() == 32) { // Coche cama
            cantCamas = com.java.tp.agency.vehicles.BusCC.calcularCamasOptimas(pasajeros);
        }
        
        // Obtener responsables
        HashMap<String, Responsable> responsablesDelViaje = new HashMap<>();
        if (esLargaDistancia && dniResponsables != null) {
            for (String dni : dniResponsables) {
                Responsable r = responsables.get(dni);
                if (r != null) {
                    responsablesDelViaje.put(dni, r);
                }
            }
        }
        
        // Crear instancia temporal y calcular costo
        Travel viajeTemporal;
        if (esLargaDistancia) {
            viajeTemporal = new LongDis();
        } else {
            viajeTemporal = new ShortDis();
        }
        
        try {
            return viajeTemporal.devuelveValorCalculado(
                vehiculo,
                destino,
                responsablesDelViaje,
                pasajeros,
                cantCamas
            );
        } catch (Exception e) {
            System.err.println("Error al calcular costo: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Obtiene la capacidad de un vehículo a partir de su descripción
     */
    public int getCapacidadVehiculo(String vehiculoDescripcion) {
        String patente = extraerPatente(vehiculoDescripcion);
        Vehicles vehiculo = vehiculos.get(patente);
        return (vehiculo != null) ? vehiculo.getCapacidad() : -1;
    }
}