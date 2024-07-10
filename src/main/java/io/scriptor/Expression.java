package io.scriptor;

public abstract class Expression {

    public abstract Value evaluate(final Env env);
}
