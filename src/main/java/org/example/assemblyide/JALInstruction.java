package org.example.assemblyide;

public class JALInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String error;
    private int rd;
    private int imm;

    public JALInstruction(MemoryModel memoryModel, int rd, int imm) {
        this.memoryModel = memoryModel;
        this.error = "";
        this.rd = rd;
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
        if (!this.memoryModel.updatePc(immBytes)) {
            this.error = "pc = " + this.memoryModel.getPc() + immBytes + " is invalid.";
            return false;
        }
        return true;
    }
}
