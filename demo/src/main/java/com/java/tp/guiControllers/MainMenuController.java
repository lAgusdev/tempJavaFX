/**
* Esta clase se encarga de controlar el apartado grafico del menú principal de la aplicación.
* Permitiendo acceder a los diferentes submenus o salir de la aplicación.
*/
package com.java.tp.guiControllers;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainMenuController {

    @FXML
    private void switchToCreateTravel() throws IOException {
        com.java.tp.App.setRoot("newTravelMenu");
    }
    @FXML
    private void switchToReports() throws IOException {
        com.java.tp.App.setRoot("reportMenu");
    }
    @FXML
    private void switchToExit() throws IOException {
        Platform.exit();
    }
}
