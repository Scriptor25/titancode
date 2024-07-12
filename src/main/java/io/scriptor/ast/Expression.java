package io.scriptor.ast;

import io.scriptor.parser.RLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public abstract class Expression {

    protected static int depth = 0;

    public final RLocation location;

    public Expression(final RLocation location) {
        assert location != null;
        this.location = location;
    }

    public boolean isConstant() {
        return false;
    }

    public abstract Value evaluate(final Env env);
}
