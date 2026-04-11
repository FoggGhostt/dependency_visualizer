package com.example.depvis.cli;

import com.example.depvis.analysis.CycleDetector;
import com.example.depvis.export.JsonExporter;
import com.example.depvis.model.GraphSnapshot;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "cycles",
        mixinStandardHelpOptions = true,
        description = "Read graph.json and print dependency cycles."
)
public class CyclesCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Path to graph.json produced by analyze.")
    Path graphJson;

    @Override
    public Integer call() throws IOException {
        GraphSnapshot graph = new JsonExporter().readGraph(graphJson);
        List<List<String>> cycles = CycleDetector.findCycles(graph);

        if (cycles.isEmpty()) {
            System.out.println("No cycles found.");
            return 0;
        }

        System.out.println("Found " + cycles.size() + " cycle(s):");
        for (int i = 0; i < cycles.size(); i++) {
            List<String> c = cycles.get(i);
            StringBuilder sb = new StringBuilder();
            sb.append(i + 1).append(") ");
            for (int j = 0; j < c.size(); j++) {
                sb.append(displayName(c.get(j))).append(" -> ");
            }
            sb.append(displayName(c.get(0)));
            System.out.println(sb);
        }
        return 0;
    }

    private static String displayName(String id) {
        int colon = id.indexOf(':');
        if (colon >= 0) {
            return id.substring(colon + 1);
        }
        return id;
    }
}
