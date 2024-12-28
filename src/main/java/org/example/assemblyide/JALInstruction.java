package org.example.assemblyide;

public class JALInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String name;
    private int rd;
    private int imm;

    public JALInstruction(MemoryModel memoryModel, String name, int rd, int imm) {
        this.memoryModel = memoryModel;
        this.name = name;
        this.rd = rd;
        this.imm = imm;
    }

    @Override
    public boolean execute() {
        this.memoryModel.updateRegister(this.rd, this.memoryModel.getPc() + 4);
        int immBytes = this.imm << 1;
        this.memoryModel.updatePc(immBytes);
    }
}
