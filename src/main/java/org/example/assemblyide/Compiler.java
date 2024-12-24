package org.example.assemblyide;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler implements EventHandler<ActionEvent> {
    private MemoryModel memoryModel;
    private TextEditor textEditor;
    private ArrayList<Instruction> instructions;
    private String error;

    private Pattern add = Pattern.compile("^add(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");
    private Pattern sub = Pattern.compile("^sub(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");
    private Pattern xor = Pattern.compile("^xor(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");
    private Pattern or = Pattern.compile("^or(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");
    private Pattern and = Pattern.compile("^and(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");
    private Pattern sll = Pattern.compile("^sll(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");
    private Pattern srl = Pattern.compile("^srl(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");
    private Pattern sra = Pattern.compile("^sra(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");
    private Pattern slt = Pattern.compile("^slt(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");
    private Pattern sltu = Pattern.compile("^sltu(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");

    private Pattern addi = Pattern.compile("^addi(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(0|[1-9][0-9]*)$");

    public Compiler(MemoryModel memoryModel, TextEditor textEditor) {
        this.memoryModel = memoryModel;
        this.textEditor = textEditor;
        this.instructions = new ArrayList<>();
        this.error = "";
    }

    public boolean compile() {
        this.instructions.clear();
        int lineNumber = 0;
        Matcher m;
        String[] lines = this.textEditor.getText().split("\n");
        for (String line: lines) {
            String originalLine = line;
            lineNumber++;
            String instruction = line.split(" ")[0];
            line = line.replaceAll("\\s+", "");
            if (line.isEmpty()) { continue; }
            switch (instruction) {
                case "add":
                    m = add.matcher(line);
                    break;
                case "sub":
                    m = sub.matcher(line);
                    break;
                case "xor":
                    m = xor.matcher(line);
                    break;
                case "or":
                    m = or.matcher(line);
                    break;
                case "and":
                    m = and.matcher(line);
                    break;
                case "sll":
                    m = sll.matcher(line);
                    break;
                case "srl":
                    m = srl.matcher(line);
                    break;
                case "sra":
                    m = sra.matcher(line);
                    break;
                case "slt":
                    m = slt.matcher(line);
                    break;
                case "sltu":
                    m = sltu.matcher(line);
                    break;
                case "addi":
                    m = addi.matcher(line);
                    break;
                default:
                    this.error = "Line " + lineNumber + ": '" + originalLine + "' Unrecognized instruction.";
                    return false;
            }
            if (!m.matches()) {
                this.error = "Line " + lineNumber + ": '" + originalLine + "' Invalid operands.";
                return false;
            }
            this.instructions.add(InstructionFactory.getInstruction(this.memoryModel, instruction, m));
        }
        this.error = "";
        this.memoryModel.resetPc();
        return true;
    }

    public String getError() {
        return this.error;
    }

    public ArrayList<Instruction> getInstructions() {
        return this.instructions;
    }

    @Override
    public void handle(ActionEvent e) {
        if (this.compile()) {
            System.out.println("Compilation successful.");
        } else {
            System.out.println("Error in compilation.");
            System.out.println(this.error);
        }
    }
}
