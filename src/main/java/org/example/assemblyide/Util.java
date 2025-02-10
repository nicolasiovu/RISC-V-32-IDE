package org.example.assemblyide;

import java.util.HashMap;

public class Util {
    public static final HashMap<String, String> registerMap = new HashMap<>();
    public static final HashMap<String, String> reverseRegisterMap = new HashMap<>();
    
    static {
        registerMap.put("zero", "x0");
        registerMap.put("ra", "x1");
        registerMap.put("sp", "x2");
        registerMap.put("gp", "x3");
        registerMap.put("tp", "x4");
        registerMap.put("t0", "x5");
        registerMap.put("t1", "x6");
        registerMap.put("t2", "x7");
        registerMap.put("s0", "x8");
        registerMap.put("s1", "x9");
        registerMap.put("a0", "x10");
        registerMap.put("a1", "x11");
        registerMap.put("a2", "x12");
        registerMap.put("a3", "x13");
        registerMap.put("a4", "x14");
        registerMap.put("a5", "x15");
        registerMap.put("a6", "x16");
        registerMap.put("a7", "x17");
        registerMap.put("s2", "x18");
        registerMap.put("s3", "x19");
        registerMap.put("s4", "x20");
        registerMap.put("s5", "x21");
        registerMap.put("s6", "x22");
        registerMap.put("s7", "x23");
        registerMap.put("s8", "x24");
        registerMap.put("s9", "x25");
        registerMap.put("s10", "x26");
        registerMap.put("s11", "x27");
        registerMap.put("t3", "x28");
        registerMap.put("t4", "x29");
        registerMap.put("t5", "x30");
        registerMap.put("t6", "x31");

        for (String key: registerMap.keySet()) {
            reverseRegisterMap.put(registerMap.get(key), key);
        }
    }


}
