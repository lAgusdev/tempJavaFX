module com.java.tp {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive jakarta.xml.bind;
    requires org.glassfish.jaxb.runtime;
    opens com.java.tp.agency.places to jakarta.xml.bind;
    opens com.java.tp.agency.vehicles to jakarta.xml.bind;
    opens com.java.tp.agency.travels to jakarta.xml.bind;
    opens com.java.tp.agency.responsables to jakarta.xml.bind;
    exports com.java.tp.guiControllers; 
    opens com.java.tp.guiControllers to javafx.fxml;
    exports com.java.tp;
}
