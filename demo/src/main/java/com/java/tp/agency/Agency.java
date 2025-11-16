/**
 * Esta clase representa la agencia con su conjunto de viajes, conjunto de destinos, conjunto de vehiculos, conjunto de responsables,la cantidad de viajes para calcular el id del viaje a partir del destino y el acumulador de km para cada responsable.
 */
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
    //constructor
    private Agency() {
        System.out.println("Se creo la instancia Singleton de agencia");
        try {
            new DataController().iniciaxml(this);
        } catch (Exception e) {
            System.out.println("Error al cargar datos en init(): " + e.getMessage());
        }
    }
//
//getter de la instancia para aplicar el singleton
    public static Agency getInstancia(){
        if (instancia==null) {
            instancia = new Agency();
        }
        return instancia;
    }
//
//actualiza los km recorridos por el responsable para el ranking
    public void ActualizaResKm(String dniResponsableABordo, float kmRecorridos){
        kmRecorridos+= resKm.getOrDefault(dniResponsableABordo, 0.0f);
        resKm.put(dniResponsableABordo, kmRecorridos); //remplaza el viejo por el nuevo
    }
//
//genera el ranking de responsables por km recorridos
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
//
    //getters
    public HashMap<String,Responsable> getResponsables() {return responsables;}
    public HashMap<String, Integer> getCantViajes() {return cantViajes;}
    public HashMap<String, Travel> getViajes() {return viajes;}
    public TreeMap<String,Place> getDestinos(){return destinos;}
    public HashMap<String,Vehicles> getVehiculos(){return vehiculos;}
    //fin getter
//devuelve los vehiculos disponibles para asignar a un viaje
    public HashMap<String, Vehicles> VehiculosDisponibles(){ //devuelve una HashMap con los vehiculos disponibles
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
    public HashMap<String, Responsable> ResponsablesDisponibles(){ //devuelve un Hashmap con los responsables disponibles
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
    //muestra el viaje creado por consola por seguridad
    public void muestraViajes(){
        for(Travel v: viajes.values()){
            System.out.println(v.getId()+" || "+v.getIdDestino() + " || " + v.getEstado());
        }
    }
    //crea el viaje y actualiza el estado del vehiculo y responsables asignados
    public void crearViaje(String destino,String patVeh ,int pasajeros,float kmRec, TreeSet <String>res){
        Travel viajenuevo;
        Place desAct=destinos.get(destino);
        String id=creaIdViaje(destino);
        
        // Extraer la patente del formato "Tipo: Patente" si es necesario
        String patenteReal = extraerPatente(patVeh);
        
        System.out.println("=== DEBUG crearViaje ===");
        System.out.println("Destino: " + destino + ", kmRec: " + kmRec + ", Patente: " + patenteReal);
        
        if(desAct.getKm()<100){//viaje corta
            viajenuevo =new ShortDis(id,patenteReal,destino,pasajeros,kmRec);
            System.out.println("=== viaje corta creado con estado: " + viajenuevo.getEstado() + " ===");
        }else{
            if(res.isEmpty()){
                throw new SinResLargaDisException("la lista de responsables esta vacia");
            }
            viajenuevo =new LongDis(id,patenteReal,destino,pasajeros,kmRec,res);
            System.out.println("=== viaje larga creado con estado: " + viajenuevo.getEstado() + " ===");
        }

        viajes.put(id,viajenuevo);
        
        // Actualizar estado del vehículo
        Vehicles v = vehiculos.get(patenteReal);
        
        if (v != null) {
            v.setEstado(Unoccupied.OCUPADO);
        } else {
            System.err.println("ADVERTENCIA: No se encontró el vehículo con patente: " + patenteReal + " (original: " + patVeh + ")");
        }
        
        if (res != null) {
            for (String r : res) {
                Responsable responsable = responsables.get(r);
                if (responsable != null) {
                    responsable.setEstado(Unoccupied.OCUPADO);
                } else {
                    System.err.println("ADVERTENCIA: No se encontró el responsable con DNI: " + r);
                }
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
//crea el id del viaje a partir del destino y la cantidad de viajes realizados a ese destino.devuelve el id creado
    public String creaIdViaje(String destino){
        int contador = cantViajes.getOrDefault(destino, 1);
        String id = destino + "-" + contador;
        cantViajes.put(destino, contador + 1);
        return id;
    }
    
//actualiza el contador de viajes a partir del id del viaje cargado desde el xml
    public void actualizarContadorDesdeId(String idViaje) {
        // Extraer el destino y número del ID (formato: "Destino-5")
        int lastDash = idViaje.lastIndexOf('-');
        if (lastDash != -1) {
            String destino = idViaje.substring(0, lastDash);
            try {
                int numero = Integer.parseInt(idViaje.substring(lastDash + 1));
                // Actualizar el contador solo si este número es mayor o igual al actual
                int contadorActual = cantViajes.getOrDefault(destino, 0);
                if (numero >= contadorActual) {
                    cantViajes.put(destino, numero + 1);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear número del ID: " + idViaje);
            }
        }
    }
//llama al data controller para serializar los viajes
    public void saveTravelsData() {
        try {
            new DataController().serializaViajes();
        } catch (Exception e) {
            System.out.println("Error al guardar datos de viajes: " + e.getMessage());
        }
    }
    
//getters para la interfaz
    public Map<String, Integer> getDistancias() {
        Map<String, Integer> distancias = new TreeMap<>();
        for (Place destino : destinos.values()) {
            distancias.put(destino.getId(), (int) destino.getKm());
        }
        return distancias;
    }   
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
    public List<String> getResponsablesDisponibles() {
        Collection<Responsable> todosLosResponsables = responsables.values();
        Set<String> responsablesOcupadosDni = new HashSet<>();
        
        // Obtener DNIs de responsables ocupados en viajes PENDIENTE o EN_CURSO
        for (Travel viaje : viajes.values()) {
            try {
                // Solo considerar viajes que no sean pendientes ni en curso
                if (viaje.getEstado() != com.java.tp.agency.enums.TravelStatus.PENDIENTE ||
                    viaje.getEstado() != com.java.tp.agency.enums.TravelStatus.EN_CURSO){
                    TreeSet<String> dnisDelViaje = viaje.getPerResponsables();
                    if (dnisDelViaje != null) {
                        responsablesOcupadosDni.addAll(dnisDelViaje);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error procesando viaje: " + e.getMessage());
            }
        }
        
        // Crear lista de disponibles
        List<String> listaDisponibles = new ArrayList<>();
        for (Responsable responsable : todosLosResponsables) {
            String dni = responsable.getDni();
            if (!responsablesOcupadosDni.contains(dni)) {
                listaDisponibles.add(String.format("%s (%s)", responsable.getNombre(), dni));
            }
        }
        
        return listaDisponibles;
    }
    //Extrae la patente de un string con formato "Tipo: Patente"
    public static String extraerPatente(String vehiculoSeleccionado) {
        if (vehiculoSeleccionado == null) {
            return null;
        }
        
        if (vehiculoSeleccionado.contains(": ")) {
            return vehiculoSeleccionado.substring(vehiculoSeleccionado.indexOf(": ") + 2);
        }
        
        return vehiculoSeleccionado;
    }
    
//calcula el costo del viaje. devuelve un double con el costo
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
                pasajeros
            );
        } catch (Exception e) {
            System.err.println("Error al calcular costo: " + e.getMessage());
            return 0.0;
        }
    }
    
//obtiene la capacidad del vehiculo a partir de su descripcion
    public int getCapacidadVehiculo(String vehiculoDescripcion) {
        String patente = extraerPatente(vehiculoDescripcion);
        Vehicles vehiculo = vehiculos.get(patente);
        return (vehiculo != null) ? vehiculo.getCapacidad() : -1;
    }


}