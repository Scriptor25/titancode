package io.scriptor.ast;

import io.scriptor.Name;
import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Type;
import io.scriptor.runtime.Value;

public class IDExpression extends Expression {

    public final Name name;

    public IDExpression(final SourceLocation location, final Name name) {
        super(location);
        assert name != null;
        this.name = name;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public Type getType() {
        return null; // TODO
    }

    @Override
    public Value evaluate(final Environment env) {
        assert env != null;
        return env.getVariable(location, name).value;
    }
}
