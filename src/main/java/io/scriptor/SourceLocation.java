package io.scriptor;

import java.io.File;

public record SourceLocation(File file, int row, int column) {

    public static final SourceLocation UNKNOWN = new SourceLocation(new File("unknown"), 0, 0);

    @Override
    public String toString() {
        return String.format("%s(%d,%d)", file, row, column);
    }
}
