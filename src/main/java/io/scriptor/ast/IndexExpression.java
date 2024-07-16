package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Value;

public class IndexExpression extends Expression {

    private Expression object;
    private Expression index;

    public IndexExpression(final SourceLocation location, final Expression object, final Expression index) {
        super(location);

        assert object != null;
        assert index != null;

        this.object = object;
        this.index = index;
    }

    public Expression getObject() {
        return object;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", object, index);
    }

    @Override
    public boolean isConstant() {
        return object.isConstant() && index.isConstant();
    }

    @Override
    public Expression makeConstant() {
        object = object.makeConstant();
        index = index.makeConstant();
        return super.makeConstant();
    }

    @Override
    public Value evaluate(final Environment env) {
        final var eindex = index.evaluate(env);
        final var value = object.evaluate(env);

        final var i = eindex.getInt();
        return value.getAt(location, i);
    }
}
