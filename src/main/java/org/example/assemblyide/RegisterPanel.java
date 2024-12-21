package org.example.assemblyide;

import javafx.scene.layout.StackPane;

public class RegisterPanel extends StackPane {
    private MemoryModel memoryModel;

    public RegisterPanel(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;
    }
}
