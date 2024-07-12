package io.scriptor.parser;

public record RLocation(String filename, int row, int column) {

    @Override
    public String toString() {
        return String.format("%s(%d,%d)", filename, row, column);
    }
}
