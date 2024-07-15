package io.scriptor.parser;

import io.scriptor.SourceLocation;

public record Token(SourceLocation location, TokenType type, String value) {

    @Override
    public String toString() {
        return String.format("%s: '%s' (%s)", location, value, type);
    }
}
