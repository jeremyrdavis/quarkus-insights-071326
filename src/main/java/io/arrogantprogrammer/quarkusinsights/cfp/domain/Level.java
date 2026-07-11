package io.arrogantprogrammer.quarkusinsights.cfp.domain;

public enum Level {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced");

    public String displayValue;

    private Level(String displayValue) {
        this.displayValue = displayValue;
    }
}
