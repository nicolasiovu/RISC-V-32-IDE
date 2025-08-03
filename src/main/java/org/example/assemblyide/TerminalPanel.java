package org.example.assemblyide;

import javafx.scene.control.TextArea;

public class TerminalPanel extends TextArea{

    public TerminalPanel() {
        this.setMinHeight(150);
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

        this.textProperty().addListener((observable, oldValue, newValue) -> {
            this.positionCaret(newValue.length());
        });
    }

    public void print(String output) {
        this.appendText(output + "\n");
    }
}
