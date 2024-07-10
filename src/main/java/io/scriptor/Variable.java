package io.scriptor;

public class Variable {

    public final String name;
    public Value value;

    public Variable(final String name, final Value value) {
        this.name = name;
        this.value = value;
    }
}
