package org.example.assemblyide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

public class InstructionFactory {
    private MemoryModel memoryModel;
    private Compiler compiler;
    private String error;
    private IOTerminal io;

    public InstructionFactory(MemoryModel memoryModel, Compiler compiler, IOTerminal io) {
        this.memoryModel = memoryModel;
        this.compiler = compiler;
        this.io = io;
        this.error = "";
    }

    public Instruction getInstruction(String instructionType, Matcher m, boolean usesLabel, int lineNum, String inputLine) {
        int rd, rs1, rs2, imm;
        switch (instructionType) {
            case "ecall":
                return new ECall(memoryModel, io, lineNum);
            case "add", "sub", "xor", "or", "and", "sll", "srl", "sra", "slt", "sltu", "mul", "mulh", "mulhsu", "mulhu", "div", "divu", "rem", "remu":
                rd = this.getRegister(m.group(2));
                rs1 = this.getRegister(m.group(3));
                rs2 = this.getRegister(m.group(4));
                return new RTypeInstruction(memoryModel, instructionType, rd, rs1, rs2, lineNum, inputLine);
            case "addi", "xori", "ori", "andi", "slli", "srli", "srai", "slti", "sltiu":
                rd = this.getRegister(m.group(2));
                rs1 = this.getRegister(m.group(3));
                imm = Integer.parseInt(m.group(4));
                return new ITypeInstruction(memoryModel, instructionType, rd, rs1, imm, lineNum, inputLine);
            case "lb", "lh", "lw", "lbu", "lhu":
                rd = this.getRegister(m.group(2));
                imm = Integer.parseInt(m.group(3));
                rs1 = this.getRegister(m.group(4));
                return new LoadInstruction(memoryModel, instructionType, rd, rs1, imm, lineNum, inputLine);
            case "sb", "sh", "sw":
                rs2 = this.getRegister(m.group(2));
                imm = Integer.parseInt(m.group(3));
                rs1 = this.getRegister(m.group(4));
                return new STypeInstruction(memoryModel, instructionType, rs1, rs2, imm, lineNum, inputLine);
            case "lui", "auipc":
                rd = this.getRegister(m.group(2));
                imm = Integer.parseInt(m.group(3));
                return new UTypeInstruction(memoryModel, instructionType, rd, imm, lineNum, inputLine);
            case "beq", "bne", "blt", "bge", "bltu", "bgeu":
                rs1 = this.getRegister(m.group(2));
                rs2 = this.getRegister(m.group(3));
                if (!usesLabel) {
                    imm = Integer.parseInt(m.group(4));
                    if (imm < -2048 || imm > 2047) {
                        this.error = "Immediate value " + imm +  "exceeds branch range.";
                        return null;
                    }
                } else {
                    imm = this.getImmFromLabel(m.group(4));
                    if (imm == -1) {
                        this.error = "Undefined reference to '" + m.group(4) + "'.";
                        return null;
                    }
                }
                return new BTypeInstruction(memoryModel, instructionType, rs1, rs2, imm, lineNum, inputLine);
            case "jal":
                rd = this.getRegister(m.group(1));
                if (!usesLabel) {
                    imm = Integer.parseInt(m.group(2));
                    if (imm < -524288 || imm > 524287) {
                        this.error = "Immediate value " + imm + "exceeds jal range.";
                        return null;
                    }
                } else {
                    imm = this.getImmFromLabel(m.group(2));
                    if (imm == -1) {
                        this.error = "Undefined reference to '" + m.group(2) + "'.";
                        return null;
                    }
                }
                return new JALInstruction(memoryModel, rd, imm, lineNum, inputLine);
            case "jalr":
                rd = this.getRegister(m.group(1));
                imm = Integer.parseInt(m.group(2));
                rs1 = this.getRegister(m.group(3));
                if (imm < -2048 || imm > 2047) {
                    this.error = "Immediate value " + imm +  "exceeds jalr range.";
                    return null;
                }
                return new JALRInstruction(memoryModel, rd, rs1, imm, lineNum, inputLine);
            case "la":
                rd = this.getRegister(m.group(1));
                Integer address = this.memoryModel.lookupVariable(m.group(2));
                if (address == null) {
                    this.error = "Undefined reference to '" + m.group(2) + "'.";
                    return null;
                }
                return new LAInstruction(memoryModel, rd, address, lineNum, inputLine);
            case "li":
                rd = this.getRegister(m.group(1));
                imm = Integer.parseInt(m.group(2));
                return new LIInstruction(memoryModel, rd, imm, lineNum, inputLine);
            case "mv", "not", "neg", "negw", "seqz", "snez", "sltz", "sgtz":
                rd = this.getRegister(m.group(2));
                int rs = this.getRegister(m.group(3));
                return new UnaryInstruction(memoryModel, instructionType, rd, rs, lineNum, inputLine);
            default:
                return null;
        }
    }

    public String getError() {
        return this.error;
    }

    private int getImmFromLabel(String label) {
        ArrayList<Instruction> instructions = this.compiler.getInstructions();
        try {
            int index = this.memoryModel.lookupLabel(label);
            return (index - instructions.size()) << 1;
        } catch (NullPointerException e) {
            return -1;
        }
    }

    private int getRegister(String regString) {
        if (regString.matches("x(0|[1-9]|1[0-9]|2[0-9]|3[0-1])")) {
            return Integer.parseInt(regString.replace("x", ""));
        }
        return Integer.parseInt(Util.registerMap.get(regString).replace("x", ""));
    }

}
