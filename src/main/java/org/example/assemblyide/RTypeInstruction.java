package org.example.assemblyide;

public class RTypeInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String name;
    private int rd;
    private int rs1;
    private int rs2;

    public RTypeInstruction(MemoryModel memoryModel, String name, int rd, int rs1, int rs2) {
        this.memoryModel = memoryModel;
        this.name = name;
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
    }

    @Override
    public boolean execute() {
        int rs1Val = this.memoryModel.getRegisterValue(this.rs1);
        int rs2Val = this.memoryModel.getRegisterValue(this.rs2);
        try {
            int rdVal = switch (this.name) {
                case "add" -> rs1Val + rs2Val;
                case "sub" -> rs1Val - rs2Val;
                case "xor" -> rs1Val ^ rs2Val;
                case "or" -> rs1Val | rs2Val;
                case "and" -> rs1Val & rs2Val;
                case "sll" -> rs1Val << rs2Val;
                case "srl" -> rs1Val >>> rs2Val;
                case "sra" -> rs1Val >> rs2Val;
                case "slt" -> (rs1Val < rs2Val) ? 1 : 0;
                case "sltu" -> (Integer.compareUnsigned(rs1Val, rs2Val) < 0) ? 1 : 0;
                default -> this.memoryModel.getRegisterValue(this.rd);
            };
            this.memoryModel.updateRegister(this.rd, rdVal);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
