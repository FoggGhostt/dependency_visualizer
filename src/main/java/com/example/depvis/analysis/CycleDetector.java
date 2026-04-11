package com.example.depvis.analysis;

import com.example.depvis.model.Edge;
import com.example.depvis.model.GraphSnapshot;
import com.example.depvis.model.Node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CycleDetector {

    private static final int WHITE = 0;
    private static final int GRAY = 1;
    private static final int BLACK = 2;

    public static List<List<String>> findCycles(GraphSnapshot graph) {
        Map<String, List<String>> adj = new HashMap<>();
        for (Node n : graph.nodes()) {
            adj.put(n.id(), new ArrayList<>());
        }
        for (Edge e : graph.edges()) {
            List<String> targets = adj.get(e.fromId());
            if (targets != null) {
                targets.add(e.toId());
            }
        }

        Map<String, Integer> color = new HashMap<>();
        for (String id : adj.keySet()) {
            color.put(id, WHITE);
        }

        List<List<String>> cycles = new ArrayList<>();
        Set<String> seenKeys = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();

        for (String id : adj.keySet()) {
            if (color.get(id) == WHITE) {
                dfs(id, adj, color, stack, cycles, seenKeys);
            }
        }
        return cycles;
    }

    private static void dfs(
            String id,
            Map<String, List<String>> adj,
            Map<String, Integer> color,
            Deque<String> stack,
            List<List<String>> cycles,
            Set<String> seenKeys
    ) {
        color.put(id, GRAY);
        stack.push(id);

        List<String> neighbors = adj.get(id);
        if (neighbors != null) {
            for (String next : neighbors) {
                int c = color.getOrDefault(next, WHITE);
                if (c == WHITE) {
                    dfs(next, adj, color, stack, cycles, seenKeys);
                } else if (c == GRAY) {
                    List<String> cycle = extractCycle(stack, next);
                    String key = canonicalKey(cycle);
                    if (seenKeys.add(key)) {
                        cycles.add(cycle);
                    }
                }
            }
        }

        stack.pop();
        color.put(id, BLACK);
    }

    private static List<String> extractCycle(Deque<String> stack, String startId) {
        List<String> tail = new ArrayList<>();
        for (String s : stack) {
            tail.add(s);
            if (s.equals(startId)) {
                break;
            }
        }
        List<String> result = new ArrayList<>();
        for (int i = tail.size() - 1; i >= 0; i--) {
            result.add(tail.get(i));
        }
        return result;
    }

    private static String canonicalKey(List<String> cycle) {
        if (cycle.isEmpty()) {
            return "";
        }
        int minIdx = 0;
        for (int i = 1; i < cycle.size(); i++) {
            if (cycle.get(i).compareTo(cycle.get(minIdx)) < 0) {
                minIdx = i;
            }
        }
        StringBuilder sb = new StringBuilder();
        int n = cycle.size();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                sb.append("|");
            }
            sb.append(cycle.get((minIdx + i) % n));
        }
        return sb.toString();
    }
}
