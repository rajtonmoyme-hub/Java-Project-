module com.example.foodprice {

    // Required JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // Gson (for JSON persistence)
    requires com.google.gson;

    // Other third-party libraries you are using
    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // Standard Java modules
    requires java.desktop;

    // Allow FXML to inject @FXML fields
    opens com.example.foodprice to javafx.fxml,com.google.gson;



    // Export your public API
    exports com.example.foodprice;
}