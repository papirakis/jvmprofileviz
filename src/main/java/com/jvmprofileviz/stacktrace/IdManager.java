package com.jvmprofileviz.stacktrace;

import java.util.HashMap;
import java.util.Map;

public class IdManager {
    private final HashMap<String, Integer> methodToId = new HashMap<String, Integer>();

    private final HashMap<Integer, String> idToMethodName = new HashMap<Integer, String>();

    private int maxId = 0;

    public IdManager() { }

    public IdManager(Map<Integer, String> mappings) {
        for (Integer i : mappings.keySet()) {
            String methodName = mappings.get(i);
            idToMethodName.put(i, methodName);
            methodToId.put(methodName, i);
        }
    }

    public int getIdForMethodName(String methodName) {
        if (methodToId.containsKey(methodName)) {
            return methodToId.get(methodName);
        }

        int currentId = maxId++;

        methodToId.put(methodName, currentId);
        idToMethodName.put(currentId, methodName);
        return currentId;
    }

    public String getMethodNameById(int methodId) {
        if (!idToMethodName.containsKey(methodId)) {
            throw new IllegalArgumentException("No such method ID: " + methodId);
        }

        return idToMethodName.get(methodId);
    }

    public HashMap<Integer, String> getMapping() {
        return idToMethodName;
    }
}
