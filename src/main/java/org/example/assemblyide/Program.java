package org.example.assemblyide;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;

public class Program implements EventHandler<ActionEvent> {
    private MemoryModel memoryModel;
    private Compiler compiler;
    private String error;

    public Program(MemoryModel memoryModel, Compiler compiler) {
        this.memoryModel = memoryModel;
        this.compiler = compiler;
    }

    public boolean run() {
        if (!this.compiler.getError().isEmpty()) {
            this.error = "Please recompile and ensure errors are fixed before running.";
            return false;
        }
        ArrayList<Instruction> instructions = this.compiler.getInstructions();
        while (true) {
            int pc = this.memoryModel.getPc();
            if (pc / 4 >= instructions.size()) {
                break;
            }
            Instruction instruction = instructions.get(pc / 4);
            instruction.execute();
        }
        return true;
    }

    @Override
    public void handle(ActionEvent event) {
        if (!this.run()) {
            System.out.println(this.error);
        }
    }
}
