package com.example.depvis.parser;

import java.util.Set;

public record ParsedClass(
        String fqn,
        String packageName,
        String simpleName,
        Set<String> dependencies
) {
}
