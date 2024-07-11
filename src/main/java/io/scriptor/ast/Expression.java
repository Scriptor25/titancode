package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.IValue;

public abstract class Expression {

    public abstract IValue evaluate(final Env env);
}
