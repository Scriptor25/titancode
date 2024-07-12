package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.IValue;

public class VarargsExpression extends Expression {

    public final Expression index;

    public VarargsExpression(final Expression index) {
        this.index = index;
    }

    @Override
    public IValue evaluate(final Env env) {
        assert env != null;

        if (index == null)
            return env.getAllVarargs();
        return env.getVararg(index.evaluate(env));
    }

}
