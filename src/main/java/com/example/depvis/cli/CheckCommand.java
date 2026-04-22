package com.example.depvis.cli;

import com.example.depvis.analysis.RuleChecker;
import com.example.depvis.config.ConfigLoader;
import com.example.depvis.config.DepvisConfig;
import com.example.depvis.export.JsonExporter;
import com.example.depvis.model.GraphSnapshot;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
        name = "check",
        mixinStandardHelpOptions = true,
        description = "Check architecture rules (forbidden dependencies) against graph.json."
)
public class CheckCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Path to graph.json produced by analyze.")
    Path graphJson;

    @Option(names = {"-c", "--config"}, description = "Path to config file (.json or .yaml).", required = true)
    Path configFile;

    @Override
    public Integer call() throws IOException {
        GraphSnapshot graph = new JsonExporter().readGraph(graphJson);
        DepvisConfig config = new ConfigLoader().load(configFile);

        List<RuleChecker.Violation> violations = RuleChecker.check(graph, config);

        if (violations.isEmpty()) {
            System.out.println("OK: no forbidden dependencies found.");
            return 0;
        }

        System.out.println("Found " + violations.size() + " violation(s):");
        for (int i = 0; i < violations.size(); i++) {
            RuleChecker.Violation v = violations.get(i);
            System.out.println((i + 1) + ") " + v.fromPackage() + " -> " + v.toPackage()
                    + "   (rule: " + v.ruleFrom() + " -> " + v.ruleTo() + ")");
        }
        return 1;
    }
}
