package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.StringValue;
import io.scriptor.runtime.IValue;

public class StringExpression extends Expression {

    public final String value;

    public StringExpression(final String value) {
        assert value != null;

        this.value = value;
    }

    @Override
    public IValue evaluate(final Env env) {
        return new StringValue(value);
    }
}
