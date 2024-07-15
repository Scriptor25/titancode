package io.scriptor.ast;

import io.scriptor.parser.SourceLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Function;
import io.scriptor.runtime.IFunction;
import io.scriptor.runtime.NativeFunction;
import io.scriptor.runtime.Value;

public class DefFunctionExpression extends Expression {

    public final String name;
    public final String nativeName;
    public final String[] argNames;
    public final boolean hasVarArgs;
    public final Expression body;

    public final IFunction function;

    public DefFunctionExpression(
            final SourceLocation location,
            final String nativeName,
            final String name,
            final String[] argNames,
            final boolean hasVarArgs,
            final Expression body) {
        super(location);

        assert name != null;
        assert argNames != null;
        assert nativeName != null || body != null;

        this.name = name;
        this.nativeName = nativeName;
        this.argNames = argNames;
        this.hasVarArgs = hasVarArgs;
        this.body = body;

        this.function = nativeName == null
                ? new Function(location, name, argNames, hasVarArgs, body)
                : new NativeFunction(location, nativeName, name, argNames.length, hasVarArgs);
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder()
                .append("def ")
                .append(name)
                .append('(');

        for (int i = 0; i < argNames.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(argNames[i]);
        }

        return builder
                .append(") = ")
                .append(body)
                .toString();
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;

        env.defineFunction(function);
        return null;
    }
}
