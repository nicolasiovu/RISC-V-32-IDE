package org.example.assemblyide;

public class UnaryInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String name;
    private String error;
    private int rd;
    private int rs;
    private int lineNum;
    private String line;

    public UnaryInstruction(MemoryModel memoryModel, String name, int rd, int rs, int lineNum, String line) {
        this.memoryModel = memoryModel;
        this.name = name;
        this.error = "";
        this.rd = rd;
        this.rs = rs;
        this.lineNum = lineNum;
        this.line = line;
    }

    @Override
    public String getError() {
        return this.error;
    }

    @Override
    public boolean execute() {
        int rsVal = this.memoryModel.getRegisterValue(this.rs);
        try {
            int rdVal = switch (this.name) {
                case "mv" -> rsVal;
                case "not" -> ~rsVal;
                case "neg" -> -rsVal;
                case "negw" -> (int) -(rsVal & 0xFFFFFFFFL);
                case "seqw" -> rsVal == 0 ? 1 : 0;
                case "snez" -> rsVal != 0 ? 1 : 0;
                case "sltz" -> rsVal < 0 ? 1 : 0;
                case "sgtz" -> rsVal > 0 ? 1 : 0;
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
