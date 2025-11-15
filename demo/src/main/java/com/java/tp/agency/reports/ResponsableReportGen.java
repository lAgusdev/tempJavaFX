package com.java.tp.agency.reports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import com.java.tp.agency.Agency;
import com.java.tp.agency.responsables.Responsable;
import com.java.tp.agency.travels.Travel;

public class ResponsableReportGen {

    public static List<String> getResponsablesFormateados() {
        HashMap<String, Responsable> todosLosResponsables = Agency.getInstancia().getResponsables();
        HashMap<String, Travel> todosLosViajes = Agency.getInstancia().getViajes(); 

        if (todosLosResponsables == null || todosLosResponsables.isEmpty()) {
            return List.of("No hay responsables cargados"); 
        }
        
        if (todosLosViajes == null || todosLosViajes.isEmpty()) {
            return List.of("No hay viajes cargados");
        }
        
        // Mapa auxiliar: DNI del Responsable -> Kilómetros totales recorridos
        HashMap<String, Float> kmPorResponsable = new HashMap<>();

        // Inicializar los KM de todos los responsables a 0
        for (String dni : todosLosResponsables.keySet()) {
            kmPorResponsable.put(dni, 0.0f);
        }

        // Recorrer todos los viajes y sumar los KM
        for (Travel viaje : todosLosViajes.values()) {
            TreeSet<String> responsablesDelViaje = viaje.getPerResponsables(); 
            if (responsablesDelViaje == null || responsablesDelViaje.isEmpty()) {
                continue;
            }
            
            float kmViaje = viaje.getKmRec();
            
            for (String dni : responsablesDelViaje) {
                // Sumar los KM al responsable si existe
                kmPorResponsable.computeIfPresent(dni, (k, v) -> v + kmViaje);
            }
        }

        // --- Ordenación y Formato ---
        // Crear una lista de objetos para ordenar sin parsear strings
        class ResponsableKmData {
            String dni;
            String nombre;
            float km;
            
            ResponsableKmData(String dni, String nombre, float km) {
                this.dni = dni;
                this.nombre = nombre;
                this.km = km;
            }
        }
        
        List<ResponsableKmData> listaResponsables = new ArrayList<>();
        
        for (Responsable responsable : todosLosResponsables.values()) {
            String dni = responsable.getDni();
            String nombre = responsable.getNombre();
            float kmTotales = kmPorResponsable.getOrDefault(dni, 0.0f);
            
            listaResponsables.add(new ResponsableKmData(dni, nombre, kmTotales));
        }

        // Ordenar de mayor a menor por KM
        listaResponsables.sort((r1, r2) -> Float.compare(r2.km, r1.km));

        // Formato final de salida
        List<String> listaConPuesto = new ArrayList<>();
        for (ResponsableKmData data : listaResponsables) {
            // Formato final: "DNI - Nombre - KM km"
            String linea = String.format("%s - %s - %.2f km", data.dni, data.nombre, data.km);
            listaConPuesto.add(linea);
        }
        
        return listaConPuesto;
    }
    
    public static float getKmTotales() {
        HashMap<String, Travel> todosLosViajes = Agency.getInstancia().getViajes();
        
        if (todosLosViajes == null || todosLosViajes.isEmpty()) {
            return 0.0f;
        }
        
        float kmTotal = 0;
        for (Travel viaje : todosLosViajes.values()) {
            TreeSet<String> responsables = viaje.getPerResponsables();
            if (responsables != null && !responsables.isEmpty()) {
                kmTotal += viaje.getKmRec();
            }
        }
        
        return kmTotal;
    }
    
    /**
     * Genera un archivo de reporte TXT con el ranking de responsables por KM.
     * @return La ruta absoluta del archivo generado, o null si no hay datos o falla.
     */
    public static String generateReport() throws IOException {
        List<String> lineasReporte = getResponsablesFormateados();
        
        // Verifica si la lista es el mensaje de error de "No hay responsables cargados"
        if (lineasReporte.size() == 1 && lineasReporte.get(0).equals("No hay responsables cargados")) {
            return null;
        }

        // 1. Añadir encabezado
        lineasReporte.add(0, "--- REPORTE DE RESPONSABLES POR KM RECORRIDOS ---");
        
        // 2. Generar nombre de archivo único con timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = "Reporte_Responsables_" + timestamp + ".txt";
        
        // 3. Definir la ruta del archivo y asegurarse de que el directorio 'reports' exista
        Path rutaDirectorio = Path.of("reports");
        if (!Files.exists(rutaDirectorio)) {
            Files.createDirectories(rutaDirectorio);
        }
        Path rutaArchivo = rutaDirectorio.resolve(nombreArchivo);
        
        // 4. Escribir al archivo
        Files.write(rutaArchivo, lineasReporte);
        
        return rutaArchivo.toAbsolutePath().toString();
    }
}