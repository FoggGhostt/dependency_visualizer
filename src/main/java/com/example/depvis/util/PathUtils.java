package com.example.depvis.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;

public final class PathUtils {

    private PathUtils() {
    }

    public static List<PathMatcher> compileGlobs(String[] patterns) {
        List<PathMatcher> result = new ArrayList<>();
        if (patterns == null) {
            return result;
        }
        for (String p : patterns) {
            if (p == null || p.isBlank()) {
                continue;
            }
            result.add(FileSystems.getDefault().getPathMatcher("glob:" + p));
        }
        return result;
    }

    public static boolean matchesAny(Path path, List<PathMatcher> matchers) {
        for (PathMatcher m : matchers) {
            if (m.matches(path)) {
                return true;
            }
        }
        return false;
    }
}
