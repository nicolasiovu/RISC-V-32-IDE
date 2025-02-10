package org.example.assemblyide;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler implements EventHandler<ActionEvent> {
    private MemoryModel memoryModel;
    private TextEditor textEditor;
    private TerminalPanel terminalPanel;
    private IOTerminal io;
    private ArrayList<Instruction> instructions;
    private String error;
    private boolean usesLabel;

    private Pattern ecall = Pattern.compile("^ecall$");

    private Pattern label = Pattern.compile("^[a-zA-Z_]+[a-zA-Z0-9_]*:$");

    private Pattern rType = Pattern.compile("^(add|sub|xor|or|and|sll|srl|sra|slt|sltu)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");

    private Pattern iType = Pattern.compile("^(addi|xori|ori|andi|slli|srli|srai|slti|sltiu)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");

    private Pattern loadType = Pattern.compile("^(lb|lh|lw|lbu|lhu)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");

    private Pattern storeType = Pattern.compile("^(sb|sh|sw)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");

    private Pattern bTypeImm = Pattern.compile("^(beq|bne|blt|bge|bltu|bgeu)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern bTypeLabel = Pattern.compile("^(beq|bne|blt|bge|bltu|bgeu)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)$");

    private Pattern jalImm = Pattern.compile("^jal(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern jalLabel = Pattern.compile("^jal(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)$");
    private Pattern jalr = Pattern.compile("^jalr(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");

    public Compiler(MemoryModel memoryModel, TextEditor textEditor, TerminalPanel terminalPanel, IOTerminal io) {
        this.memoryModel = memoryModel;
        this.textEditor = textEditor;
        this.terminalPanel = terminalPanel;
        this.io = io;
        this.instructions = new ArrayList<>();
        this.error = "";
        this.usesLabel = false;
    }

    public boolean compile() {
        this.instructions.clear();
        this.memoryModel.resetLabels();
        this.setLabels();
        this.usesLabel = false;
        InstructionFactory instructionFactory = new InstructionFactory(this.memoryModel, this, io);
        int lineNumber = 0;
        Matcher m;
        String[] lines = this.textEditor.getText().split("\n");
        for (String line: lines) {
            lineNumber++;
            this.usesLabel = false;
            String inputLine = line;
            line = line.trim();
            if (label.matcher(line).matches()) {
                continue;
            }
            String originalLine = line;
            if (line.isEmpty()) { continue; }
            m = this.decodeLine(line);
            line = line.trim();
            String instruction = line.split(" ")[0];
            if (m == null) {
                this.error = "Line " + lineNumber + ": '" + originalLine + "' Unrecognized instruction.";
                return false;
            }
            if (!m.matches()) {
                this.error = "Line " + lineNumber + ": '" + originalLine + "' Invalid operands.";
                return false;
            }
            Instruction toAdd = instructionFactory.getInstruction(instruction, m, usesLabel, lineNumber, inputLine);
            if (toAdd == null) {
                this.error = "Line " + lineNumber + ": " + originalLine + instructionFactory.getError();
                return false;
            }
            this.instructions.add(instructionFactory.getInstruction(instruction, m, usesLabel, lineNumber, inputLine));
        }
        this.error = "";
        this.memoryModel.resetPc();
        return true;
    }

    private Matcher decodeLine(String line) {
        Matcher m;
        line = line.trim();
        String instruction = line.split(" ")[0];
        line = line.replaceAll("\\s+", "");
        switch (instruction) {
            case "ecall":
                m = ecall.matcher(line);
                break;
            case "add", "sub", "xor", "or", "and", "sll", "srl", "sra", "slt", "sltu":
                m = rType.matcher(line);
                break;
            case "addi", "xori", "ori", "andi", "slli", "srli", "srai", "slti", "sltiu":
                m = iType.matcher(line);
                break;
            case "lb", "lh", "lw", "lbu", "lhu":
                m = loadType.matcher(line);
                break;
            case "sb", "sh", "sw":
                m = storeType.matcher(line);
                break;
            case "beq", "bne", "blt", "bge", "bltu", "bgeu":
                if (bTypeLabel.matcher(line).matches()) {
                    m = bTypeLabel.matcher(line);
                    this.usesLabel = true;
                } else {
                    m = bTypeImm.matcher(line);
                }
                break;
            case "jal":
                if (jalLabel.matcher(line).matches()) {
                    m = jalLabel.matcher(line);
                    this.usesLabel = true;
                } else {
                    m = jalImm.matcher(line);
                }
                break;
            case "jalr":
                m = jalr.matcher(line);
                break;
            default:
                return null;
        }
        return m;
    }

    public void setLabels() {
        String[] lines = this.textEditor.getText().split("\n");
        int numInstructions = 0;
        for (String line: lines) {
            String potentialLabel = line.trim();
            if (label.matcher(potentialLabel).matches()) {
                this.memoryModel.addLabel(potentialLabel.replace(":", ""), numInstructions);
            }
            Matcher m = this.decodeLine(line);
            if (m != null && m.matches()) {
                numInstructions++;
            }
        }
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
            this.terminalPanel.print("Compiled successfully.");
        } else {
            this.terminalPanel.print("Error in compilation.");
            this.terminalPanel.print(this.error);
        }
    }
}
