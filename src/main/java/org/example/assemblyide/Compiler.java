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
    private boolean usesLabel;

    private Pattern label = Pattern.compile("^[a-zA-Z_]+[a-zA-Z0-9_]*:$");

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

    private Pattern addi = Pattern.compile("^addi(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern xori = Pattern.compile("^xori(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern ori = Pattern.compile("^ori(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern andi = Pattern.compile("^andi(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern slli = Pattern.compile("^slli(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern srli = Pattern.compile("^srli(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern srai = Pattern.compile("^srai(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern slti = Pattern.compile("^slti(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern sltiu = Pattern.compile("^sltiu(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");

    private Pattern lb = Pattern.compile("^lb(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");
    private Pattern lh = Pattern.compile("^lh(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");
    private Pattern lw = Pattern.compile("^lw(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");
    private Pattern lbu = Pattern.compile("^lbu(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");
    private Pattern lhu = Pattern.compile("^lhu(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");

    private Pattern sb = Pattern.compile("^sb(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");
    private Pattern sh = Pattern.compile("^sh(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");
    private Pattern sw = Pattern.compile("^sw(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");

    private Pattern beqImm = Pattern.compile("^beq(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern beqLabel = Pattern.compile("^beq(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)$");
    private Pattern bneImm = Pattern.compile("^bne(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern bneLabel = Pattern.compile("^bne(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)$");
    private Pattern bltImm = Pattern.compile("^blt(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern bltLabel = Pattern.compile("^blt(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)$");
    private Pattern bgeImm = Pattern.compile("^bge(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern bgeLabel = Pattern.compile("^bge(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)$");
    private Pattern bltuImm = Pattern.compile("^bltu(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern bltuLabel = Pattern.compile("^bltu(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)$");
    private Pattern bgeuImm = Pattern.compile("^bgeu(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern bgeuLabel = Pattern.compile("^bgeu(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)$");

    private Pattern jalImm = Pattern.compile("^jal(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)$");
    private Pattern jalLabel = Pattern.compile("^jal(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)$");
    private Pattern jalr = Pattern.compile("^jalr(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)");

    public Compiler(MemoryModel memoryModel, TextEditor textEditor) {
        this.memoryModel = memoryModel;
        this.textEditor = textEditor;
        this.instructions = new ArrayList<>();
        this.error = "";
        this.usesLabel = false;
    }

    public boolean compile() {
        this.instructions.clear();
        this.memoryModel.resetLabels();
        this.setLabels();
        this.usesLabel = false;
        InstructionFactory instructionFactory = new InstructionFactory(this.memoryModel, this);
        int lineNumber = 0;
        Matcher m;
        String[] lines = this.textEditor.getText().split("\n");
        for (String line: lines) {
            this.usesLabel = false;
            line = line.trim();
            if (label.matcher(line).matches()) {
                continue;
            }
            String originalLine = line;
            lineNumber++;
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
            Instruction toAdd = instructionFactory.getInstruction(instruction, m, usesLabel);
            if (toAdd == null) {
                this.error = "Line " + lineNumber + ": '" + originalLine + instructionFactory.getError();
                return false;
            }
            this.instructions.add(instructionFactory.getInstruction(instruction, m, usesLabel));
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
            case "xori":
                m = xori.matcher(line);
                break;
            case "ori":
                m = ori.matcher(line);
                break;
            case "andi":
                m = andi.matcher(line);
                break;
            case "slli":
                m = slli.matcher(line);
                break;
            case "srli":
                m = srli.matcher(line);
                break;
            case "srai":
                m = srai.matcher(line);
                break;
            case "slti":
                m = slti.matcher(line);
                break;
            case "sltiu":
                m = sltiu.matcher(line);
                break;
            case "lb":
                m = lb.matcher(line);
                break;
            case "lh":
                m = lh.matcher(line);
                break;
            case "lw":
                m = lw.matcher(line);
                break;
            case "lbu":
                m = lbu.matcher(line);
                break;
            case "lhu":
                m = lhu.matcher(line);
                break;
            case "sb":
                m = sb.matcher(line);
                break;
            case "sh":
                m = sh.matcher(line);
                break;
            case "sw":
                m = sw.matcher(line);
                break;
            case "beq":
                if (beqLabel.matcher(line).matches()) {
                    m = beqLabel.matcher(line);
                    this.usesLabel = true;
                } else {
                    m = beqImm.matcher(line);
                }
                break;
            case "bne":
                if (bneLabel.matcher(line).matches()) {
                    m = bneLabel.matcher(line);
                    this.usesLabel = true;
                } else {
                    m = bneImm.matcher(line);
                }
                break;
            case "blt":
                if (bltLabel.matcher(line).matches()) {
                    m = bltLabel.matcher(line);
                    this.usesLabel = true;
                } else {
                    m = bltImm.matcher(line);
                }
                break;
            case "bge":
                if (bgeLabel.matcher(line).matches()) {
                    m = bgeLabel.matcher(line);
                    this.usesLabel = true;
                } else {
                    m = bgeImm.matcher(line);
                }
                break;
            case "bltu":
                if (bltuLabel.matcher(line).matches()) {
                    m = bltuLabel.matcher(line);
                    this.usesLabel = true;
                } else {
                    m = bltuImm.matcher(line);
                }
                break;
            case "bgeu":
                if (bgeuLabel.matcher(line).matches()) {
                    m = bgeuLabel.matcher(line);
                    this.usesLabel = true;
                } else {
                    m = bgeuImm.matcher(line);
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
            System.out.println("Compilation successful.");
        } else {
            System.out.println("Error in compilation.");
            System.out.println(this.error);
        }
    }
}
