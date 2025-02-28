package org.example.assemblyide;

public class LoadInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String name;
    private String error;
    private int rd;
    private int rs1;
    private int imm;
    private int lineNum;
    private String line;

    public LoadInstruction(MemoryModel memoryModel, String name, int rd, int rs1, int imm, int lineNum, String line) {
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
        int signedImm = (short) this.imm;
        int address = rs1Val + signedImm;
        try {
            int rdVal;
            switch (this.name) {
                case "lb":
                    rdVal = memoryModel.readByte(address);
                    break;
                case "lh":
                    byte lowByte = this.memoryModel.readByte(address);
                    byte highByte = this.memoryModel.readByte(address + 1);
                    rdVal = (short) ((highByte << 8) | (lowByte & 0xFF));
                    break;
                case "lw":
                    rdVal = this.memoryModel.readWord(address);
                    break;
                case "lbu":
                    rdVal = this.memoryModel.readByte(address) & 0xFF;
                    break;
                case "lhu":
                    byte lb = this.memoryModel.readByte(address);
                    byte hb = this.memoryModel.readByte(address + 1);
                    short hw = (short) ((hb << 8) | (lb & 0xFF));
                    rdVal = hw & 0xFFFF;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown instruction");
            }
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
