package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.IValue;

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
    public IValue evaluate(final Env env) {
        return env.getVariable(name).value;
    }
}
