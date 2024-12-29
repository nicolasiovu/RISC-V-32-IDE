package org.example.assemblyide;

public class BTypeInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String name;
    private String error;
    private int rs1;
    private int rs2;
    private int imm;

    public BTypeInstruction(MemoryModel memoryModel, String name, int rs1, int rs2, int imm) {
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
        int rs2Val = this.memoryModel.getRegisterValue(this.rs2);
        int immBytes = this.imm << 1;
        try {
            switch (this.name) {
                case "beq":
                    if (rs1Val == rs2Val) {
                        if (!this.memoryModel.updatePc(immBytes)) {
                            this.error = "pc = " + this.memoryModel.getPc() + immBytes + " is invalid.";
                            return false;
                        }
                    } else {
                        this.memoryModel.updatePc(4);
                    }
                    break;
                case "bne":
                    if (rs1Val != rs2Val) {
                        if (!this.memoryModel.updatePc(immBytes)) {
                            this.error = "pc = " + this.memoryModel.getPc() + immBytes + " is invalid.";
                            return false;
                        }
                    } else {
                        this.memoryModel.updatePc(4);
                    }
                    break;
                case "blt":
                    if (rs1Val < rs2Val) {
                        if (!this.memoryModel.updatePc(immBytes)) {
                            this.error = "pc = " + this.memoryModel.getPc() + immBytes + " is invalid.";
                            return false;
                        }
                    } else {
                        this.memoryModel.updatePc(4);
                    }
                    break;
                case "bge":
                    if (rs1Val >= rs2Val) {
                        if (!this.memoryModel.updatePc(immBytes)) {
                            this.error = "pc = " + this.memoryModel.getPc() + immBytes + " is invalid.";
                            return false;
                        }
                    } else {
                        this.memoryModel.updatePc(4);
                    }
                    break;
                case "bltu":
                    if (Integer.compareUnsigned(rs1Val, rs2Val) < 0) {
                        if (!this.memoryModel.updatePc(immBytes)) {
                            this.error = "pc = " + this.memoryModel.getPc() + immBytes + " is invalid.";
                            return false;
                        }
                    } else {
                        this.memoryModel.updatePc(4);
                    }
                    break;
                case "bgeu":
                    if (Integer.compareUnsigned(rs1Val, rs2Val) >= 0) {
                        if (!this.memoryModel.updatePc(immBytes)) {
                            this.error = "pc = " + this.memoryModel.getPc() + immBytes + " is invalid.";
                            return false;
                        }
                    } else {
                        this.memoryModel.updatePc(4);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown instruction");
            }
        } catch (Exception e) {
            this.error = e.getMessage();
            return false;
        }
        return true;
    }
}
