module org.example.assemblyide {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.assemblyide to javafx.fxml;
    exports org.example.assemblyide;
}