package com.example.depvis.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaDependencyParserTest {

    @Test
    void extractsPackageImportsAndFieldAndParameterTypes(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("Foo.java");
        Files.writeString(file,
                "package com.example.test;\n" +
                "import com.example.other.Helper;\n" +
                "public class Foo {\n" +
                "    Helper h;\n" +
                "    Bar b;\n" +
                "    void use(Baz baz) {}\n" +
                "    Qux make() { return new Qux(); }\n" +
                "}\n");

        Optional<ParsedClass> result = new JavaDependencyParser().parse(file);

        assertTrue(result.isPresent());
        ParsedClass pc = result.get();
        assertEquals("com.example.test.Foo", pc.fqn());
        assertEquals("com.example.test", pc.packageName());

        assertTrue(pc.dependencies().contains("com.example.other.Helper"));
        assertTrue(pc.dependencies().contains("com.example.test.Bar"));
        assertTrue(pc.dependencies().contains("com.example.test.Baz"));
        assertTrue(pc.dependencies().contains("com.example.test.Qux"));
    }

    @Test
    void ignoresPrimitivesAndJavaLang(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("Bar.java");
        Files.writeString(file,
                "package com.example.test;\n" +
                "public class Bar {\n" +
                "    int n;\n" +
                "    String s;\n" +
                "}\n");

        Optional<ParsedClass> result = new JavaDependencyParser().parse(file);

        assertTrue(result.isPresent());
        assertFalse(result.get().dependencies().contains("int"));
        assertFalse(result.get().dependencies().contains("String"));
    }
}
