module com.example.pillmasterjfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.pillmasterjfx to javafx.fxml;
    exports com.example.pillmasterjfx;
}