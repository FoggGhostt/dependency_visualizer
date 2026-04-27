package com.example.depvis.analysis;

import com.example.depvis.model.Edge;
import com.example.depvis.model.GraphSnapshot;
import com.example.depvis.model.Node;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CycleDetectorTest {

    @Test
    void detectsSimpleThreeNodeCycle() {
        Node a = new Node("class:A", "A", Node.KIND_CLASS);
        Node b = new Node("class:B", "B", Node.KIND_CLASS);
        Node c = new Node("class:C", "C", Node.KIND_CLASS);
        Edge ab = new Edge("class:A", "class:B", Edge.KIND_CLASS_REF);
        Edge bc = new Edge("class:B", "class:C", Edge.KIND_CLASS_REF);
        Edge ca = new Edge("class:C", "class:A", Edge.KIND_CLASS_REF);
        GraphSnapshot graph = new GraphSnapshot("/test", "now",
                List.of(a, b, c), List.of(ab, bc, ca));

        List<List<String>> cycles = CycleDetector.findCycles(graph);

        assertEquals(1, cycles.size());
        assertEquals(3, cycles.get(0).size());
        assertTrue(cycles.get(0).contains("class:A"));
        assertTrue(cycles.get(0).contains("class:B"));
        assertTrue(cycles.get(0).contains("class:C"));
    }

    @Test
    void noCyclesInDag() {
        Node a = new Node("class:A", "A", Node.KIND_CLASS);
        Node b = new Node("class:B", "B", Node.KIND_CLASS);
        Edge ab = new Edge("class:A", "class:B", Edge.KIND_CLASS_REF);
        GraphSnapshot graph = new GraphSnapshot("/test", "now",
                List.of(a, b), List.of(ab));

        List<List<String>> cycles = CycleDetector.findCycles(graph);

        assertTrue(cycles.isEmpty());
    }
}
