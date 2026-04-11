package com.example.depvis.analysis;

import com.example.depvis.model.Edge;
import com.example.depvis.model.GraphSnapshot;
import com.example.depvis.model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsCalculator {

    public static Map<String, Integer> fanIn(GraphSnapshot graph) {
        Map<String, Integer> result = new HashMap<>();
        for (Node n : graph.nodes()) {
            result.put(n.id(), 0);
        }
        for (Edge e : graph.edges()) {
            Integer cur = result.get(e.toId());
            if (cur != null) {
                result.put(e.toId(), cur + 1);
            }
        }
        return result;
    }

    public static Map<String, Integer> fanOut(GraphSnapshot graph) {
        Map<String, Integer> result = new HashMap<>();
        for (Node n : graph.nodes()) {
            result.put(n.id(), 0);
        }
        for (Edge e : graph.edges()) {
            Integer cur = result.get(e.fromId());
            if (cur != null) {
                result.put(e.fromId(), cur + 1);
            }
        }
        return result;
    }

    public static List<Node> topConnectedPackages(GraphSnapshot graph, int limit) {
        Map<String, Integer> in = fanIn(graph);
        Map<String, Integer> out = fanOut(graph);

        List<Node> packages = new ArrayList<>();
        for (Node n : graph.nodes()) {
            if (Node.KIND_PACKAGE.equals(n.kind())) {
                packages.add(n);
            }
        }

        packages.sort((a, b) -> {
            int sa = in.getOrDefault(a.id(), 0) + out.getOrDefault(a.id(), 0);
            int sb = in.getOrDefault(b.id(), 0) + out.getOrDefault(b.id(), 0);
            return Integer.compare(sb, sa);
        });

        List<Node> result = new ArrayList<>();
        int max = Math.min(limit, packages.size());
        for (int i = 0; i < max; i++) {
            result.add(packages.get(i));
        }
        return result;
    }
}
