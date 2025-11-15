package com.java.tp.guiControllers;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import com.java.tp.agency.Agency;
import com.java.tp.agency.dataController.DataController;
import com.java.tp.agency.responsables.Responsable;
import com.java.tp.agency.travels.Travel;
import com.java.tp.App;
public class NewTravelController {

    // Componentes de la UI inyectados desde FXML
    @FXML private ComboBox<String> destinoComboBox;
    @FXML private ComboBox<String> vehiculoComboBox; 
    @FXML private Spinner<Integer> pasajerosSpinner;
    @FXML private Spinner<Integer> kmHechosSpinner; 
    
    // Componentes de Responsables (Doble ListView)
    @FXML private HBox responsablesHBox;
    @FXML private ListView<String> responsablesDisponiblesListView;
    @FXML private ListView<String> responsablesSeleccionadosListView;
    @FXML private Button agregarResponsableButton;
    @FXML private Button quitarResponsableButton;
    @FXML private Label responsablesSectionLabel; 

    @FXML private Label totalValue;

    private final static int UMBRAL_LARGA_DISTANCIA = 100;

    private ObservableList<String> disponibles;
    private ObservableList<String> seleccionados = FXCollections.observableArrayList();

private double calcularCostoViaje() {
    String vehiculoSeleccionado = vehiculoComboBox.getValue();
    String destinoSeleccionado = destinoComboBox.getValue(); 
    
    if (vehiculoComboBox == null || destinoComboBox == null || pasajerosSpinner == null) {
        return 0.0;
    }
    
    int pasajeros = pasajerosSpinner.getValue();
    
    if (vehiculoSeleccionado == null || destinoSeleccionado == null) {
        return 0.0;
    }
    
    // Obtener DNIs de responsables seleccionados
    TreeSet<String> dniResponsables = seleccionados.stream()
        .map(r -> r.substring(r.indexOf('(') + 1, r.indexOf(')')))
        .collect(Collectors.toCollection(TreeSet::new));
    
    // Llamar al método de negocio en Agency
    try {
        return Agency.getInstancia().calcularCostoViaje(
            destinoSeleccionado,
            vehiculoSeleccionado,
            pasajeros,
            dniResponsables
        );
    } catch (Exception e) {
        System.err.println("Error al calcular costo: " + e.getMessage());
        return 0.0;
    }
}


private void actualizarCostoLabel() {
    // Verificar que el label existe antes de usarlo
    if (totalValue == null) {
        return;
    }
    
    try {
        // [MODIFICADO] Llama a la función que calcula el costo
        double costo = calcularCostoViaje(); 
        
        String costoFormateado = String.format("$ %.2f", costo);
        totalValue.setText(costoFormateado);
    } catch (Exception e) {
        totalValue.setText("ERROR");
        System.err.println("Error al actualizar label de costo: " + e.getMessage());
    }
}



    @FXML
private List<String> obtenerResponsablesDisponibles() {
    

    java.util.Collection<Responsable> todosLosResponsables = Agency.getInstancia().getResponsables().values();
        

    java.util.Set<String> responsablesOcupadosDni = new java.util.HashSet<>();
    
    for (Travel viaje : Agency.getInstancia().getViajes().values()) {
        try {

            if ("ACTIVO".equals(viaje.getEstado())) { 
                java.util.TreeSet<String> dnisDelViaje = viaje.getPerResponsables(); 
                responsablesOcupadosDni.addAll(dnisDelViaje);
            }
        } catch (Exception e) {
            System.err.println("Error procesando estado del viaje: " + e.getMessage());
        }
    }
    
    List<String> listaDisponibles = new java.util.ArrayList<>();
    
    for (Responsable responsable : todosLosResponsables) {
        String dni = responsable.getDni();
        
    
        if (!responsablesOcupadosDni.contains(dni)) {
            listaDisponibles.add(String.format("%s (%s)", responsable.getNombre(), dni));
        }
    }
    
    return listaDisponibles;
}
    @FXML
    public void initialize() {
        // --- CONFIGURACIÓN DE DESTINO ---
        destinoComboBox.setItems(FXCollections.observableArrayList(Agency.getInstancia().getDistancias().keySet()));
        destinoComboBox.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                actualizarOpcionesDeViaje(newValue);
                actualizarCostoLabel();
            }
        );

        // --- CONFIGURACIÓN DE PASAJEROS ---
        pasajerosSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1));
        pasajerosSpinner.valueProperty().addListener((obs, oldVal, newVal) -> actualizarCostoLabel());
        // --- CONFIGURACIÓN DE KILÓMETROS (CON EDICIÓN POR TECLADO) ---
        SpinnerValueFactory.IntegerSpinnerValueFactory factory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0);
        
        kmHechosSpinner.setValueFactory(factory);
        kmHechosSpinner.setEditable(true); // Habilitar la edición por teclado
        
        // Listener para validar y aplicar la entrada del teclado
        kmHechosSpinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.matches("\\d*")) {
                try {
                    int value = Integer.parseInt(newValue);
                    if (value >= factory.getMin() && value <= factory.getMax()) {
                        factory.setValue(value);
                        actualizarCostoLabel();
                    } else {
                        // Si está fuera de rango, revertimos o limpiamos
                        if (!newValue.isEmpty()) kmHechosSpinner.getEditor().setText(oldValue);
                    }
                } catch (NumberFormatException e) {
                    // Si el texto está vacío, lo ignoramos. Si es inválido, revertimos.
                    if (!newValue.isEmpty()) {
                        kmHechosSpinner.getEditor().setText(oldValue);
                    }
                }
            } else {
                // Si la entrada contiene caracteres no numéricos, revertimos
                kmHechosSpinner.getEditor().setText(oldValue);
            }
        });
        
        // --- CONFIGURACIÓN DE VEHÍCULOS (ComboBox) ---
        vehiculoComboBox.setItems(FXCollections.observableArrayList(Agency.getInstancia().getVehiculosParaCortaDistancia()));
        vehiculoComboBox.getSelectionModel().select("Auto");
        vehiculoComboBox.valueProperty().addListener((obs, oldVal, newVal) -> actualizarCostoLabel());
        
        // --- CONFIGURACIÓN DE RESPONSABLES ---
        List<String> responsablesEstandar = obtenerResponsablesDisponibles();
        
        disponibles = FXCollections.observableArrayList(responsablesEstandar);
        responsablesDisponiblesListView.setItems(disponibles);
        responsablesSeleccionadosListView.setItems(seleccionados);
        responsablesDisponiblesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        responsablesSeleccionadosListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Ocultar la sección de responsables al inicio
        responsablesHBox.setVisible(false);
        responsablesHBox.setManaged(false);
        if (responsablesSectionLabel != null) {
            responsablesSectionLabel.setVisible(false);
            responsablesSectionLabel.setManaged(false);
        }
    }
    
    /**
     * Lógica de Distancia: Oculta/Muestra Responsables y actualiza Vehículos
     */
    private void actualizarOpcionesDeViaje(String destino) {
        if (destino == null) return;
        
        int distancia = Agency.getInstancia().getDistancias().getOrDefault(destino, 0);
        boolean esLargaDistancia = distancia >= UMBRAL_LARGA_DISTANCIA;

        // 1. LÓGICA DE RESPONSABLES (Ocultar/Mostrar el HBox completo)
        responsablesHBox.setVisible(esLargaDistancia);
        responsablesHBox.setManaged(esLargaDistancia);
        if (responsablesSectionLabel != null) {
            responsablesSectionLabel.setVisible(esLargaDistancia);
            responsablesSectionLabel.setManaged(esLargaDistancia);
        }

        // 2. LÓGICA DE VEHÍCULOS (Actualiza las opciones del ComboBox)
        actualizarVehiculos(esLargaDistancia);
    }
    
    /**
     * Configura el ComboBox de Vehículos basado en la distancia
     */
    private void actualizarVehiculos(boolean esLargaDistancia) {
        List<String> opcionesVehiculos = esLargaDistancia 
            ? Agency.getInstancia().getVehiculosParaLargaDistancia()
            : Agency.getInstancia().getVehiculosParaCortaDistancia();
        vehiculoComboBox.setItems(FXCollections.observableArrayList(opcionesVehiculos));
        
        // Mantiene la selección si es posible, sino elige el primero
        String currentSelection = vehiculoComboBox.getValue();
        if (currentSelection != null && opcionesVehiculos.contains(currentSelection)) {
            vehiculoComboBox.getSelectionModel().select(currentSelection);
        } else {
            vehiculoComboBox.getSelectionModel().selectFirst();
        }
    }

    // --- Métodos para mover responsables entre listas ---

    @FXML
    private void agregarResponsable() {
        ObservableList<String> selected = responsablesDisponiblesListView.getSelectionModel().getSelectedItems();
        if (!selected.isEmpty()) {
            seleccionados.addAll(selected);
            disponibles.removeAll(selected);
            responsablesDisponiblesListView.getSelectionModel().clearSelection();
            actualizarCostoLabel();
        }
    }

    @FXML
    private void quitarResponsable() {
        ObservableList<String> selected = responsablesSeleccionadosListView.getSelectionModel().getSelectedItems();
        if (!selected.isEmpty()) {
            disponibles.addAll(selected);
            seleccionados.removeAll(selected);
            responsablesSeleccionadosListView.getSelectionModel().clearSelection();
            actualizarCostoLabel();
        }
    }

    // --- Método de Acción principal ---

    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("mainMenu");
    }

    @FXML
    public static int getCapacidadDeVehiculoSeleccionado(String vehiculoFormateado) {
        return Agency.getInstancia().getCapacidadVehiculo(vehiculoFormateado);
    }
    @FXML private final DataController dataController = new DataController();
    @FXML
    private void crearViaje() throws IOException {
        String destino = destinoComboBox.getValue();
        String vehiculo = vehiculoComboBox.getValue();
        int pasajeros = pasajerosSpinner.getValue();
        int kmHechos = kmHechosSpinner.getValue();
        
        // El Stream convierte los Strings "Nombre (DNI)" a un TreeSet de solo los DNIs
        TreeSet<String> responsablesDNI = seleccionados.stream()
            .map(r -> r.substring(r.indexOf('(') + 1, r.indexOf(')'))) 
            .collect(Collectors.toCollection(TreeSet::new));

        // 1. Validaciones
        if (destino == null || destino.isEmpty() || vehiculo == null || vehiculo.isEmpty()) {
            mostrarAlerta("Error de Selección", "Por favor, selecciona Destino y Vehículo.");
            return;
        }
        if(getCapacidadDeVehiculoSeleccionado(vehiculo)< pasajeros){
            mostrarAlerta("Error de Capacidad", "El vehículo seleccionado no tiene capacidad suficiente para los pasajeros.");
            return;
        }
        if (responsablesHBox.isManaged() && responsablesDNI.isEmpty()) { // Uso responsablesDNI
            mostrarAlerta("Error de Selección", "Para viajes de larga distancia, debes seleccionar al menos un responsable.");
            return;
        }
        
        // 2. Lógica de Negocio y Persistencia
        
        // Crear el viaje en la Agency
        try {
            Agency.getInstancia().crearViaje(destino, vehiculo, pasajeros, (float) kmHechos, responsablesDNI);
            
            // 3. SERIALIZAR LOS VIAJES CREADOS
            dataController.serializaViajes(); // <-- LLAMADA PARA GUARDAR EL CAMBIO
            
            App.setRoot("menuPostNewTravel");
            mostrarAlerta("Viaje Creado", "¡El viaje a " + destino + " ha sido creado con éxito!");

        } catch (com.java.tp.agency.exceptions.SinResLargaDisException e) {
            mostrarAlerta("Error de Negocio", "Error al crear viaje: " + e.getMessage());
            // O manejar la excepción si Agency.crearViaje lanza otras (ej. VehiculoNoDisponibleException)
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error inesperado al guardar el viaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método auxiliar para mostrar alertas
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}