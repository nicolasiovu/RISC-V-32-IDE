package org.example.assemblyide;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstructionFactory {
    private MemoryModel memoryModel;

    public InstructionFactory(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;
    }

    public Instruction getInstruction(String instructionType, Matcher m, int instructions) {
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
            case "beq":
                rs1 = Integer.parseInt(m.group(1).replace("x", ""));
                rs2 = Integer.parseInt(m.group(2).replace("x", ""));
                // TODO: fix beq
            default:
                return null;
        }
    }
}
