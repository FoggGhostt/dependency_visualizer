package com.example.depvis.export;

import com.example.depvis.analysis.CycleDetector;
import com.example.depvis.analysis.MetricsCalculator;
import com.example.depvis.model.Edge;
import com.example.depvis.model.GraphSnapshot;
import com.example.depvis.model.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class HtmlReporter {

    private static final String CSS =
            "body { font-family: sans-serif; margin: 20px; color: #222; }\n" +
            "h1 { border-bottom: 2px solid #555; padding-bottom: 4px; }\n" +
            "h2 { margin-top: 28px; }\n" +
            "table { border-collapse: collapse; margin-top: 8px; }\n" +
            "th, td { border: 1px solid #aaa; padding: 4px 10px; text-align: left; }\n" +
            "th { background: #eee; }\n" +
            "ul { margin-top: 5px; }\n" +
            ".meta { color: #666; font-size: 0.9em; }\n";

    public void writeReport(GraphSnapshot graph, Path outFile) throws IOException {
        Path parent = outFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<title>depvis report</title>\n");
        html.append("<style>\n").append(CSS).append("</style>\n");
        html.append("</head>\n<body>\n");

        html.append("<h1>depvis — Dependency Report</h1>\n");
        html.append("<p class=\"meta\">Project: ").append(escape(graph.rootPath())).append("</p>\n");
        html.append("<p class=\"meta\">Generated: ").append(escape(graph.createdAt())).append("</p>\n");

        appendPackages(html, graph);
        appendEdgesTable(html, graph, "Dependencies (package level)", Edge.KIND_PACKAGE_REF);
        appendEdgesTable(html, graph, "Dependencies (class level)", Edge.KIND_CLASS_REF);
        appendMetrics(html, graph);
        appendCycles(html, graph);

        // TODO: render an actual graph visualization (e.g. via Graphviz/D3)

        html.append("</body>\n</html>\n");

        Files.writeString(outFile, html.toString());
    }

    private void appendPackages(StringBuilder html, GraphSnapshot graph) {
        html.append("<h2>Packages</h2>\n<ul>\n");
        for (Node n : graph.nodes()) {
            if (Node.KIND_PACKAGE.equals(n.kind())) {
                html.append("<li>").append(escape(n.name())).append("</li>\n");
            }
        }
        html.append("</ul>\n");
    }

    private void appendEdgesTable(StringBuilder html, GraphSnapshot graph, String title, String kind) {
        html.append("<h2>").append(escape(title)).append("</h2>\n");
        html.append("<table>\n<tr><th>From</th><th>To</th></tr>\n");
        boolean any = false;
        for (Edge e : graph.edges()) {
            if (kind.equals(e.kind())) {
                html.append("<tr><td>").append(escape(stripPrefix(e.fromId())))
                        .append("</td><td>").append(escape(stripPrefix(e.toId())))
                        .append("</td></tr>\n");
                any = true;
            }
        }
        if (!any) {
            html.append("<tr><td colspan=\"2\">(none)</td></tr>\n");
        }
        html.append("</table>\n");
    }

    private void appendMetrics(StringBuilder html, GraphSnapshot graph) {
        Map<String, Integer> fanIn = MetricsCalculator.fanIn(graph);
        Map<String, Integer> fanOut = MetricsCalculator.fanOut(graph);

        html.append("<h2>Metrics (fan-in / fan-out)</h2>\n");
        html.append("<table>\n<tr><th>Node</th><th>Kind</th><th>Fan-in</th><th>Fan-out</th></tr>\n");
        for (Node n : graph.nodes()) {
            int in = fanIn.getOrDefault(n.id(), 0);
            int out = fanOut.getOrDefault(n.id(), 0);
            html.append("<tr><td>").append(escape(n.name())).append("</td>");
            html.append("<td>").append(escape(n.kind())).append("</td>");
            html.append("<td>").append(in).append("</td>");
            html.append("<td>").append(out).append("</td></tr>\n");
        }
        html.append("</table>\n");
    }

    private void appendCycles(StringBuilder html, GraphSnapshot graph) {
        List<List<String>> cycles = CycleDetector.findCycles(graph);
        html.append("<h2>Cycles</h2>\n");
        if (cycles.isEmpty()) {
            html.append("<p>No cycles found.</p>\n");
            return;
        }
        html.append("<ul>\n");
        for (List<String> cycle : cycles) {
            StringBuilder line = new StringBuilder();
            for (String id : cycle) {
                line.append(stripPrefix(id)).append(" -> ");
            }
            line.append(stripPrefix(cycle.get(0)));
            html.append("<li>").append(escape(line.toString())).append("</li>\n");
        }
        html.append("</ul>\n");
    }

    private static String stripPrefix(String id) {
        int colon = id.indexOf(':');
        if (colon >= 0) {
            return id.substring(colon + 1);
        }
        return id;
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
