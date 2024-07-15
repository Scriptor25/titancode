package io.scriptor.ast;

import io.scriptor.parser.SourceLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class IDExpression extends Expression {

    public final String name;

    public IDExpression(final SourceLocation location, final String name) {
        super(location);

        assert name != null;

        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;
        return env.getVariable(location, name).value;
    }
}
