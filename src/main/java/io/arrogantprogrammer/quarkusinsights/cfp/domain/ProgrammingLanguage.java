package io.arrogantprogrammer.quarkusinsights.cfp.domain;

import java.util.Map;
import java.util.Objects;

public record ProgrammingLanguage(String language) {

    private static final Map<String, String> COMMON_LANGUAGES = Map.ofEntries(
            Map.entry("java", "Java"),
            Map.entry("javascript", "JavaScript"),
            Map.entry("c#", "C#"),
            Map.entry("python", "Python"),
            Map.entry("go", "Go"),
            Map.entry("rust", "Rust"),
            Map.entry("kotlin", "Kotlin"),
            Map.entry("typescript", "TypeScript"),
            Map.entry("scala", "Scala"),
            Map.entry("groovy", "Groovy"),
            Map.entry("clojure", "Clojure"),
            Map.entry("ceylon", "Ceylon"),
            Map.entry("jruby", "JRuby"),
            Map.entry("jython", "Jython")
    );

    public ProgrammingLanguage {
        Objects.requireNonNull(language, "Language cannot be null");
        String trimmed = language.trim();
        if (trimmed.isBlank()) {
            throw new IllegalArgumentException("Language cannot be blank");
        }

        String normalized = COMMON_LANGUAGES.get(trimmed.toLowerCase());
        if (normalized != null) {
            language = normalized;
        } else {
            // Basic normalization for unknown languages: Capitalize first letter
            language = trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
        }
    }

    @Override
    public String toString() {
        return language;
    }
}
