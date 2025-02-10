package org.example.assemblyide;

public class ITypeInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String name;
    private String error;
    private int rd;
    private int rs1;
    private int imm;
    private int lineNum;
    private String line;

    public ITypeInstruction(MemoryModel memoryModel, String name, int rd, int rs1, int imm, int lineNum, String line) {
        this.memoryModel = memoryModel;
        this.name = name;
        this.error = "";
        this.rd = rd;
        this.rs1 = rs1;
        this.imm = imm;
        this.lineNum = lineNum;
        this.line = line;
    }

    @Override
    public String getError() {
        return this.error;
    }

    @Override
    public boolean execute() {
        int rs1Val = this.memoryModel.getRegisterValue(this.rs1);
        try {
            int rdVal = switch (this.name) {
                case "addi" -> rs1Val + imm;
                case "xori" -> rs1Val ^ imm;
                case "ori" -> rs1Val | imm;
                case "andi" -> rs1Val & imm;
                case "slli" -> rs1Val << imm;
                case "srli" -> rs1Val >>> imm;
                case "srai" -> rs1Val >> imm;
                case "slti" -> (rs1Val < imm) ? 1 : 0;
                case "sltui" -> (Integer.compareUnsigned(rs1Val, imm) < 0) ? 1 : 0;
                default -> throw new IllegalArgumentException("Unknown instruction");
            };
            this.memoryModel.updateRegister(this.rd, rdVal);
            this.memoryModel.updatePc(4);
        } catch (Exception e) {
            this.error = e.getMessage();
            return false;
        }
        return true;
    }

    @Override
    public String getInstructionInfo() {
        return "Line " + this.lineNum + ": " + this.line.replaceFirst("^\\s+", "");
    }
}
