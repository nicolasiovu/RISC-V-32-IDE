package org.example.assemblyide;

public class ECall implements Instruction {
    private MemoryModel memoryModel;
    private IOTerminal terminal;
    private String error;
    private int lineNum;

    public ECall(MemoryModel memoryModel, IOTerminal terminal, int lineNum) {
        this.memoryModel = memoryModel;
        this.terminal = terminal;
        this.error = "";
        this.lineNum = lineNum;
    }

    @Override
    public String getError() {
        return this.error;
    }

    @Override
    public boolean execute() {
        int a7 = this.memoryModel.getRegisterValue(17);
        switch (a7) {
            case 1:
                this.terminal.printInteger();
                break;
            case 4:
                this.terminal.printString();
                break;
            case 5, 8, 12:
                this.terminal.startReading();
                break;
            case 10:
                this.memoryModel.exit();
                break;
            case 11:
                this.terminal.printCharacter();
                break;
            case 30:
                int currentTime = (int) System.currentTimeMillis();
                this.memoryModel.updateRegister(10, currentTime);
                break;
            default:
                this.error = "Invalid syscall input: " + a7;
                return false;
        }
        this.memoryModel.updatePc(4);
        return true;
    }

    @Override
    public String getInstructionInfo() {
        return "Line " + this.lineNum + ": ecall " + this.memoryModel.getRegisterValue(17);
    }
}
