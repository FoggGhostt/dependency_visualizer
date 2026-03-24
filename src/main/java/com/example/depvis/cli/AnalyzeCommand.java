package com.example.depvis.cli;

import com.example.depvis.scan.JavaSourceScanner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
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
    public Integer call() throws IOException {
        System.out.println("[analyze] scanning: " + projectPath);

        JavaSourceScanner scanner = new JavaSourceScanner(include, exclude);
        List<Path> files = scanner.scan(projectPath);

        System.out.println("[analyze] found " + files.size() + " Java files");
        for (Path f : files) {
            System.out.println("  " + f);
        }

        // TODO: stage 4+ — parse files, build graph, write graph.json to outDir
        System.out.println("[analyze] graph build is not implemented yet (out=" + outDir + ")");
        return 0;
    }
}
