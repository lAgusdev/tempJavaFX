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
