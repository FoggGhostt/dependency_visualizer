package com.example.depvis.model;

import com.example.depvis.parser.ParsedClass;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class GraphBuilder {

    private GraphBuilder() {
    }

    public static GraphSnapshot build(Path rootPath, List<ParsedClass> parsedClasses) {
        Set<String> knownFqns = new HashSet<>();
        Map<String, String> fqnToPackage = new HashMap<>();
        for (ParsedClass pc : parsedClasses) {
            knownFqns.add(pc.fqn());
            fqnToPackage.put(pc.fqn(), pc.packageName());
        }

        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Set<String> nodeIds = new HashSet<>();
        Set<String> edgeKeys = new HashSet<>();

        for (ParsedClass pc : parsedClasses) {
            String id = Node.classId(pc.fqn());
            if (nodeIds.add(id)) {
                nodes.add(new Node(id, pc.fqn(), Node.KIND_CLASS));
            }
        }

        Set<String> packages = new TreeSet<>();
        for (ParsedClass pc : parsedClasses) {
            if (!pc.packageName().isEmpty()) {
                packages.add(pc.packageName());
            }
        }
        for (String pkg : packages) {
            String id = Node.packageId(pkg);
            if (nodeIds.add(id)) {
                nodes.add(new Node(id, pkg, Node.KIND_PACKAGE));
            }
        }

        for (ParsedClass pc : parsedClasses) {
            for (String dep : pc.dependencies()) {
                if (!knownFqns.contains(dep)) {
                    continue;
                }
                String from = Node.classId(pc.fqn());
                String to = Node.classId(dep);
                String key = "C|" + from + "|" + to;
                if (edgeKeys.add(key)) {
                    edges.add(new Edge(from, to, Edge.KIND_CLASS_REF));
                }
            }
        }

        for (ParsedClass pc : parsedClasses) {
            String fromPkg = pc.packageName();
            if (fromPkg.isEmpty()) {
                continue;
            }
            for (String dep : pc.dependencies()) {
                if (!knownFqns.contains(dep)) {
                    continue;
                }
                String toPkg = fqnToPackage.get(dep);
                if (toPkg == null || toPkg.isEmpty() || toPkg.equals(fromPkg)) {
                    continue;
                }
                String from = Node.packageId(fromPkg);
                String to = Node.packageId(toPkg);
                String key = "P|" + from + "|" + to;
                if (edgeKeys.add(key)) {
                    edges.add(new Edge(from, to, Edge.KIND_PACKAGE_REF));
                }
            }
        }

        return new GraphSnapshot(
                rootPath.toString(),
                Instant.now().toString(),
                nodes,
                edges
        );
    }
}
