package org.example.assemblyide;

public class BTypePseudoInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String name;
    private String error;
    private int rs;
    private int imm;
    private int lineNum;
    private String line;

    public BTypePseudoInstruction(MemoryModel memoryModel, String name, int rs, int imm, int lineNum, String line) {
        this.memoryModel = memoryModel;
        this.name = name;
        this.error = "";
        this.rs = rs;
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
        int rsVal = this.memoryModel.getRegisterValue(this.rs);
        int immBytes = this.imm << 1;
        try {
            switch (this.name) {
                case "beqz":
                    if (rsVal == 0) {
                        if (!this.memoryModel.updatePc(immBytes)) {
                            this.error = "pc = " + this.memoryModel.getPc() + immBytes + " is invalid.";
                            return false;
                        }
                    } else {
                        this.memoryModel.updatePc(4);
                    }
                    break;
                case "bnez":
                    if (rsVal != 0) {
                        if (!this.memoryModel.updatePc(immBytes)) {
                            this.error = "pc = " + this.memoryModel.getPc() + immBytes + " is invalid.";
                            return false;
                        }
                    } else {
                        this.memoryModel.updatePc(4);
                    }
                    break;
                case "blez":
                    if (rsVal <= 0) {
                        if (!this.memoryModel.updatePc(immBytes)) {
                            this.error = "pc = " + this.memoryModel.getPc() + immBytes + " is invalid.";
                            return false;
                        }
                    } else {
                        this.memoryModel.updatePc(4);
                    }
                    break;
                case "bgez":
                    if (rsVal >= 0) {
                        if (!this.memoryModel.updatePc(immBytes)) {
                            this.error = "pc = " + this.memoryModel.getPc() + immBytes + " is invalid.";
                            return false;
                        }
                    } else {
                        this.memoryModel.updatePc(4);
                    }
                    break;
                case "bltz":
                    if (rsVal < 0) {
                        if (!this.memoryModel.updatePc(immBytes)) {
                            this.error = "pc = " + this.memoryModel.getPc() + immBytes + " is invalid.";
                            return false;
                        }
                    } else {
                        this.memoryModel.updatePc(4);
                    }
                    break;
                case "bgtz":
                    if (rsVal > 0) {
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

    @Override
    public String getInstructionInfo() {
        return "Line " + this.lineNum + ": " + this.line.replaceFirst("^\\s+", "");
    }
}
