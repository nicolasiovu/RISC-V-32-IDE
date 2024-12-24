package org.example.assemblyide;

import java.util.regex.Matcher;

public class InstructionFactory {
    public static Instruction getInstruction(MemoryModel memoryModel, String instructionType, Matcher m) {
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
            default:
                return null;
        }
    }
}
