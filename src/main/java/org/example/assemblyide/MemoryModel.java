package org.example.assemblyide;

import java.util.HashMap;
import java.util.Observable;

public class MemoryModel extends Observable {
    private static final int PAGE_SIZE = 4096;
    private static final int PAGE_MASK = PAGE_SIZE - 1;

    private int pc;
    private HashMap<Integer, Integer> registers;
    private HashMap<Integer, byte[]> memory;
    private HashMap<String, Integer> labels;

    public MemoryModel() {
        this.pc = 0;
        this.registers = new HashMap<>();
        for (int i=0; i < 32; i++) {
            this.registers.put(i, 0);
        }
        this.memory = new HashMap<>();
        this.labels = new HashMap<>();
    }

    public int getRegisterValue(int register) {
        return this.registers.get(register);
    }

    public void updateRegister(int register, int value) {
        this.registers.put(register, value);
        this.setChanged();
        this.notifyObservers(register);
    }

    public int getPc() {
        return this.pc;
    }

    public void resetPc() {
        this.pc = 0;
        this.setChanged();
        this.notifyObservers("pc");
    }

    public boolean updatePc(int imm) {
        this.pc += imm;
        this.setChanged();
        this.notifyObservers("pc");
        return this.pc >= 0 && this.pc % 4 == 0;
    }

    public void addLabel(String label, int index) {
        this.labels.put(label, index);
    }

    public int lookupLabel(String label) {
        return this.labels.get(label);
    }

    public void resetLabels() {
        this.labels = new HashMap<>();
    }

    public byte readByte(int address) {
        int page = address / PAGE_SIZE;
        int offset = address & PAGE_MASK;

        byte[] pageData = this.memory.get(page);
        return (pageData != null) ? pageData[offset]: 0;
    }

    public void writeByte(int address, byte value) {
        int page = address / PAGE_SIZE;
        int offset = address & PAGE_MASK;

        byte[] pageData = this.memory.computeIfAbsent(page, k -> new byte[PAGE_SIZE]);
        pageData[offset] = value;
    }

    public int readWord(int address) {
        return (this.readByte(address) & 0xFF) |
                ((this.readByte(address + 1) & 0xFF) << 8) |
                ((this.readByte(address + 2) & 0xFF) << 16) |
                ((this.readByte(address + 3) & 0xFF) << 24);
    }

    public void writeWord(int address, int value) {
        this.writeByte(address, (byte) value);
        this.writeByte(address + 1, (byte) ((value >> 8) & 0xFF));
        this.writeByte(address + 2, (byte) ((value >> 16) & 0xFF));
        this.writeByte(address + 3, (byte) ((value >> 24) & 0xFF));
    }
}
