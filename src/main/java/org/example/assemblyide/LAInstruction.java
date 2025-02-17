package org.example.assemblyide;

public class LAInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String error;
    private int rd;
    private int address;
    private int lineNum;
    private String line;

    public LAInstruction(MemoryModel memoryModel, int rd, int address, int lineNum, String line) {
        this.memoryModel = memoryModel;
        this.error = "";
        this.rd = rd;
        this.address = address;
        this.lineNum = lineNum;
        this.line = line;
    }

    @Override
    public String getError() {
        return this.error;
    }

    @Override
    public boolean execute() {
        this.memoryModel.updateRegister(this.rd, this.address);
        this.memoryModel.updatePc(4);
        return true;
    }

    @Override
    public String getInstructionInfo() {
        return "Line " + this.lineNum + ": " + this.line.replaceFirst("^\\s+", "");
    }
}
