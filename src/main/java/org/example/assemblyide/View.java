package org.example.assemblyide;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.LineNumberReader;

public class View {

    private Stage stage;
    private MemoryModel memoryModel;
    private TextEditor textEditor;
    private LineNumberPanel lineNumberPanel;
    private RegisterPanel registerPanel;
    private TerminalPanel terminalPanel;
    private IOTerminal ioTerminal;

    private FileHandler fileHandler;

    private Compiler compiler;
    private Program program;


    public View(MemoryModel model, Stage stage) {
        this.stage = stage;
        this.memoryModel = model;
        this.initGUI();
    }

    private SplitPane createMainLayout() {
        // Main horizontal split: Registers | Editor+IO
        SplitPane mainSplit = new SplitPane();
        mainSplit.setOrientation(Orientation.HORIZONTAL);

        // Vertical split for center: Code area | Terminal
        SplitPane centerSplit = new SplitPane();
        centerSplit.setOrientation(Orientation.VERTICAL);
        centerSplit.getItems().addAll(createEditor(), this.terminalPanel);
        centerSplit.setDividerPositions(0.7); // 70% for editor, 30% for terminal

        // Horizontal split for editor+IO: Editor | IO Terminal
        SplitPane editorIOSplit = new SplitPane();
        editorIOSplit.setOrientation(Orientation.HORIZONTAL);
        editorIOSplit.getItems().addAll(centerSplit, this.ioTerminal);
        editorIOSplit.setDividerPositions(0.8); // 80% for editor area, 20% for IO terminal

        // Final layout: Registers | (Editor+Terminal | IO)
        mainSplit.getItems().addAll(this.registerPanel, editorIOSplit);
        mainSplit.setDividerPositions(0.2); // 20% for registers, 80% for everything else

        styleSplitPane(mainSplit);
        styleSplitPane(centerSplit);
        styleSplitPane(editorIOSplit);

        return mainSplit;
    }

    private void styleSplitPane(SplitPane splitPane) {
        // Apply custom CSS to make dividers smaller and darker
        splitPane.setStyle(
                "-fx-background-color: #1e1e1e;" +
                        "-fx-box-border: transparent;"
        );

        // Add CSS for divider styling
        String dividerCSS =
                ".split-pane > .split-pane-divider { " +
                        "    -fx-background-color: #3c3c3c; " +
                        "    -fx-border-color: transparent; " +
                        "    -fx-border-width: 0; " +
                        "    -fx-pref-width: 3px; " +
                        "    -fx-pref-height: 3px; " +
                        "} " +
                        ".split-pane:horizontal > .split-pane-divider { " +
                        "    -fx-cursor: h-resize; " +
                        "} " +
                        ".split-pane:vertical > .split-pane-divider { " +
                        "    -fx-cursor: v-resize; " +
                        "} " +
                        ".split-pane > .split-pane-divider:hover { " +
                        "    -fx-background-color: #679ea6; " +
                        "}";

        splitPane.getStylesheets().add("data:text/css," + dividerCSS);
    }

    private void initGUI() {
        this.lineNumberPanel = new LineNumberPanel();
        this.textEditor = new TextEditor(this.memoryModel, this.lineNumberPanel);
        this.registerPanel = new RegisterPanel(this.memoryModel);
        this.terminalPanel = new TerminalPanel();
        this.ioTerminal = new IOTerminal(this.memoryModel);

        this.memoryModel.addObserver(this.registerPanel);

        this.compiler = new Compiler(this.memoryModel, this.textEditor, this.terminalPanel, this.ioTerminal);
        this.program = new Program(this.memoryModel, this.compiler, this.terminalPanel);
        this.ioTerminal.setProgram(this.program);

        this.fileHandler = new FileHandler(this.textEditor, this.terminalPanel, this.stage);

        BorderPane root = new BorderPane();
        root.setTop(this.createMenuBar());

        root.setCenter(createMainLayout());
        root.setStyle("-fx-background-color: #1e1e1e;");
//        root.setCenter(this.createEditor());
//        root.setLeft(this.registerPanel);
//        root.setBottom(this.terminalPanel);
//        root.setRight(this.ioTerminal);

        Scene scene = new Scene(root, 1200, 800);
        this.stage.setScene(scene);
        this.stage.setMinWidth(800);
        this.stage.setMinHeight(600);
        this.stage.setMaximized(false); // Allow maximizing
        this.stage.setResizable(true);
        this.stage.setTitle("RISC-V 32 Assembly IDE");
        this.stage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menu;
        MenuItem menuItem;

        menu = new Menu("File");

        menuItem = new MenuItem("New");
        menuItem.setOnAction((ActionEvent event) -> {});
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Open");
        menuItem.setOnAction(this.fileHandler);
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Save");
        menuItem.setOnAction(this.fileHandler);
        menu.getItems().add(menuItem);

        menuBar.getMenus().add(menu);

        menu = new Menu("Program");

        menuItem = new MenuItem("Compile");
        menuItem.setOnAction(this.compiler);
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Run");
        menuItem.setOnAction(this.program);
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Step");
        menuItem.setOnAction(this.program);
        menu.getItems().add(menuItem);

        menuBar.getMenus().add(menu);

        return menuBar;
    }

    private BorderPane createEditor() {
        BorderPane editor = new BorderPane();
        editor.setStyle("-fx-background-color: #1e1e1e;" +
                        "-fx-padding: 0");
        editor.setPadding(Insets.EMPTY);
        editor.setLeft(this.lineNumberPanel);
        editor.setCenter(this.textEditor);
        editor.setBottom(this.terminalPanel);
        return editor;
    }
}
