package org.example.assemblyide;

import java.util.HashMap;
import java.util.Observable;

public class MemoryModel extends Observable {

    private int pc;
    private HashMap<Integer, Integer> registers;

    public MemoryModel() {
        this.pc = 0;
        this.registers = new HashMap<>();
        for (int i=0; i < 32; i++) {
            this.registers.put(i, 0);
        }
    }

    public int getRegisterValue(int register) {
        return this.registers.get(register);
    }

    public void updateRegister(int register, int value) {
        this.registers.put(register, value);
        this.setChanged();
        this.notifyObservers(register);
    }
}
