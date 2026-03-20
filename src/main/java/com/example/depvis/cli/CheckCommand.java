package com.example.depvis.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
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
    public Integer call() {
        System.out.println("[check] not implemented yet");
        System.out.println("  graphJson  = " + graphJson);
        System.out.println("  configFile = " + configFile);
        // TODO: stage 8 — load config, run RuleChecker, exit 1 on violations
        return 0;
    }
}
