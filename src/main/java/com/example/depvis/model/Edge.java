package com.example.depvis.model;

public record Edge(String fromId, String toId, String kind) {

    public static final String KIND_CLASS_REF = "CLASS_REF";
    public static final String KIND_PACKAGE_REF = "PACKAGE_REF";
}
