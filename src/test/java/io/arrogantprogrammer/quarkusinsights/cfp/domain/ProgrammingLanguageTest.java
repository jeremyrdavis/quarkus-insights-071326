package io.arrogantprogrammer.quarkusinsights.cfp.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ProgrammingLanguageTest {

    @Test
    void shouldCreateValidProgrammingLanguage() {
        ProgrammingLanguage java = new ProgrammingLanguage("Java");
        assertEquals("Java", java.language());
    }

    @Test
    void shouldNormalizeProgrammingLanguage() {
        assertEquals("Java", new ProgrammingLanguage(" java ").language());
        assertEquals("JavaScript", new ProgrammingLanguage("javascript").language());
        assertEquals("C#", new ProgrammingLanguage("c#").language());
        assertEquals("Python", new ProgrammingLanguage("PYTHON").language());
        assertEquals("Scala", new ProgrammingLanguage("scala").language());
        assertEquals("Groovy", new ProgrammingLanguage("GROOVY").language());
        assertEquals("Clojure", new ProgrammingLanguage("clojure").language());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void shouldThrowExceptionForBlankLanguage(String blankLanguage) {
        assertThrows(IllegalArgumentException.class, () -> new ProgrammingLanguage(blankLanguage));
    }

    @Test
    void shouldThrowExceptionForNullLanguage() {
        assertThrows(NullPointerException.class, () -> new ProgrammingLanguage(null));
    }

    @Test
    void shouldHandleUnknownLanguage() {
        // Assuming unknown languages are kept as is (but trimmed/normalized) if not in the common list?
        // Or should it only allow from a fixed list? 
        // The issue says "normalize against an internal list", usually implying it should match or be rejected, 
        // but "normalize" often means "if it looks like X, turn it into X".
        // Let's assume for now it allows other languages but fixes the casing for known ones.
        ProgrammingLanguage cobol = new ProgrammingLanguage(" cobol ");
        assertEquals("Cobol", cobol.language());
    }
}
