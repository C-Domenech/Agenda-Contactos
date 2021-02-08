module com.cdomenech.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires javax.persistence;
    requires java.sql;

    opens com.cdomenech.ui to javafx.fxml, java.sql;
    opens models;
    exports com.cdomenech.ui;
}
