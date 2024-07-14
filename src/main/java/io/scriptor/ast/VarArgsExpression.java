package io.scriptor.ast;

import io.scriptor.parser.RLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class VarArgsExpression extends Expression {

    public VarArgsExpression(final RLocation location) {
        super(location);
    }

    @Override
    public String toString() {
        return "?";
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        return env.getVarArgs();
    }
}
