package io.scriptor;

public class CallExpression extends Expression {

    public final Expression callee;
    public final Expression[] args;

    public CallExpression(final Expression callee, final Expression[] args) {
        this.callee = callee;
        this.args = args;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        builder.append(callee).append('(');
        for (int i = 0; i < args.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(args[i]);
        }
        builder.append(')');
        return builder.toString();
    }

    @Override
    public Value evaluate(final Env env) {
        throw new UnsupportedOperationException();
    }
}
