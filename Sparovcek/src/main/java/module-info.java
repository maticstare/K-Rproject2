module fri.sparovcek {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens fri.sparovcek to javafx.fxml;
    exports fri.sparovcek;
}