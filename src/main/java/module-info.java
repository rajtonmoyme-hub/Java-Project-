module com.example.foodprice {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    // Allow FXML to access your controller
    opens com.example.foodprice to javafx.fxml;

    exports com.example.foodprice;
}