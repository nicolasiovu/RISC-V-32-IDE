package org.example.assemblyide;

import java.util.HashMap;

public class MemoryModel {

    private int pc;
    private HashMap<Integer, Integer> registers;

    public MemoryModel() {
        this.pc = 0;
        this.registers = new HashMap<>();
        for (int i=0; i < 32; i++) {
            this.registers.put(i, 0);
        }
    }
}
