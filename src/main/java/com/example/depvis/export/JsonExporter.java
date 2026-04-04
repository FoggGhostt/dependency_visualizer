package com.example.depvis.export;

import com.example.depvis.model.GraphSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonExporter {

    private final ObjectMapper mapper;

    public JsonExporter() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void writeGraph(GraphSnapshot graph, Path outFile) throws IOException {
        Path parent = outFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        mapper.writeValue(outFile.toFile(), graph);
    }

    public GraphSnapshot readGraph(Path inFile) throws IOException {
        return mapper.readValue(inFile.toFile(), GraphSnapshot.class);
    }
}
