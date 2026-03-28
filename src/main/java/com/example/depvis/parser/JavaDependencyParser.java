package com.example.depvis.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.Type;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class JavaDependencyParser {

    private static final Set<String> PRIMITIVES = Set.of(
            "void", "int", "long", "short", "byte", "double", "float", "boolean", "char"
    );

    private static final Set<String> JAVA_LANG_COMMON = Set.of(
            "String", "Integer", "Long", "Double", "Float", "Boolean", "Byte", "Short",
            "Character", "Object", "Void", "Number", "Math", "System", "Throwable",
            "Exception", "RuntimeException", "Class", "Enum", "Iterable", "Comparable",
            "Thread", "Runnable", "StringBuilder", "StringBuffer", "CharSequence", "Error"
    );

    public Optional<ParsedClass> parse(Path file) throws IOException {
        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parse(file);
        } catch (Exception e) {
            // TODO: log parse failures and continue gracefully
            return Optional.empty();
        }

        String packageName = cu.getPackageDeclaration()
                .map(pd -> pd.getNameAsString())
                .orElse("");

        // TODO: wildcard imports support
        Map<String, String> importMap = new HashMap<>();
        for (ImportDeclaration imp : cu.getImports()) {
            if (imp.isAsterisk() || imp.isStatic()) {
                continue;
            }
            String fqn = imp.getNameAsString();
            int dot = fqn.lastIndexOf('.');
            String simple = dot >= 0 ? fqn.substring(dot + 1) : fqn;
            importMap.put(simple, fqn);
        }

        Optional<ClassOrInterfaceDeclaration> clsOpt = cu.findFirst(ClassOrInterfaceDeclaration.class);
        if (clsOpt.isEmpty()) {
            return Optional.empty();
        }
        ClassOrInterfaceDeclaration cls = clsOpt.get();
        String simpleName = cls.getNameAsString();
        String fqn = packageName.isEmpty() ? simpleName : packageName + "." + simpleName;

        Set<String> deps = new TreeSet<>();

        cls.findAll(FieldDeclaration.class).forEach(fd ->
                addType(fd.getElementType(), packageName, importMap, deps));

        cls.findAll(MethodDeclaration.class).forEach(md -> {
            addType(md.getType(), packageName, importMap, deps);
            for (Parameter p : md.getParameters()) {
                addType(p.getType(), packageName, importMap, deps);
            }
        });

        cls.findAll(ConstructorDeclaration.class).forEach(cd -> {
            for (Parameter p : cd.getParameters()) {
                addType(p.getType(), packageName, importMap, deps);
            }
        });

        cls.findAll(ObjectCreationExpr.class).forEach(oce ->
                addTypeName(oce.getType().getNameAsString(), packageName, importMap, deps));

        deps.remove(fqn);

        // TODO: generics support (currently only raw types are tracked)
        // TODO: method calls
        // TODO: inheritance (extends/implements)

        return Optional.of(new ParsedClass(fqn, packageName, simpleName, deps));
    }

    private void addType(Type type, String currentPackage, Map<String, String> imports, Set<String> deps) {
        String s = type.asString();
        int lt = s.indexOf('<');
        if (lt >= 0) {
            s = s.substring(0, lt);
        }
        s = s.replace("[]", "").trim();
        if (s.isEmpty()) {
            return;
        }
        addTypeName(s, currentPackage, imports, deps);
    }

    private void addTypeName(String name, String currentPackage, Map<String, String> imports, Set<String> deps) {
        if (PRIMITIVES.contains(name) || JAVA_LANG_COMMON.contains(name)) {
            return;
        }
        if (name.startsWith("java.") || name.startsWith("javax.")) {
            return;
        }
        if (name.contains(".")) {
            deps.add(name);
            return;
        }
        if (imports.containsKey(name)) {
            String fqn = imports.get(name);
            if (!fqn.startsWith("java.") && !fqn.startsWith("javax.")) {
                deps.add(fqn);
            }
            return;
        }
        if (!currentPackage.isEmpty()) {
            deps.add(currentPackage + "." + name);
        }
    }
}
