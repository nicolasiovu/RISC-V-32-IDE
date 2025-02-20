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
                if (m.group(4).contains("x")) {
                    String hex = m.group(4).split("x")[1];
                    imm = (int) Long.parseLong(hex, 16);
                } else {
                    imm = Integer.parseInt(m.group(4));
                }
                return new ITypeInstruction(memoryModel, instructionType, rd, rs1, imm, lineNum, inputLine);
            case "lb", "lh", "lw", "lbu", "lhu":
                rd = this.getRegister(m.group(2));
                if (m.group(3).contains("x")) {
                    String hex = m.group(3).split("x")[1];
                    imm = (int) Long.parseLong(hex, 16);
                } else {
                    imm = Integer.parseInt(m.group(3));
                }
                rs1 = this.getRegister(m.group(4));
                return new LoadInstruction(memoryModel, instructionType, rd, rs1, imm, lineNum, inputLine);
            case "sb", "sh", "sw":
                rs2 = this.getRegister(m.group(2));
                if (m.group(3).contains("x")) {
                    String hex = m.group(3).split("x")[1];
                    imm = (int) Long.parseLong(hex, 16);
                } else {
                    imm = Integer.parseInt(m.group(3));
                }
                rs1 = this.getRegister(m.group(4));
                return new STypeInstruction(memoryModel, instructionType, rs1, rs2, imm, lineNum, inputLine);
            case "lui", "auipc":
                rd = this.getRegister(m.group(2));
                if (m.group(3).contains("x")) {
                    String hex = m.group(3).split("x")[1];
                    imm = (int) Long.parseLong(hex, 16);
                } else {
                    imm = Integer.parseInt(m.group(3));
                }
                return new UTypeInstruction(memoryModel, instructionType, rd, imm, lineNum, inputLine);
            case "beq", "bne", "blt", "bge", "bltu", "bgeu", "bgt", "ble", "bgtu", "bleu":
                rs1 = this.getRegister(m.group(2));
                rs2 = this.getRegister(m.group(3));
                if (!usesLabel) {
                    if (m.group(4).contains("x")) {
                        String hex = m.group(4).split("x")[1];
                        imm = (int) Long.parseLong(hex, 16);
                    } else {
                        imm = Integer.parseInt(m.group(4));
                    }
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
            case "beqz", "bnez", "blez", "bgez", "bltz", "bgtz":
                int rsB = this.getRegister(m.group(2));
                if (!usesLabel) {
                    if (m.group(3).contains("x")) {
                        String hex = m.group(3).split("x")[1];
                        imm = (int) Long.parseLong(hex, 16);
                    } else {
                        imm = Integer.parseInt(m.group(3));
                    }
                    if (imm < -2048 || imm > 2047) {
                        this.error = "Immediate value " + imm +  "exceeds branch range.";
                        return null;
                    }
                } else {
                    imm = this.getImmFromLabel(m.group(3));
                    if (imm == -1) {
                        this.error = "Undefined reference to '" + m.group(4) + "'.";
                        return null;
                    }
                }
                return new BTypePseudoInstruction(memoryModel, instructionType, rsB, imm, lineNum, inputLine);
            case "jal":
                if (m.groupCount() == 1) {
                    rd = 1;
                    if (!usesLabel) {
                        if (m.group(1).contains("x")) {
                            String hex = m.group(1).split("x")[1];
                            imm = (int) Long.parseLong(hex, 16);
                        } else {
                            imm = Integer.parseInt(m.group(1));
                        }
                        if (imm < -524288 || imm > 524287) {
                            this.error = "Immediate value " + imm + "exceeds jal range.";
                            return null;
                        }
                    } else {
                        imm = this.getImmFromLabel(m.group(1));
                        if (imm == -1) {
                            this.error = "Undefined reference to '" + m.group(2) + "'.";
                            return null;
                        }
                    }
                } else {
                    rd = this.getRegister(m.group(1));
                    if (!usesLabel) {
                        if (m.group(2).contains("x")) {
                            String hex = m.group(2).split("x")[1];
                            imm = (int) Long.parseLong(hex, 16);
                        } else {
                            imm = Integer.parseInt(m.group(2));
                        }
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
                }
                return new JALInstruction(memoryModel, rd, imm, lineNum, inputLine);
            case "j":
                rd = 0;
                if (!usesLabel) {
                    if (m.group(1).contains("x")) {
                        String hex = m.group(1).split("x")[1];
                        imm = (int) Long.parseLong(hex, 16);
                    } else {
                        imm = Integer.parseInt(m.group(1));
                    }
                    if (imm < -524288 || imm > 524287) {
                        this.error = "Immediate value " + imm + "exceeds jal range.";
                        return null;
                    }
                } else {
                    imm = this.getImmFromLabel(m.group(1));
                    if (imm == -1) {
                        this.error = "Undefined reference to '" + m.group(2) + "'.";
                        return null;
                    }
                }
                return new JALInstruction(memoryModel, rd, imm, lineNum, inputLine);
            case "jalr":
                if (m.groupCount() == 1) {
                    rd = 1;
                    int rs = this.getRegister(m.group(1));
                    imm = 0;
                    return new JALRInstruction(memoryModel, rd, rs, imm, lineNum, inputLine);
                } else {
                    rd = this.getRegister(m.group(1));
                    if (m.group(2).contains("x")) {
                        String hex = m.group(2).split("x")[1];
                        imm = (int) Long.parseLong(hex, 16);
                    } else {
                        imm = Integer.parseInt(m.group(2));
                    }
                    rs1 = this.getRegister(m.group(3));
                    if (imm < -2048 || imm > 2047) {
                        this.error = "Immediate value " + imm + "exceeds jalr range.";
                        return null;
                    }
                    return new JALRInstruction(memoryModel, rd, rs1, imm, lineNum, inputLine);
                }
            case "jr":
                rd = 0;
                int rs = this.getRegister(m.group(1));
                imm = 0;
                return new JALRInstruction(memoryModel, rd, rs, imm, lineNum, inputLine);
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
                if (m.group(2).contains("x")) {
                    String hex = m.group(2).split("x")[1];
                    imm = (int) Long.parseLong(hex, 16);
                } else {
                    imm = Integer.parseInt(m.group(2));
                }
                return new LIInstruction(memoryModel, rd, imm, lineNum, inputLine);
            case "mv", "not", "neg", "negw", "seqz", "snez", "sltz", "sgtz":
                rd = this.getRegister(m.group(2));
                int rsJR = this.getRegister(m.group(3));
                return new UnaryInstruction(memoryModel, instructionType, rd, rsJR, lineNum, inputLine);
            case "ret":
                rd = 0;
                imm = 0;
                rs1 = 1;
                return new JALRInstruction(memoryModel, rd, rs1, imm, lineNum, inputLine);
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
