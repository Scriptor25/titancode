package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.StringValue;
import io.scriptor.runtime.Value;

public class StringExpression extends Expression {

    public final String value;

    public StringExpression(final String value) {
        assert value != null;

        this.value = value;
    }

    @Override
    public Value evaluate(final Env env) {
        return new StringValue(value);
    }
}
