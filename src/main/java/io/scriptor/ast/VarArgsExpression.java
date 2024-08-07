package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Value;

public class VarArgsExpression extends Expression {

    public VarArgsExpression(final SourceLocation location) {
        super(location);
    }

    @Override
    public String toString() {
        return "?";
    }

    @Override
    public Value evaluate(final Environment env) {
        assert env != null;
        return env.getVarArgs(location);
    }
}
