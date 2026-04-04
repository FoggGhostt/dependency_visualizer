package com.example.depvis.model;

public record Node(String id, String name, String kind) {

    public static final String KIND_CLASS = "CLASS";
    public static final String KIND_PACKAGE = "PACKAGE";

    public static String classId(String fqn) {
        return "class:" + fqn;
    }

    public static String packageId(String packageName) {
        return "pkg:" + packageName;
    }
}
