module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires java.sql;
    requires pdfbox;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.domain;
    opens com.example.demo.domain to javafx.fxml;
    exports com.example.demo.infrastructure;
    opens com.example.demo.infrastructure to javafx.fxml;
}