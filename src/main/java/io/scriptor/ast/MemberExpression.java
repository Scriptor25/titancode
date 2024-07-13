package io.scriptor.ast;

import io.scriptor.parser.RLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class MemberExpression extends Expression {

    public final Expression object;
    public final String member;

    public MemberExpression(final RLocation location, final Expression object, final String member) {
        super(location);

        assert object != null;
        assert member != null;

        this.object = object;
        this.member = member;
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        final var obj = object.evaluate(env);
        return obj.getField(member);
    }
}
