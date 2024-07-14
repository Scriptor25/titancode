package io.scriptor.runtime;

public class Variable {

    public final String name;
    public Value value;

    public Variable(final String name, final Value value) {
        assert name != null;
        assert value != null;
        this.name = name;
        this.value = value;
    }
}
