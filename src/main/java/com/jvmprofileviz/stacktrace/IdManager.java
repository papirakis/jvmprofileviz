/**
 * JvmProfile - java monitoring from inside Docker containers and more!
 *
 * Copyright (C) 2019 by Emmanuel Papirakis. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
