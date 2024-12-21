package org.example.assemblyide;

import javafx.event.EventHandler;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

import java.io.LineNumberReader;
import java.util.ArrayList;

public class TextEditor extends TextArea {
    private MemoryModel memoryModel;
    private LineNumberPanel lineNumberPanel;

    public TextEditor(MemoryModel memoryModel, LineNumberPanel lineNumberPanel) {
        this.memoryModel = memoryModel;
        this.lineNumberPanel = lineNumberPanel;

        this.setMinWidth(720);
        this.setMinHeight(500);

        this.setStyle("-fx-background-color: #000000;" +
        "-fx-control-inner-background: #1e1e1e;" +
        "-fx-text-fill: #7d34af;" +
        "-fx-font-family: 'Consolas';" +
        "-fx-font-size: 14px;" +
        "-fx-highlight-fill: #679ea6;" +
        "-fx-highlight-text-fill: #573670;" +
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
            int lineCount = this.getParagraphs().size();
            this.lineNumberPanel.updateLineNumbers(lineCount);
        });

        this.scrollTopProperty().addListener((observable, oldValue, newValue) -> {
            this.lineNumberPanel.setScrollTop(newValue.doubleValue());
        });

        this.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.TAB) {
                int caretPosition = this.getCaretPosition();
                this.insertText(caretPosition, "    ");
                event.consume();
            }
        });
    }
}
