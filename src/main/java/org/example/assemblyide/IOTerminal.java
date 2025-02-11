package org.example.assemblyide;

import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class IOTerminal extends TextArea{

    private MemoryModel memoryModel;
    private Program program;
    private String collectedInput;

    private final EventHandler<KeyEvent> keyHandler = event -> {
        String c = event.getCharacter();
        this.collectedInput += c;
        if (c.equals("\n") || c.equals("\r") || c.equals("\r\n")) {
            this.collectedInput = this.collectedInput.substring(0, this.collectedInput.length() - 1);
            switch (this.memoryModel.getRegisterValue(17)) {
                case 5:
                    this.readInteger();
                    break;
                case 8:
                    this.readString();
                    break;
                case 12:
                    this.readCharacter();
                    break;
                default:
                    break;
            }
            this.stopReading();
        }
    };

    private final EventHandler<KeyEvent> deleteHandler = event -> {
        if (event.getCode() == KeyCode.BACK_SPACE && !this.collectedInput.isEmpty()) {
            this.collectedInput = this.collectedInput.substring(0, this.collectedInput.length() - 1);
            event.consume();
        }
    };

    public IOTerminal(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;
        this.collectedInput = "";

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

    public void setProgram(Program program) {
        this.program = program;
    }

    public void printInteger() {
        int output = this.memoryModel.getRegisterValue(10);
        this.appendText(Integer.toString(output));
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

    public void startReading() {
        this.setEditable(true);
        this.addEventHandler(KeyEvent.KEY_TYPED, keyHandler);
        this.addEventHandler(KeyEvent.KEY_PRESSED, deleteHandler);
    }

    private void stopReading() {
        this.setEditable(false);
        this.removeEventHandler(KeyEvent.KEY_TYPED, keyHandler);
        this.removeEventHandler(KeyEvent.KEY_PRESSED, deleteHandler);
        this.program.allowRunning();
    }

    private void readInteger() {
        try {
            int input = Integer.parseInt(this.collectedInput);
            this.memoryModel.updateRegister(10, input);
        } catch (NumberFormatException _) {}
    }

    private void readCharacter() {
        char c = this.collectedInput.toCharArray()[0];
        this.memoryModel.updateRegister(10, c);
    }

    private void readString() {
        int address = this.memoryModel.getRegisterValue(10);
        int numToRead = this.memoryModel.getRegisterValue(11) - 1;
        for (char c: this.collectedInput.toCharArray()) {
            if (numToRead <= 0) {
                break;
            }
            this.memoryModel.writeByte(address, (byte) c);
            address++;
            numToRead--;
        }
        this.memoryModel.writeByte(address, (byte) '\0');
    }
}
