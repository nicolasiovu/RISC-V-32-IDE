package org.example.assemblyide;

import javafx.scene.control.TextArea;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;

import java.util.Objects;

public class IOTerminal extends TextArea{

    private MemoryModel memoryModel;

    public IOTerminal(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;

        this.minWidth(200);
        this.setEditable(false);
        this.setWrapText(false);

        this.setStyle("-fx-background-color: #000000;" +
                "-fx-control-inner-background: #1e1e1e;" +
                "-fx-text-fill: #ffffff;" +
                "-fx-font-family: 'Consolas';" +
                "-fx-font-size: 14px;" +
                "-fx-highlight-fill: #679ea6;" +
                "-fx-highlight-text-fill: #ffffff;" +
                "-fx-border-color: #1e1e1e;" +
                "-fx-focus-color: transparent;" +
                "-fx-faint-focus-color: transparent;" +
                "-fx-background-insets: 0;" +
                "-fx-padding: 0;" +
                "-fx-background-radius: 0;" +
                "-fx-border-radius: 0;");

        String css = getClass().getResource("/scrollbar.css").toExternalForm();
        this.getStylesheets().add(css);

        this.textProperty().addListener((observable, oldValue, newValue) -> {
            this.positionCaret(newValue.length());
        });
    }

    public void printInteger() {
        int output = this.memoryModel.getRegisterValue(10);
        this.appendText(output + "\n");
    }

    public void printString() {
        int address = this.memoryModel.getRegisterValue(10);
        char current = (char) this.memoryModel.readByte(address);
        while (current != '\0') {
            this.appendText(Character.toString(current));
            current = (char) this.memoryModel.readByte(++address);
        }
    }

    public void printCharacter() {
        int address = this.memoryModel.getRegisterValue(10);
        char c = (char) this.memoryModel.readByte(address);
        this.appendText(Character.toString(c));
    }

    public void readInteger() {
        this.setEditable(true);
        // Platform.runLater(this::requestFocus);

        StringBuilder input = new StringBuilder();

        this.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    String[] lines = this.getText().split("\n");
                    input.append(lines[lines.length - 1].trim());

                    this.setEditable(false);

                    this.setOnKeyPressed(null);
                    break;
                default:
                    break;
            }
        });

        while (input.isEmpty()) {}

        int output = Integer.parseInt(input.toString());
        this.memoryModel.updateRegister(10, output);
    }

    public void readString() {
        this.setEditable(true);
        Platform.runLater(this::requestFocus);

        StringBuilder input = new StringBuilder();

        this.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    String[] lines = this.getText().split("\n");
                    input.append(lines[lines.length - 1].trim());

                    this.setEditable(false);

                    this.setOnKeyPressed(null);
                    break;
                default:
                    break;
            }
        });

        while (input.isEmpty()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int address = this.memoryModel.getRegisterValue(10);
        for (char c: input.toString().toCharArray()) {
            byte character = (byte) c;
            this.memoryModel.writeByte(address, character);
            address++;
        }
        this.memoryModel.writeByte(address, (byte) 0);
    }
}
