package com.example.depvis.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(
        name = "analyze",
        mixinStandardHelpOptions = true,
        description = "Scan a Java project and build a dependency graph (graph.json)."
)
public class AnalyzeCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Path to the Java project to analyze.")
    Path projectPath;

    @Option(names = {"-o", "--out"}, description = "Output directory for graph.json.", defaultValue = "out")
    Path outDir;

    @Option(names = "--include", description = "Glob include patterns (repeatable).", arity = "0..*")
    String[] include = new String[0];

    @Option(names = "--exclude", description = "Glob exclude patterns (repeatable).", arity = "0..*")
    String[] exclude = new String[0];

    @Override
    public Integer call() {
        System.out.println("[analyze] not implemented yet");
        System.out.println("  projectPath = " + projectPath);
        System.out.println("  outDir      = " + outDir);
        System.out.println("  include     = " + String.join(",", include));
        System.out.println("  exclude     = " + String.join(",", exclude));
        // TODO: stage 3+ — scan, parse, build graph, save graph.json
        return 0;
    }
}
