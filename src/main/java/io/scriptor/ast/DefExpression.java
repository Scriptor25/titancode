package io.scriptor.ast;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.IValue;

public class DefExpression extends Expression {

    public final String name;
    public final String[] args;
    public final boolean varargs;
    public final Expression expression;

    public DefExpression(final String name, final String[] args, final boolean varargs, final Expression expression) {
        this.name = name;
        this.args = args;
        this.varargs = varargs;
        this.expression = expression;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        builder.append("def ").append(name);

        if (args != null) {
            builder.append('(');
            for (int i = 0; i < args.length; ++i) {
                if (i > 0)
                    builder.append(", ");
                builder.append(args[i]);
            }
            builder.append(')');
        }
        builder.append(" = ").append(expression);
        return builder.toString();
    }

    @Override
    public IValue evaluate(final Env env) {
        if (args != null)
            env.defineFunction(name, args, varargs, expression);
        else
            env.defineVariable(name, expression.evaluate(env));
        return null;
    }
}