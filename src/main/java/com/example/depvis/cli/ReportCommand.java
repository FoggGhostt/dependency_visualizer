package com.example.depvis.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(
        name = "report",
        mixinStandardHelpOptions = true,
        description = "Generate a simple HTML report from graph.json."
)
public class ReportCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Path to graph.json produced by analyze.")
    Path graphJson;

    @Option(names = {"-o", "--out"}, description = "Output HTML file.", defaultValue = "out/report.html")
    Path outFile;

    @Override
    public Integer call() {
        System.out.println("[report] not implemented yet");
        System.out.println("  graphJson = " + graphJson);
        System.out.println("  outFile   = " + outFile);
        // TODO: stage 7 — render HTML report
        return 0;
    }
}
