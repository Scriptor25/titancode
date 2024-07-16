package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Value;

public abstract class Expression {

    protected static int depth = 0;

    protected static String getSpaces() {
        final var builder = new StringBuilder();
        for (int i = 0; i < depth; ++i)
            builder.append("  ");
        return builder.toString();
    }

    public final SourceLocation location;

    public Expression(final SourceLocation location) {
        assert location != null;
        this.location = location;
    }

    public boolean isConstant() {
        return false;
    }

    public Expression makeConstant() {
        return ConstExpression.makeConst(this);
    }

    public abstract Value evaluate(final Environment env);
}
