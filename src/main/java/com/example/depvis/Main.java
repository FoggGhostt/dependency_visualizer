package com.example.depvis;

import com.example.depvis.cli.DepvisCommand;
import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new DepvisCommand()).execute(args);
        System.exit(exitCode);
    }
}
