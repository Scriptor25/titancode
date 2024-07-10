package io.scriptor;

public class IDExpression extends Expression {

    public final String name;

    public IDExpression(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Value evaluate(final Env env) {
        return env.getVariable(name);
    }
}
