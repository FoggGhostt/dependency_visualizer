package com.example.depvis.cli;

import picocli.CommandLine.Command;

@Command(
        name = "depvis",
        mixinStandardHelpOptions = true,
        version = "depvis 1.0.0-SNAPSHOT",
        description = "Dependency Visualizer for Java projects.",
        subcommands = {
                AnalyzeCommand.class,
                CyclesCommand.class,
                ReportCommand.class,
                CheckCommand.class
        }
)
public class DepvisCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("depvis: no subcommand given. Use --help to see available commands.");
    }
}
