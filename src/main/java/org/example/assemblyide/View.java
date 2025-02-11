package org.example.assemblyide;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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

    private Compiler compiler;
    private Program program;


    public View(MemoryModel model, Stage stage) {
        this.stage = stage;
        this.memoryModel = model;
        this.initGUI();
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

        BorderPane root = new BorderPane();
        root.setTop(this.createMenuBar());

        root.setCenter(this.createEditor());
        root.setLeft(this.registerPanel);
        root.setBottom(this.terminalPanel);
        root.setRight(this.ioTerminal);

        Scene scene = new Scene(root);
        this.stage.setScene(scene);
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
        menuItem.setOnAction((ActionEvent event) -> {});
        menu.getItems().add(menuItem);

        menuItem = new MenuItem("Save");
        menuItem.setOnAction((ActionEvent event) -> {});
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
