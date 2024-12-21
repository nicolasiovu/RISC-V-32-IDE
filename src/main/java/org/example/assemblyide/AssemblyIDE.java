package org.example.assemblyide;

import javafx.application.Application;
import javafx.stage.Stage;

public class AssemblyIDE extends Application {

    View view;
    MemoryModel model;

    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage stage) throws Exception {
        this.model = new MemoryModel();
        this.view = new View(this.model, stage);
    }
}
