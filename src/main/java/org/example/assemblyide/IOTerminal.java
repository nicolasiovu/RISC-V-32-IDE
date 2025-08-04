package org.example.assemblyide;

import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class IOTerminal extends TextArea{

    private MemoryModel memoryModel;
    private Program program;

    private int inputStartPos;

    public IOTerminal(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;

        this.minWidth(200);
        this.setEditable(false);
        this.setWrapText(true);

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

        this.setTextFormatter(new TextFormatter<>(change -> {
            int start = change.getRangeStart();
            int end = change.getRangeEnd();

            if (start < this.inputStartPos || end < this.inputStartPos) {
                return null;
            }
            return change;
        }));

        this.caretPositionProperty().addListener((obs, oldPos, newPos) -> {
            if (this.isEditable() && newPos.intValue() < this.inputStartPos) {
                this.positionCaret(inputStartPos);
            }
        });

        this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (this.isEditable() && event.getCode() == KeyCode.ENTER) {
                this.stopReading();
            }
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
        int num = this.memoryModel.getRegisterValue(10);
        char c = (char) num;
        this.appendText(Character.toString(c));
    }

    public void startReading() {
        this.inputStartPos = this.getLength();
        this.setEditable(true);
        if (this.getLength() != 0) {
            this.appendText("\n");
        }
        this.positionCaret(this.inputStartPos);
    }

    private void stopReading() {
        this.setEditable(false);
        int a7 = this.memoryModel.getRegisterValue(17);
        this.appendText("\n");
        switch (a7) {
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
        this.program.allowRunning();
    }

    private void readInteger() {
        try {
            String input = this.getText(this.inputStartPos, this.getLength()).trim();
            int num = Integer.parseInt(input);
            this.memoryModel.updateRegister(10, num);
        } catch (NumberFormatException _) {}
    }

    private void readCharacter() {
        String input = this.getText(this.inputStartPos, this.getLength()).trim();
        char[] arr = input.toCharArray();
        char c;
        try {
            c = arr[0];
        } catch (Exception _) {
            c = (char) 0;
        }
        this.memoryModel.updateRegister(10, c);
    }

    private void readString() {
        String input = this.getText(this.inputStartPos, this.getLength()).trim();
        int address = this.memoryModel.getRegisterValue(10);
        int numToRead = this.memoryModel.getRegisterValue(11) - 1;
        for (char c: input.toCharArray()) {
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
