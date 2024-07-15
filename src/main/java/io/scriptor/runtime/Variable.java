package io.scriptor.runtime;

import io.scriptor.Name;

public class Variable {

    public final Name name;
    public Value value;

    public Variable(final Name name, final Value value) {
        assert name != null;
        assert value != null;
        this.name = name;
        this.value = value;
    }
}
