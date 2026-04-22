package com.example.depvis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigLoader {

    public DepvisConfig load(Path file) throws IOException {
        String name = file.getFileName().toString().toLowerCase();
        ObjectMapper mapper;
        if (name.endsWith(".yaml") || name.endsWith(".yml")) {
            mapper = new YAMLMapper();
        } else {
            mapper = new ObjectMapper();
        }
        return mapper.readValue(file.toFile(), DepvisConfig.class);
    }
}
