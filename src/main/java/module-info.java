module com.example.pillmasterjfx {
    requires javafx.controls;
    requires javafx.fxml;

    // Pi4J MODULES
    requires com.pi4j;
    requires com.pi4j.plugin.pigpio;

    uses com.pi4j.extension.Extension;
    uses com.pi4j.provider.Provider;

    // allow access to classes in the following namespaces for Pi4J annotation processing
    opens com.example.pillmasterjfx to com.pi4j, javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires jssc;
    requires org.apache.httpcomponents.httpclient;
    requires org.json;
    requires java.mail;

    exports com.example.pillmasterjfx;
}