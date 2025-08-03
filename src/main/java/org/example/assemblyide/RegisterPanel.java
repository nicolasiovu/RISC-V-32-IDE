package org.example.assemblyide;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

public class RegisterPanel extends GridPane implements Observer {
    private MemoryModel memoryModel;
    private Label pc;
    private Label[] registers;
    private Label recentRegister;

    public RegisterPanel(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;
        this.registers = new Label[32];
        this.pc = new Label();

        Label programCounter = new Label();
        programCounter.setStyle("-fx-text-fill: #679ea6;" +
                "-fx-font-family: 'Consolas';" +
                "-fx-font-size: 12px;");
        programCounter.setText("pc");
        this.add(programCounter, 0, 0);

        this.pc.setStyle("-fx-text-fill: #cfcfcf;" +
                "-fx-font-family: 'Consolas';" +
                "-fx-font-size: 12px;");
        this.pc.setText("00000000");
        this.add(this.pc, 1, 0);

        for (int i=0; i < this.registers.length; i++) {
            Label registerName = new Label();
            registerName.setStyle("-fx-text-fill: #7d34af;" +
                    "-fx-font-family: 'Consolas';" +
                    "-fx-font-size: 12px;");
            String register = "x" + i;
            String alias = Util.reverseRegisterMap.get(register);
            String paddedText = String.format("%-5s %s", alias, register);
            registerName.setText(paddedText);
            this.add(registerName, 0, i + 1);

            Label registerValue = new Label();
            registerValue.setStyle("-fx-text-fill: #cfcfcf;" +
                    "-fx-font-family: 'Consolas';" +
                    "-fx-font-size: 12px;");
            registerValue.setText("00000000");
            this.registers[i] = registerValue;
            this.add(registerValue, 1, i + 1);
        }

        this.setMinWidth(150);
        this.setMaxWidth(150);
        this.setStyle("-fx-background-color: #1e1e1e;" +
                "-fx-border-color: #1e1e1e;" +
                "-fx-border-radius: 0;");
        this.setHgap(10);
        this.setVgap(3);
        this.setPadding(new Insets(5, 5, 5, 5));
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg != "pc") {
            int register = (Integer) arg;
            int value = this.memoryModel.getRegisterValue(register);
            String formattedHex = String.format("%08X", value);
            this.registers[register].setText(formattedHex);
            this.registers[register].setStyle("-fx-text-fill: #ffffff;" +
                    "-fx-font-family: 'Consolas';" +
                    "-fx-font-size: 12px;" +
                    "-fx-background-color: rgba(255,0,0,0.85);");
            if (this.recentRegister != null) {
                this.recentRegister.setStyle("-fx-text-fill: #cfcfcf;" +
                        "-fx-font-family: 'Consolas';" +
                        "-fx-font-size: 12px;");
            }
            this.recentRegister = this.registers[register];
        } else {
            int value = this.memoryModel.getPc();
            String pcHex = String.format("%08X", value);
            this.pc.setText(pcHex);
        }
    }
}
