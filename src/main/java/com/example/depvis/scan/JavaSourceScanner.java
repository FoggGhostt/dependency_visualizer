package com.example.depvis.scan;

import com.example.depvis.util.PathUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class JavaSourceScanner {

    private final List<PathMatcher> includes;
    private final List<PathMatcher> excludes;

    public JavaSourceScanner() {
        this(new String[0], new String[0]);
    }

    public JavaSourceScanner(String[] includeGlobs, String[] excludeGlobs) {
        this.includes = PathUtils.compileGlobs(includeGlobs);
        this.excludes = PathUtils.compileGlobs(excludeGlobs);
    }

    public List<Path> scan(Path root) throws IOException {
        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("Not a directory: " + root);
        }
        List<Path> result = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".java"))
                    .filter(p -> {
                        Path rel = root.relativize(p);
                        if (!includes.isEmpty() && !PathUtils.matchesAny(rel, includes)) {
                            return false;
                        }
                        return !PathUtils.matchesAny(rel, excludes);
                    })
                    .forEach(result::add);
        }
        Collections.sort(result);
        return result;
    }
}
