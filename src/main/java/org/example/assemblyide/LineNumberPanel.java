package org.example.assemblyide;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class LineNumberPanel extends TextArea {

    public LineNumberPanel() {
        this.setMaxWidth(40);
        this.setMinHeight(500);
        this.setEditable(false);
        this.setWrapText(false);

        this.setStyle("-fx-background-color: #000000;" +
                "-fx-control-inner-background: #1e1e1e;" +
                "-fx-text-fill: #7a7979;" +
                "-fx-font-family: 'Consolas';" +
                "-fx-font-size: 14px;" +
                "-fx-highlight-fill: #679ea6;" +
                "-fx-highlight-text-fill: #7a7979;" +
                "-fx-border-color: #1e1e1e;" +
                "-fx-focus-color: transparent;" +
                "-fx-faint-focus-color: transparent;" +
                "-fx-background-insets: 0;" +
                "-fx-padding: 0;" +
                "-fx-background-radius: 0;" +
                "-fx-border-radius: 0;");

        String css = getClass().getResource("/scrollbar.css").toExternalForm();
        this.getStylesheets().add(css);

        this.setText(1 + "\n");
    }

    public void updateLineNumbers(int lineCount) {
        String lines = 1 + "\n";
        for (int i = 2; i <= lineCount; i++) {
            lines += i + "\n";
        }
        this.setText(lines);
    }
}
