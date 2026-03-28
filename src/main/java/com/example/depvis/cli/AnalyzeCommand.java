package com.example.depvis.cli;

import com.example.depvis.parser.JavaDependencyParser;
import com.example.depvis.parser.ParsedClass;
import com.example.depvis.scan.JavaSourceScanner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

        JavaDependencyParser parser = new JavaDependencyParser();
        List<ParsedClass> parsed = new ArrayList<>();
        for (Path f : files) {
            Optional<ParsedClass> pc = parser.parse(f);
            pc.ifPresent(parsed::add);
        }
        System.out.println("[analyze] parsed " + parsed.size() + " classes");
        for (ParsedClass pc : parsed) {
            System.out.println("  " + pc.fqn() + " -> " + pc.dependencies());
        }

        // TODO: stage 5 — build graph and write graph.json to outDir
        System.out.println("[analyze] graph build is not implemented yet (out=" + outDir + ")");
        return 0;
    }
}
