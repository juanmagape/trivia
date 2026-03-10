module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires org.json;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.Controlador;
    opens com.example.demo.Controlador to javafx.fxml;
}