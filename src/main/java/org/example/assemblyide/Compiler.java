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
    private boolean inData;
    private int staticAddressPtr;

    private String startingLabel;

    private Pattern wordStatic = Pattern.compile("^([a-zA-Z_]+[a-zA-Z0-9_]*):\\.word(-?\\d+|0x[0-9a-fA-F]+)(,(-?\\d+|0x[0-9a-fA-F]+))*$");
    private Pattern wordGrouper = Pattern.compile("(\\w+):\\.word(.+)");

    private Pattern halfStatic = Pattern.compile("^([a-zA-Z_]+[a-zA-Z0-9_]*):\\.half(-?\\d+|0x[0-9a-fA-F]+)(,(-?\\d+|0x[0-9a-fA-F]+))*$");
    private Pattern halfGrouper = Pattern.compile("(\\w+):\\.half(.+)");


    private Pattern byteStatic = Pattern.compile("^([a-zA-Z_]+[a-zA-Z0-9_]*):\\.byte(-?\\d+|0x[0-9a-fA-F]+)(,(-?\\d+|0x[0-9a-fA-F]+))*$");
    private Pattern byteGrouper = Pattern.compile("(\\w+):\\.byte(.+)");


    private Pattern stringStatic = Pattern.compile("^\\s*([a-zA-Z_]+[a-zA-Z0-9_]*):\\s*(\\.asciz|\\.string)\\s*\"(.*)\"\\s*$");

    private Pattern ecall = Pattern.compile("^ecall$");

    private Pattern label = Pattern.compile("^[a-zA-Z_]+[a-zA-Z0-9_]*:$");

    private Pattern rType = Pattern.compile("^(add|sub|xor|or|and|sll|srl|sra|slt|sltu|mul|mulh|mulhsu|mulhu|div|divu|rem|remu)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");

    private Pattern iType = Pattern.compile("^(addi|xori|ori|andi|slli|srli|srai|slti|sltiu)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?\\d+|0x[0-9a-fA-F]+)$");

    private Pattern loadType = Pattern.compile("^(lb|lh|lw|lbu|lhu)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");

    private Pattern storeType = Pattern.compile("^(sb|sh|sw)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");

    private Pattern bTypeImm = Pattern.compile("^(beq|bne|blt|bge|bltu|bgeu|bgt|ble|bgtu|bleu)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?\\d+|0x[0-9a-fA-F]+)$");
    private Pattern bTypeLabel = Pattern.compile("^(beq|bne|blt|bge|bltu|bgeu|bgt|ble|bgtu|bleu)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)$");

    private Pattern bTypePseudoImm = Pattern.compile("^(beqz|bnez|blez|bgez|bltz|bgtz)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?\\d+|0x[0-9a-fA-F]+)$");
    private Pattern bTypePseudoLabel = Pattern.compile("^(beqz|bnez|blez|bgez|bltz|bgtz)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)$");

    private Pattern jalImm = Pattern.compile("^jal(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?\\d+|0x[0-9a-fA-F]+)$");
    private Pattern jalShortImm = Pattern.compile("^jal(-?\\d+|0x[0-9a-fA-F]+)$");
    private Pattern jalLabel = Pattern.compile("^jal(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)$");
    private Pattern jalShortLabel = Pattern.compile("^jal([a-zA-Z_]+[a-zA-Z0-9_]*)$");

    private Pattern jalr = Pattern.compile("^jalr(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?0|-?[1-9][0-9]*)\\((zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])\\)$");
    private Pattern jalrShort = Pattern.compile("^jalr(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");

    private Pattern uType = Pattern.compile("^(lui|auipc)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?\\d+|0x[0-9a-fA-F]+)$");

    private Pattern la = Pattern.compile("^la(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),([a-zA-Z_]+[a-zA-Z0-9_]*)$");

    private Pattern li = Pattern.compile("^li(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(-?\\d+|0x[0-9a-fA-F]+)$");

    private Pattern unary = Pattern.compile("^(mv|not|neg|negw|seqz|snez|sltz|sgtz)(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1]),(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");

    private Pattern jImm = Pattern.compile("^j(-?\\d+|0x[0-9a-fA-F]+)$");
    private Pattern jLabel = Pattern.compile("^j([a-zA-Z_]+[a-zA-Z0-9_]*)$");

    private Pattern jr = Pattern.compile("^jr(zero|ra|sp|gp|tp|t[0-6]|s[0-9]|s1[0-1]|a[0-7]|x[0-9]|x1[0-9]|x2[0-9]|x3[0-1])$");

    private Pattern ret = Pattern.compile("^ret$");

    private Pattern globl = Pattern.compile("^\\.(globl|global)([a-zA-Z_]+[a-zA-Z0-9_]*)");

    public Compiler(MemoryModel memoryModel, TextEditor textEditor, TerminalPanel terminalPanel, IOTerminal io) {
        this.memoryModel = memoryModel;
        this.textEditor = textEditor;
        this.terminalPanel = terminalPanel;
        this.io = io;
        this.instructions = new ArrayList<>();
        this.error = "";
        this.usesLabel = false;
        this.inData = false;
        this.staticAddressPtr = 0x00000004;
        this.startingLabel = null;
    }

    public boolean compile() {
        this.instructions.clear();
        this.memoryModel.resetLabels();
        this.staticAddressPtr = 0x00000004;
        this.startingLabel = null;
        this.setLabels();
        this.inData = false;
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
            if (line.startsWith("#") || line.split("#").length == 0) continue;
            if (line.equals(".data")) {
                this.inData = true;
                continue;
            }
            if (line.equals(".text")) {
                this.inData = false;
                continue;
            }
            if (label.matcher(line).matches()) {
                continue;
            }
            String originalLine = line;
            if (line.isEmpty()) { continue; }
            if (this.inData) {
                if (!this.handleStaticVariable(originalLine)) {
                    this.error = "Line " + lineNumber + ": '" + originalLine + "' Invalid variable declaration.";
                    return false;
                }
                continue;
            }
            if ((m = this.globl.matcher(line)).matches() && !this.inData) {
                String label = m.group(2);
                if (this.memoryModel.lookupLabel(label) == null) {
                    this.error = "Line " + lineNumber + ": '" + originalLine + "' Unrecognized label.";
                    return false;
                }
                this.startingLabel = label;
            }
            m = this.decodeInstruction(line);
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

    private Matcher decodeInstruction(String line) {
        Matcher m;
        line = line.split("#")[0];
        line = line.trim();
        String instruction = line.split(" ")[0];
        line = line.replaceAll("\\s+", "");
        switch (instruction) {
            case "ecall":
                m = ecall.matcher(line);
                break;
            case "add", "sub", "xor", "or", "and", "sll", "srl", "sra", "slt", "sltu", "mul", "mulh", "mulhsu", "mulhu", "div", "divu", "rem", "remu":
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
            case "lui", "auipc":
                m = uType.matcher(line);
                break;
            case "beq", "bne", "blt", "bge", "bltu", "bgeu", "bgt", "ble", "bgtu", "bleu":
                if (bTypeLabel.matcher(line).matches()) {
                    m = bTypeLabel.matcher(line);
                    this.usesLabel = true;
                } else {
                    m = bTypeImm.matcher(line);
                }
                break;
            case "beqz", "bnez", "blez", "bgez", "bltz", "bgtz":
                if (bTypePseudoLabel.matcher(line).matches()) {
                    m = bTypePseudoLabel.matcher(line);
                    this.usesLabel = true;
                } else {
                    m = bTypePseudoImm.matcher(line);
                }
                break;
            case "jal":
                if (jalLabel.matcher(line).matches()) {
                    m = jalLabel.matcher(line);
                    this.usesLabel = true;
                } else if (jalShortLabel.matcher(line).matches()) {
                    m = jalShortLabel.matcher(line);
                    this.usesLabel = true;
                } else if (jalShortImm.matcher(line).matches()) {
                    m = jalShortImm.matcher(line);
                } else {
                    m = jalImm.matcher(line);
                }
                break;
            case "j":
                if (jLabel.matcher(line).matches()) {
                    m = jLabel.matcher(line);
                    this.usesLabel = true;
                } else {
                    m = jImm.matcher(line);
                }
                break;
            case "jalr":
                if (jalrShort.matcher(line).matches()) {
                    m = jalrShort.matcher(line);
                } else {
                    m = jalr.matcher(line);
                }
                break;
            case "jr":
                m = jr.matcher(line);
                break;
            case "la":
                m = la.matcher(line);
                break;
            case "li":
                m = li.matcher(line);
                break;
            case "mv", "not", "neg", "negw", "seqz", "snez", "sltz", "sgtz":
                m = unary.matcher(line);
                break;
            case "ret":
                m = ret.matcher(line);
                break;
            default:
                return null;
        }
        return m;
    }

    private boolean handleStaticVariable(String line) {
        line = line.trim();
        Matcher m = stringStatic.matcher(line);
        if (m.matches()) {
            String name = m.group(1);
            this.memoryModel.addVariable(name, this.staticAddressPtr);
            String value = this.fixEscapeCharacters(m.group(3));
            for (char c: value.toCharArray()) {
                this.memoryModel.writeByte(this.staticAddressPtr, (byte) c);
                this.staticAddressPtr++;
            }
            this.memoryModel.writeByte(this.staticAddressPtr, (byte) '\0');
            this.staticAddressPtr++;
            return true;
        }
        line = line.replaceAll("\\s+", "");
        m = wordStatic.matcher(line);
        Matcher groups = wordGrouper.matcher(line);
        if (m.matches() && groups.matches()) {
            String name = groups.group(1);
            this.memoryModel.addVariable(name, this.staticAddressPtr);
            String[] inputs = groups.group(2).split(",");
            for (String input: inputs) {
                int value;
                if (input.contains("x")) {
                    value = Integer.parseInt(input, 16);
                } else {
                    value = Integer.parseInt(input);
                }
                this.memoryModel.writeWord(this.staticAddressPtr, value);
                this.staticAddressPtr += 4;
            }
            return true;
        }
        m = halfStatic.matcher(line);
        groups = halfGrouper.matcher(line);
        if (m.matches() && groups.matches()) {
            String name = groups.group(1);
            this.memoryModel.addVariable(name, this.staticAddressPtr);
            String[] inputs = groups.group(2).split(",");
            for (String input: inputs) {
                int value;
                if (input.contains("x")) {
                    String hex = input.split("x")[1];
                    value = (int) Long.parseLong(hex, 16);
                } else {
                    value = Integer.parseInt(input);
                }
                short val = (short) (value & 0xFFFF);
                this.memoryModel.writeByte(this.staticAddressPtr, (byte) (val & 0xFF));
                this.memoryModel.writeByte(this.staticAddressPtr + 1, (byte) ((val >> 8) & 0xFF));
                this.staticAddressPtr += 2;
            }
            return true;
        }
        m = byteStatic.matcher(line);
        groups = byteGrouper.matcher(line);
        if (m.matches() && groups.matches()) {
            String name = groups.group(1);
            this.memoryModel.addVariable(name, this.staticAddressPtr);
            String[] inputs = groups.group(2).split(",");
            for (String input: inputs) {
                int value;
                if (input.contains("x")) {
                    String hex = input.split("x")[1];
                    value = (int) Long.parseLong(hex, 16);
                } else {
                    value = Integer.parseInt(input);
                }
                this.memoryModel.writeByte(this.staticAddressPtr, (byte) (value & 0xFF));
                this.staticAddressPtr++;
            }
            return true;
        }
        return false;
    }

    private String fixEscapeCharacters(String input) {
        StringBuilder sb = new StringBuilder();
        int length = input.length();
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (c == '\\' && i + 1 < length) {
                char next = input.charAt(i + 1);
                switch (next) {
                    case 'n':
                        sb.append('\n');
                        i++;
                        break;
                    case 't':
                        sb.append('\t');
                        i++;
                        break;
                    case 'r':
                        sb.append('\r');
                        i++;
                        break;
                    case '\\':
                        sb.append('\\');
                        i++;
                        break;
                    case '"':
                        sb.append('"');
                        i++;
                        break;
                    default:
                        sb.append(c);
                        break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public void setLabels() {
        String[] lines = this.textEditor.getText().split("\n");
        int numInstructions = 0;
        this.inData = false;
        for (String line: lines) {
            String potentialLabel = line.trim();
            if (line.equals(".data")) {
                this.inData = true;
                continue;
            }
            if (line.equals(".text")) {
                this.inData = false;
                continue;
            }
            if (line.startsWith("#") || line.split("#").length == 0) continue;
            if (!inData) {
                if (label.matcher(potentialLabel).matches()) {
                    this.memoryModel.addLabel(potentialLabel.replace(":", ""), numInstructions);
                }
                Matcher m = this.decodeInstruction(line);
                if (m != null && m.matches()) {
                    numInstructions++;
                }
            }
        }
    }

    public String getError() {
        return this.error;
    }

    public ArrayList<Instruction> getInstructions() {
        return this.instructions;
    }

    public String getStartingLabel() {
        return this.startingLabel;
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