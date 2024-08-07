package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.CharValue;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Value;

public class CharExpression extends Expression {

    public final char value;

    public CharExpression(final SourceLocation location, final String value) {
        super(location);
        assert value != null;
        assert value.length() == 1;
        this.value = value.charAt(0);
    }

    @Override
    public String toString() {
        return value >= 0x20 ? "'" + Character.toString(value) + "'" : "' '";
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public Value evaluate(final Environment env) {
        return new CharValue(location, value);
    }
}
