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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackTraceStatsBuilder {
    private final IdManager idManager;
    private final HashMap<List<Integer>, Integer> callStacks = new HashMap<List<Integer>, Integer>();

    public StackTraceStatsBuilder() {
        idManager = new IdManager();
    }

    public void addStackTrace(List<String> callStack) {
        ArrayList<Integer> intCallStack = new ArrayList<Integer>();

        for (String method : callStack) {
            int methodId = idManager.getIdForMethodName(method);
            intCallStack.add(methodId);
        }

        if (!callStacks.containsKey(intCallStack)) {
            callStacks.put(intCallStack, 1);
        } else {
            Integer curentCount = callStacks.get(intCallStack);
            curentCount++;
            callStacks.put(intCallStack, curentCount);
        }
    }

    public List<StackTraceInfo> getStackTraces() {
        ArrayList<StackTraceInfo> result = new ArrayList<StackTraceInfo>();

        for (Map.Entry<List<Integer>, Integer> entry : callStacks.entrySet()) {
            StackTraceInfo newEntry = new StackTraceInfo(entry.getKey(), entry.getValue());
            result.add(newEntry);
        }

        return result;
    }

    public StackTraceStats getStats() {
        StackTraceStats result = new StackTraceStats();

        result.methods = idManager.getMapping();
        result.data = getStackTraces();
        return result;
    }
}
