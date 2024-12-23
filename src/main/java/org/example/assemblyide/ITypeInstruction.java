package org.example.assemblyide;

public class ITypeInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String name;
    private int rd;
    private int rs1;
    private int imm;

    public ITypeInstruction(MemoryModel memoryModel, String name, int rd, int rs1, int imm) {
        this.memoryModel = memoryModel;
        this.name = name;
        this.rd = rd;
        this.rs1 = rs1;
        this.imm = imm;
    }

    @Override
    public boolean execute() {
        int rs1Val = this.memoryModel.getRegisterValue(this.rs1);
        try {
            int rdVal = switch (this.name) {
                case "addi" -> rs1Val + imm;
                case "subi" -> rs1Val - imm;
                case "xori" -> rs1Val ^ imm;
                case "ori" -> rs1Val | imm;
                case "andi" -> rs1Val & imm;
                case "slli" -> rs1Val << imm;
                case "srli" -> rs1Val >>> imm;
                case "srai" -> rs1Val >> imm;
                case "slti" -> (rs1Val < imm) ? 1 : 0;
                case "sltui" -> (Integer.compareUnsigned(rs1Val, imm) < 0) ? 1 : 0;
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
