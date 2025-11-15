package com.java.tp.guiControllers;
import com.java.tp.App;
import com.java.tp.agency.reports.ReportPlaces;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class TopPlacesController {

    @FXML
    private javafx.scene.control.ListView<String> list;
    
    @FXML
    private javafx.scene.control.Label totalPlaces;

    @FXML
    private void initialize() {
        try {
            List<String> lista = ReportPlaces.getKmDestino();
            float totalPrecio = ReportPlaces.getPrecioAcumulados();
            ObservableList<String> items = FXCollections.observableArrayList(lista);
            list.setItems(items);
            totalPlaces.setText(String.format("$%.2f", totalPrecio));
            
        } catch (Exception e) {
            ObservableList<String> items = FXCollections.observableArrayList(
                List.of("No se pudieron cargar los destinos")
            );
            list.setItems(items);
            totalPlaces.setText("0.00");
            System.out.println("No se pudieron cargar los destinos: " + e.getMessage());
        }
    }

    @FXML
    private void generateReport() {
        try {
            ReportPlaces.generateReport();
        } catch (IOException e) {
            System.err.println("Error al generar el reporte: " + e.getMessage());
        }
    }

    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("reportMenu");
    }

}