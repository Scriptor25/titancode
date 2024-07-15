package io.scriptor.ast;

import io.scriptor.parser.SourceLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.StringValue;
import io.scriptor.runtime.Value;

public class StringExpression extends Expression {

    public final String value;

    public StringExpression(final SourceLocation location, final String value) {
        super(location);

        assert value != null;

        this.value = value;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder().append('"');
        value.chars().filter(c -> c >= 0x20).forEach(c -> builder.append((char) c));
        return builder.append('"').toString();
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public Value evaluate(final Env env) {
        return new StringValue(location, value);
    }
}
