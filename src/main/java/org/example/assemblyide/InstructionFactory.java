package org.example.assemblyide;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstructionFactory {
    private MemoryModel memoryModel;
    private Compiler compiler;
    private String error;

    public InstructionFactory(MemoryModel memoryModel, Compiler compiler) {
        this.memoryModel = memoryModel;
        this.compiler = compiler;
        this.error = "";
    }

    public Instruction getInstruction(String instructionType, Matcher m, boolean usesLabel) {
        int rd, rs1, rs2, imm;
        switch (instructionType) {
            case "add", "sub", "xor", "or", "and", "sll", "srl", "sra", "slt", "sltu":
                rd = Integer.parseInt(m.group(1).replace("x", ""));
                rs1 = Integer.parseInt(m.group(2).replace("x", ""));
                rs2 = Integer.parseInt(m.group(3).replace("x", ""));
                return new RTypeInstruction(memoryModel, instructionType, rd, rs1, rs2);
            case "addi", "xori", "ori", "andi", "slli", "srli", "srai", "slti", "sltiu":
                rd = Integer.parseInt(m.group(1).replace("x", ""));
                rs1 = Integer.parseInt(m.group(2).replace("x", ""));
                imm = Integer.parseInt(m.group(3));
                return new ITypeInstruction(memoryModel, instructionType, rd, rs1, imm);
            case "lb", "lh", "lw", "lbu", "lhu":
                rd = Integer.parseInt(m.group(1).replace("x", ""));
                imm = Integer.parseInt(m.group(2));
                rs1 = Integer.parseInt(m.group(3).replace("x", ""));
                return new LoadInstruction(memoryModel, instructionType, rd, rs1, imm);
            case "sb", "sh", "sw":
                rs2 = Integer.parseInt(m.group(1).replace("x", ""));
                imm = Integer.parseInt(m.group(2));
                rs1 = Integer.parseInt(m.group(3).replace("x", ""));
                return new STypeInstruction(memoryModel, instructionType, rs1, rs2, imm);
            case "beq", "bne", "blt", "bge", "bltu", "bgeu":
                rs1 = Integer.parseInt(m.group(1).replace("x", ""));
                rs2 = Integer.parseInt(m.group(2).replace("x", ""));
                if (!usesLabel) {
                    imm = Integer.parseInt(m.group(3));
                    if (imm < -2048 || imm > 2047) {
                        this.error = "Immediate value " + imm +  "exceeds branch range.";
                        return null;
                    }
                } else {
                    imm = this.getImmFromLabel(m.group(3));
                    if (imm == -1) {
                        this.error = "Undefined reference to '" + m.group(3) + "'.";
                        return null;
                    }
                }
                return new BTypeInstruction(memoryModel, instructionType, rs1, rs2, imm);
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
}
