package com.java.tp.guiControllers;
import java.io.IOException;
import java.util.List;
import com.java.tp.App;
import com.java.tp.agency.reports.ResponsableReportGen;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class ResponsablesReportController {

    @FXML
    private javafx.scene.control.ListView<String> list;

    @FXML
    private void initialize() {
        try {
            List<String> lista = ResponsableReportGen.getResponsablesFormateados();
            ObservableList<String> items = FXCollections.observableArrayList(lista);
            list.setItems(items);
        } catch (Exception e) {
            ObservableList<String> items = FXCollections.observableArrayList(
                List.of("No se pudieron cargar los responsables")
            );
            list.setItems(items);
            System.out.println("No se pudieron cargar los responsables: " + e.getMessage());
        }
    }

    @FXML
    private void generateReport() {
        try {
            ResponsableReportGen.generateReport();
        } catch (IOException e) {
            System.err.println("Error al generar el reporte: " + e.getMessage());
        }
    }

    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("reportMenu");
    }

}