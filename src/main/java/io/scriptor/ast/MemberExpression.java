package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Value;

public class MemberExpression extends Expression {

    private Expression object;
    private final String member;

    public MemberExpression(final SourceLocation location, final Expression object, final String member) {
        super(location);

        assert object != null;
        assert member != null;

        this.object = object;
        this.member = member;
    }

    public Expression getObject() {
        return object;
    }

    public String getMember() {
        return member;
    }

    @Override
    public String toString() {
        return String.format("%s.%s", object, member);
    }

    @Override
    public boolean isConstant() {
        return object.isConstant();
    }

    @Override
    public Expression makeConstant() {
        object = object.makeConstant();
        return super.makeConstant();
    }

    @Override
    public Value evaluate(final Environment env) {
        final var obj = object.evaluate(env);
        return obj.getField(location, member);
    }
}
