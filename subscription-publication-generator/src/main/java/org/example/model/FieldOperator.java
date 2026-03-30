package org.example.model;

public enum FieldOperator {
    EQUAL("="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<=");

    private final String symbol;

    FieldOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}