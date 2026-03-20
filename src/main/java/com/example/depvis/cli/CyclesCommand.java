package com.example.depvis.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
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
    public Integer call() {
        System.out.println("[cycles] not implemented yet");
        System.out.println("  graphJson = " + graphJson);
        // TODO: stage 6 — load graph, run CycleDetector, print cycles
        return 0;
    }
}
