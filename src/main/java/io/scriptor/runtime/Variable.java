package io.scriptor.runtime;

public class Variable {

    public final String name;
    public IValue value;

    public Variable(final String name, final IValue value) {
        this.name = name;
        this.value = value;
    }
}
