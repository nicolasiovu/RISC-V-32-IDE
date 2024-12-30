package org.example.assemblyide;

public class JALRInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String error;
    private int rd;
    private int rs1;
    private int imm;

    public JALRInstruction(MemoryModel memoryModel, int rd, int rs1, int imm) {
        this.memoryModel = memoryModel;
        this.error = "";
        this.rd = rd;
        this.rs1 = rs1;
        this.imm = imm;
    }

    @Override
    public String getError() {
        return this.error;
    }

    @Override
    public boolean execute() {
        int immBytes = this.imm << 1;
        this.memoryModel.updateRegister(this.rd, this.memoryModel.getPc() + 4);
        if (!this.memoryModel.updatePc(this.memoryModel.getRegisterValue(this.rs1) + immBytes)) {
            this.error = "pc = " + this.memoryModel.getRegisterValue(this.rs1) + immBytes + " is invalid.";
            return false;
        }
        return true;
    }
}
