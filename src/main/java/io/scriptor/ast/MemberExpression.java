package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Type;
import io.scriptor.runtime.Value;

public class MemberExpression extends Expression {

    public final Expression object;
    public final String member;

    public MemberExpression(final SourceLocation location, final Expression object, final String member) {
        super(location);

        assert object != null;
        assert member != null;

        this.object = object;
        this.member = member;
    }

    @Override
    public String toString() {
        return String.format("%s.%s", object, member);
    }

    @Override
    public Type getType() {
        return null; // TODO
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        final var obj = object.evaluate(env);
        return obj.getField(location, member);
    }
}
