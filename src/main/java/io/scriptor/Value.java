package io.scriptor;

public abstract class Value {

    public final Type type;

    public Value(final Type type) {
        this.type = type;
    }
}
