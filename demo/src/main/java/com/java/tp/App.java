/**
 * @author Gaspar Puente Villaroel
 * @author Agustin Moar
 * Entrega Final - T.P. Agencia de Viajes
 * 
 * Esta es la clase principal que inicia la aplicación JavaFX e instancia el singleton Agency.
 */
package com.java.tp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException; 
import com.java.tp.agency.Agency;



public class App extends Application {

    private static Scene scene;

    // Inicializa el singleton Agency
    public App() {
        Agency.getInstancia();
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("mainMenu"), 1080, 720);
        java.io.InputStream iconIs = App.class.getResourceAsStream("/com/java/tp/img/icon.png");
        if (iconIs != null) {
            Image icon = new Image(iconIs);
            stage.getIcons().add(icon);
        } else {
            System.out.println("Icon resource not found: /com/java/tp/img/icon.png");
        }
        stage.setTitle("Agencia de Viajes");
        stage.setMaximized(true);
        
        // Intentar cargar el CSS solo si existe
        java.net.URL cssUrl = App.class.getResource("/com/java/tp/styles/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("CSS resource not found: /com/java/tp/styles/styles.css");
        }
        
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}