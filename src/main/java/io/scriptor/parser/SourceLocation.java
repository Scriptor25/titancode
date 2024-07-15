package io.scriptor.parser;

import java.io.File;

public record SourceLocation(File file, int row, int column) {

    public static final SourceLocation UNKNOWN = new SourceLocation(null, 0, 0);

    @Override
    public String toString() {
        return String.format("%s(%d,%d)", file, row, column);
    }
}
