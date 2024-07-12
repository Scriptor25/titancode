package io.scriptor.ast;

import io.scriptor.runtime.CharValue;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class CharExpression extends Expression {

    public final char value;

    public CharExpression(final String value) {
        assert value != null;
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
    public Value evaluate(final Env env) {
        return new CharValue(value);
    }
}
