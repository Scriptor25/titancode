package io.scriptor.ast;

import io.scriptor.Name;
import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.Function;
import io.scriptor.runtime.IFunction;
import io.scriptor.runtime.NativeFunction;
import io.scriptor.runtime.Value;

public class DefFunctionExpression extends Expression {

    private final String nativeName;
    private final Name name;
    private final String[] argNames;
    private final boolean hasVarArgs;
    private final Expression body;

    private final IFunction function;

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
        assert nativeName == null || body == null;

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
    public boolean isConstant() {
        return true;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder()
                .append("def ");
        if (nativeName != null)
            builder.append("native(\"")
                    .append(nativeName)
                    .append("\") ");
        builder
                .append(name)
                .append('(');

        for (int i = 0; i < argNames.length; ++i) {
            if (i > 0)
                builder.append(", ");
            builder.append(argNames[i]);
        }

        if (hasVarArgs)
            if (argNames.length == 0)
                builder.append('?');
            else
                builder.append(", ?");

        if (body == null)
            return builder
                    .append(')')
                    .toString();

        return builder
                .append(") = ")
                .append(body)
                .toString();
    }

    @Override
    public Value evaluate(final Environment env) {
        return Environment.defineFunction(function);
    }
}
