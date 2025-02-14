package org.example.assemblyide;

public class UTypeInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String name;
    private String error;
    private int rd;
    private int imm;
    private int lineNum;
    private String line;

    public UTypeInstruction(MemoryModel model, String name, int rd, int imm, int lineNum, String line) {
        this.memoryModel = model;
        this.name = name;
        this.rd = rd;
        this.imm = imm;
        this.error = "";
        this.lineNum = lineNum;
        this.line = line;
    }

    @Override
    public String getError() {
        return this.error;
    }

    @Override
    public boolean execute() {
        try {
            switch (this.name) {
                case "lui":
                    this.memoryModel.updateRegister(this.rd, imm << 12);
                    break;
                case "auipc":
                    int pc = this.memoryModel.getPc();
                    this.memoryModel.updateRegister(this.rd, pc + (imm << 12));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown instruction");
            }
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
