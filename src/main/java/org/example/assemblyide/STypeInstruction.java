package org.example.assemblyide;

public class STypeInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String name;
    private String error;
    private int rs1;
    private int rs2;
    private int imm;

    public STypeInstruction(MemoryModel memoryModel, String name, int rs1, int rs2, int imm) {
        this.memoryModel = memoryModel;
        this.name = name;
        this.error = "";
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.imm = imm;
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
        int rs2Val = this.memoryModel.getRegisterValue(this.rs2);
        try {
            switch (this.name) {
                case "sb":
                    this.memoryModel.writeByte(address, (byte) (rs2Val & 0xFF));
                    break;
                case "sh":
                    short value = (short) (rs2Val & 0xFFFF);
                    this.memoryModel.writeByte(address, (byte) (value & 0xFF));
                    this.memoryModel.writeByte(address + 1, (byte) ((value >> 8) & 0xFF));
                    break;
                case "sw":
                    this.memoryModel.writeWord(address, rs2Val);
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
}
