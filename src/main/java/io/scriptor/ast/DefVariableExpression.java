package io.scriptor.ast;

import java.util.Arrays;

import io.scriptor.Name;
import io.scriptor.SourceLocation;
import io.scriptor.runtime.ArrayValue;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.NumberValue;
import io.scriptor.runtime.Value;

public class DefVariableExpression extends Expression {

    private final String nativeName;
    private final Name name;
    private Expression size;
    private Expression init;

    public DefVariableExpression(
            final SourceLocation location,
            final String nativeName,
            final Name name,
            final Expression init) {
        super(location);

        assert name != null;
        assert nativeName == null || init == null;

        this.nativeName = nativeName;
        this.name = name;
        this.size = null;
        this.init = init;
    }

    public DefVariableExpression(
            final SourceLocation location,
            final String nativeName,
            final Name name,
            final Expression size,
            final Expression init) {
        super(location);

        assert name != null;
        assert size != null;
        assert nativeName == null || init == null;

        this.nativeName = nativeName;
        this.name = name;
        this.size = size;
        this.init = init;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder()
                .append("def ");

        if (nativeName != null)
            builder
                    .append("native(\"")
                    .append(nativeName)
                    .append("\") ");

        builder.append(name);

        if (size != null)
            builder
                    .append('[')
                    .append(size)
                    .append(']');

        if (init == null)
            return builder.toString();

        return builder
                .append(" = ")
                .append(init)
                .toString();
    }

    @Override
    public Expression makeConstant() {
        if (size != null)
            size = size.makeConstant();
        if (init != null)
            init = init.makeConstant();
        return super.makeConstant();
    }

    @Override
    public Value evaluate(final Environment env) {
        assert env != null;

        if (size != null) {
            final var n = size.evaluate(env).getInt();
            final var values = new Value[n];
            if (init == null)
                Arrays.fill(values, new NumberValue(location, 0));
            else
                for (int i = 0; i < n; ++i)
                    values[i] = init.evaluate(env);
            final var array = new ArrayValue(location, values);
            env.defineVariable(location, name, array);
            return array;
        }

        final var value = init == null ? new NumberValue(location, 0) : init.evaluate(env);
        env.defineVariable(location, name, value);
        return value;
    }
}
