package org.example.assemblyide;

public class JALInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String error;
    private int rd;
    private int imm;
    private int lineNum;
    private String line;

    public JALInstruction(MemoryModel memoryModel, int rd, int imm, int lineNum, String line) {
        this.memoryModel = memoryModel;
        this.error = "";
        this.rd = rd;
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
        if (!this.memoryModel.updatePc(immBytes)) {
            this.error = "pc = " + this.memoryModel.getPc() + immBytes + " is invalid.";
            return false;
        }
        return true;
    }

    @Override
    public String getInstructionInfo() {
        return "Line " + this.lineNum + ": " + this.line.replaceFirst("^\\s+", "");
    }
}
