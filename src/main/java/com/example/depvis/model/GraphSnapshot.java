package com.example.depvis.model;

import java.util.List;

public record GraphSnapshot(
        String rootPath,
        String createdAt,
        List<Node> nodes,
        List<Edge> edges
) {
}
