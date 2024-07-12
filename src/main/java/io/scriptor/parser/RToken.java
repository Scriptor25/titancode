package io.scriptor.parser;

public record RToken(RLocation location, TokenType type, String value) {

    @Override
    public String toString() {
        return String.format("%s: '%s' (%s)", location, value, type);
    }
}
