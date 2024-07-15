package io.scriptor.ast;

import io.scriptor.Name;
import io.scriptor.SourceLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Function;
import io.scriptor.runtime.IFunction;
import io.scriptor.runtime.NativeFunction;
import io.scriptor.runtime.Type;
import io.scriptor.runtime.Value;

public class DefFunctionExpression extends Expression {

    public final String nativeName;
    public final Name name;
    public final String[] argNames;
    public final boolean hasVarArgs;
    public final Expression body;

    public final IFunction function;

    public DefFunctionExpression(
            final SourceLocation location,
            final String nativeName,
            final Name name,
            final String[] argNames,
            final boolean hasVarArgs,
            final Expression body) {
        super(location);

        assert name != null;
        assert argNames != null;

        this.nativeName = nativeName;
        this.name = name;
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
    public Type getType() {
        return Type.getFunction(location);
    }

    @Override
    public Value evaluate(final Env env) {
        return Env.defineFunction(function);
    }
}
