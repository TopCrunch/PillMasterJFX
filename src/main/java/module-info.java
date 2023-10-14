module com.example.pillmasterjfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires jssc;
    requires org.apache.httpcomponents.httpclient;
    requires org.json;

    opens com.example.pillmasterjfx to javafx.fxml;
    exports com.example.pillmasterjfx;
}