package org.example.assemblyide;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;

public class Program implements EventHandler<ActionEvent> {
    private MemoryModel memoryModel;
    private Compiler compiler;
    private TerminalPanel terminalPanel;
    private String error;
    private boolean awaitingInput;

    private int lastAction;

    public Program(MemoryModel memoryModel, Compiler compiler, TerminalPanel terminalPanel) {
        this.memoryModel = memoryModel;
        this.terminalPanel = terminalPanel;
        this.compiler = compiler;
        this.awaitingInput = false;
        this.lastAction = 0;
    }

    public boolean run() {
        this.lastAction = 0;
        if (this.awaitingInput) {
            this.error = "Cannot step: awaiting user input.";
            return false;
        }
        if (!this.compiler.getError().isEmpty()) {
            this.error = "Please recompile and ensure errors are fixed before running.";
            return false;
        }
        ArrayList<Instruction> instructions = this.compiler.getInstructions();
        while (true) {
            if (this.memoryModel.exitCalled()) {
                this.memoryModel.resetExit();
                this.terminalPanel.print("Exited due to syscall 10");
                return true;
            }
            int pc = this.memoryModel.getPc();
            if (pc / 4 >= instructions.size()) {
                this.error = "No instruction at pc=" + pc;
                return false;
            }
            Instruction instruction = instructions.get(pc / 4);
            if (!instruction.execute()) {
                this.error = instruction.getError();
                return false;
            }
            if (instruction.getInstructionInfo().contains("ecall")) {
                int a7 = this.memoryModel.getRegisterValue(17);
                if (a7 == 5 || a7 == 8 || a7 == 12) {
                    this.terminalPanel.print("Awaiting user input: syscall " + a7);
                    this.awaitingInput = true;
                    return true;
                }
            }
        }
    }

    public boolean step() {
        this.lastAction = 1;
        if (this.awaitingInput) {
            this.error = "Cannot step: awaiting user input.";
            return false;
        }
        if (!this.compiler.getError().isEmpty()) {
            this.error = "Please recompile and ensure errors are fixed before running.";
            return false;
        }
        if (this.memoryModel.exitCalled()) {
            this.memoryModel.resetExit();
            this.terminalPanel.print("Exited due to syscall 10");
            return true;
        }
        ArrayList<Instruction> instructions = this.compiler.getInstructions();
        int pc = this.memoryModel.getPc();
        if (pc / 4 >= instructions.size()) {
            this.error = "No instruction at pc=" + String.format("%08X", pc);
            return false;
        }
        Instruction instruction = instructions.get(pc / 4);
        if (!instruction.execute()) {
            this.error = instruction.getError();
            return false;
        }
        this.terminalPanel.print("Executing: " + instruction.getInstructionInfo());
        if (instruction.getInstructionInfo().contains("ecall")) {
            int a7 = this.memoryModel.getRegisterValue(17);
            if (a7 == 5 || a7 == 8 || a7 == 12) {
                this.awaitingInput = true;
            }
        }
        if (this.memoryModel.exitCalled()) {
            this.memoryModel.resetExit();
            return true;
        }
        return true;
    }

    public void allowRunning() {
        this.awaitingInput = false;
        if (this.lastAction == 0) {
            if (!this.run()) {
                this.terminalPanel.print(this.error);
            }
        } else {
            if (!this.step()) {
                this.terminalPanel.print(this.error);
            }
        }
    }

    @Override
    public void handle(ActionEvent event) {
        MenuItem caller = (MenuItem) event.getSource();
        if (caller.getText().equals("Run")) {
            if (!this.run()) {
                this.terminalPanel.print(this.error);
            }
        } else if (caller.getText().equals("Step")) {
            if (!this.step()) {
                this.terminalPanel.print(this.error);
            }
        }
    }
}
