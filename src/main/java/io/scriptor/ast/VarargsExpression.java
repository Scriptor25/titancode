package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class VarargsExpression extends Expression {

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        return env.getVarargs();
    }

}
