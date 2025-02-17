package org.example.assemblyide;

public class LIInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String error;
    private int rd;
    private int imm;
    private int lineNum;
    private String line;

    public LIInstruction(MemoryModel memoryModel, int rd, int imm, int lineNum, String line) {
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
        this.memoryModel.updateRegister(this.rd, this.imm);
        this.memoryModel.updatePc(4);
        return true;
    }

    @Override
    public String getInstructionInfo() {
        return "Line " + this.lineNum + ": " + this.line.replaceFirst("^\\s+", "");
    }
}
