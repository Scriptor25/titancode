package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public abstract class Expression {

    public abstract Value evaluate(final Env env);
}
