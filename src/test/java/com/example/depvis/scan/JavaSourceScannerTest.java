package com.example.depvis.scan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaSourceScannerTest {

    @Test
    void findsJavaFilesAndIgnoresOthers(@TempDir Path root) throws IOException {
        Files.writeString(root.resolve("A.java"), "class A {}");
        Files.createDirectories(root.resolve("pkg"));
        Files.writeString(root.resolve("pkg/B.java"), "class B {}");
        Files.writeString(root.resolve("README.txt"), "hi");

        List<Path> found = new JavaSourceScanner().scan(root);

        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(p -> p.endsWith("A.java")));
        assertTrue(found.stream().anyMatch(p -> p.endsWith("B.java")));
    }

    @Test
    void appliesExcludeGlob(@TempDir Path root) throws IOException {
        Files.writeString(root.resolve("Keep.java"), "class Keep {}");
        Files.createDirectories(root.resolve("excluded"));
        Files.writeString(root.resolve("excluded/Skip.java"), "class Skip {}");

        JavaSourceScanner scanner = new JavaSourceScanner(
                new String[0],
                new String[]{"excluded/**"}
        );
        List<Path> found = scanner.scan(root);

        assertEquals(1, found.size());
        assertTrue(found.get(0).endsWith("Keep.java"));
    }
}
