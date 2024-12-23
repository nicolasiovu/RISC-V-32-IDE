package org.example.assemblyide;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

public class RegisterPanel extends GridPane implements Observer {
    private MemoryModel memoryModel;
    private int pc;
    private Label[] registers;

    public RegisterPanel(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;
        this.registers = new Label[32];
        this.pc = 0;

        Label programCounter = new Label();
        programCounter.setStyle("-fx-text-fill: #679ea6;" +
                "-fx-font-family: 'Consolas';" +
                "-fx-font-size: 12px;");
        programCounter.setText("pc");
        this.add(programCounter, 0, 0);

        Label pcValue = new Label();
        pcValue.setStyle("-fx-text-fill: #7a7979;" +
                "-fx-font-family: 'Consolas';" +
                "-fx-font-size: 12px;");
        pcValue.setText("00000000");
        this.add(pcValue, 1, 0);

        for (int i=0; i < this.registers.length; i++) {
            Label registerName = new Label();
            registerName.setStyle("-fx-text-fill: #7d34af;" +
                    "-fx-font-family: 'Consolas';" +
                    "-fx-font-size: 12px;");
            registerName.setText("x" + i);
            this.add(registerName, 0, i + 1);

            Label registerValue = new Label();
            registerValue.setStyle("-fx-text-fill: #7a7979;" +
                    "-fx-font-family: 'Consolas';" +
                    "-fx-font-size: 12px;");
            registerValue.setText("00000000");
            this.registers[i] = registerValue;
            this.add(registerValue, 1, i + 1);
        }

        this.setMaxWidth(250);
        this.setStyle("-fx-background-color: #1e1e1e;" +
                "-fx-border-color: #1e1e1e;" +
                "-fx-border-radius: 0;");
        this.setHgap(10);
        this.setVgap(3);
        this.setPadding(new Insets(5, 5, 5, 5));
    }

    @Override
    public void update(Observable o, Object arg) {
        int register = (Integer) arg;
        int value = this.memoryModel.getRegisterValue(register);
        String formattedHex = String.format("%08X", value);
        this.registers[register].setText(formattedHex);
    }
}
