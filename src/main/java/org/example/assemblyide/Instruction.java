package org.example.assemblyide;

public interface Instruction {
    boolean execute();
    String getError();
    String getInstructionInfo();
}
