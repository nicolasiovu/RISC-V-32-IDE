package org.example.assemblyide;

public class JALRInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String error;
    private int rd;
    private int rs1;
    private int imm;
    private int lineNum;
    private String line;

    public JALRInstruction(MemoryModel memoryModel, int rd, int rs1, int imm, int lineNum, String line) {
        this.memoryModel = memoryModel;
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
        int immBytes = this.imm << 1;
        this.memoryModel.updateRegister(this.rd, this.memoryModel.getPc() + 4);
        if (!this.memoryModel.setPc((this.memoryModel.getRegisterValue(this.rs1) + immBytes) & ~1)) {
            this.error = "pc = " + this.memoryModel.getRegisterValue(this.rs1) + immBytes + " is invalid.";
            return false;
        }
        return true;
    }

    @Override
    public String getInstructionInfo() {
        return "Line " + this.lineNum + ": " + this.line.replaceFirst("^\\s+", "");
    }
}
