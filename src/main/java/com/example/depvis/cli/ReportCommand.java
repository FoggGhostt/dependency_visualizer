package com.example.depvis.cli;

import com.example.depvis.export.HtmlReporter;
import com.example.depvis.export.JsonExporter;
import com.example.depvis.model.GraphSnapshot;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
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
    public Integer call() throws IOException {
        GraphSnapshot graph = new JsonExporter().readGraph(graphJson);
        new HtmlReporter().writeReport(graph, outFile);
        System.out.println("[report] wrote " + outFile);
        return 0;
    }
}
