package com.example.depvis.model;

import java.util.List;
import java.util.Map;

public record AnalysisResult(
        Map<String, Integer> fanIn,
        Map<String, Integer> fanOut,
        List<List<String>> cycles
) {
}
