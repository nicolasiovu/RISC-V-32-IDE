package org.example.assemblyide;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class FileHandler implements EventHandler<ActionEvent> {
    private TextEditor textEditor;
    private Stage stage;
    private FileChooser fileChooser;
    private TerminalPanel terminalPanel;

    public FileHandler(TextEditor textEditor, TerminalPanel terminal, Stage stage) {
        this.textEditor = textEditor;
        this.terminalPanel = terminal;
        this.stage = stage;

        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("Browse Files");

        this.fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        this.fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Assembly Source Files", "*.s", "*.txt")
        );
    }

    @Override
    public void handle(ActionEvent event) {
        MenuItem caller = (MenuItem) event.getSource();
        File file = this.fileChooser.showOpenDialog(this.stage);

        if (caller.getText().equals("Open")) {
            if (file != null) {
                try {
                    String fileContent = Files.readString(file.toPath());
                    this.textEditor.setText(fileContent);
                    this.terminalPanel.appendText("File opened successfully\n");
                } catch (IOException e) {
                    this.terminalPanel.appendText("Error reading file: " + e.getMessage() + "\n");
                }
            }
        } else if (caller.getText().equals("Save")) {
            if (file != null) {
                try {
                    Files.writeString(file.toPath(), this.textEditor.getText());
                    this.terminalPanel.appendText("File saved successfully\n");
                } catch (IOException e) {
                    this.terminalPanel.appendText("Error saving file: " + e.getMessage() + "\n");
                }
            }
        }
    }
}
