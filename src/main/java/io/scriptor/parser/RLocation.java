package io.scriptor.parser;

import java.io.File;

public record RLocation(File file, int row, int column) {

    @Override
    public String toString() {
        return String.format("%s(%d,%d)", file, row, column);
    }
}
