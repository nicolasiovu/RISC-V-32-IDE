package org.example.assemblyide;

public class RTypeInstruction implements Instruction {
    private MemoryModel memoryModel;

    private String name;
    private String error;
    private int rd;
    private int rs1;
    private int rs2;
    private int lineNum;
    private String line;

    public RTypeInstruction(MemoryModel memoryModel, String name, int rd, int rs1, int rs2, int lineNum, String line) {
        this.memoryModel = memoryModel;
        this.name = name;
        this.error = "";
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
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
        int rs2Val = this.memoryModel.getRegisterValue(this.rs2);
        try {
            int rdVal;
            switch (this.name) {
                case "add":
                    rdVal = rs1Val + rs2Val;
                    break;
                case "sub":
                    rdVal = rs1Val - rs2Val;
                    break;
                case "xor":
                    rdVal = rs1Val ^ rs2Val;
                    break;
                case "or":
                    rdVal = rs1Val | rs2Val;
                    break;
                case "and":
                    rdVal = rs1Val & rs2Val;
                    break;
                case "sll":
                    rdVal = rs1Val << rs2Val;
                    break;
                case "srl":
                    rdVal = rs1Val >>> rs2Val;
                    break;
                case "sra":
                    rdVal = rs1Val >> rs2Val;
                    break;
                case "slt":
                    rdVal = (rs1Val < rs2Val) ? 1 : 0;
                    break;
                case "sltu":
                    rdVal = (Integer.compareUnsigned(rs1Val, rs2Val) < 0) ? 1 : 0;
                    break;
                case "mul":
                    rdVal = rs1Val * rs2Val;
                    break;
                case "mulh":
                    rdVal = (int) (((long) rs1Val * (long) rs2Val) >> 32);
                    break;
                case "mulhsu":
                    long mulhsu = (long) rs1Val * Integer.toUnsignedLong(rs2Val);
                    rdVal = (int) (mulhsu >> 32);
                    break;
                case "mulhu":
                    long mulhu = Integer.toUnsignedLong(rs1Val) * Integer.toUnsignedLong(rs2Val);
                    rdVal = (int) (mulhu >> 32);
                    break;
                case "div":
                    if (rs2Val == 0) {
                        rdVal = -1;
                    } else {
                        rdVal = (int) (rs1Val / rs2Val);
                    }
                    break;
                case "divu":
                    if (rs2Val == 0) {
                        rdVal = -1;
                    } else {
                        rdVal = (int) (Integer.toUnsignedLong(rs1Val) / Integer.toUnsignedLong(rs2Val));
                    }
                    break;
                case "rem":
                    if (rs2Val == 0) {
                        rdVal = rs1Val;
                    } else {
                        rdVal = rs1Val % rs2Val;
                    }
                    break;
                case "remu":
                    if (rs2Val == 0) {
                        rdVal = rs1Val;
                    } else {
                        rdVal = (int) (Integer.toUnsignedLong(rs1Val) % Integer.toUnsignedLong(rs2Val));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown instruction");
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
