package io.arrogantprogrammer.quarkusinsights.cfp.domain;

public enum Language {

    DUTCH("Dutch"),
    ENGLISH("English");

    public String displayValue;

    private Language(String displayValue) {
        this.displayValue = displayValue;
    }
}
