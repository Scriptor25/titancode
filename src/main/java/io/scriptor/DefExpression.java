package io.scriptor;

public class DefExpression extends Expression {

    public final String name;
    public final String[] args;
    public final Expression expression;

    public DefExpression(final String name, final String[] args, final Expression expression) {
        this.name = name;
        this.args = args;
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
    public Value evaluate(final Env env) {
        env.defineFunction(name, args, expression);
        return null;
    }
}
