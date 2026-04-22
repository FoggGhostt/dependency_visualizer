package com.example.depvis.analysis;

import com.example.depvis.config.DepvisConfig;
import com.example.depvis.model.Edge;
import com.example.depvis.model.GraphSnapshot;
import com.example.depvis.model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleChecker {

    public record Violation(String fromPackage, String toPackage, String ruleFrom, String ruleTo) {
    }

    public static List<Violation> check(GraphSnapshot graph, DepvisConfig config) {
        List<Violation> violations = new ArrayList<>();
        if (config == null || config.forbidden == null || config.forbidden.isEmpty()) {
            return violations;
        }

        Map<String, String> idToName = new HashMap<>();
        for (Node n : graph.nodes()) {
            if (Node.KIND_PACKAGE.equals(n.kind())) {
                idToName.put(n.id(), n.name());
            }
        }

        for (Edge e : graph.edges()) {
            if (!Edge.KIND_PACKAGE_REF.equals(e.kind())) {
                continue;
            }
            String fromPkg = idToName.get(e.fromId());
            String toPkg = idToName.get(e.toId());
            if (fromPkg == null || toPkg == null) {
                continue;
            }
            for (DepvisConfig.ForbiddenRule rule : config.forbidden) {
                if (rule == null || rule.from == null || rule.to == null) {
                    continue;
                }
                if (matches(fromPkg, rule.from) && matches(toPkg, rule.to)) {
                    violations.add(new Violation(fromPkg, toPkg, rule.from, rule.to));
                }
            }
        }
        return violations;
    }

    private static boolean matches(String pkg, String prefix) {
        return pkg.equals(prefix) || pkg.startsWith(prefix + ".");
    }
}
