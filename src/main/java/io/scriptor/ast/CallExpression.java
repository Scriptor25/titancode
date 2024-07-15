package io.scriptor.ast;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.FunctionValue;
import io.scriptor.runtime.Type;
import io.scriptor.runtime.Value;

public class CallExpression extends Expression {

    public final FunctionValue callee;
    public final Expression[] args;

    public CallExpression(
            final SourceLocation location,
            final Expression callee,
            final Expression[] args) {
        super(location);

        assert callee != null;
        assert args != null;

        final var name = ((IDExpression) callee).name;
        this.callee = Environment.getFunction(location, name, args.length);

        this.args = args;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        builder.append(callee.getValue().name()).append('(');
        for (int i = 0; i < args.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(args[i]);
        }
        builder.append(')');
        return builder.toString();
    }

    @Override
    public Type getType() {
        return null; // TODO
    }

    @Override
    public Value evaluate(final Environment env) {
        assert env != null;

        final var args = new Value[this.args.length];
        for (int i = 0; i < args.length; ++i)
            args[i] = this.args[i].evaluate(env);

        return callee
                .getValue()
                .call(env.getGlobal(), args);
    }
}
