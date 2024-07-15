package io.scriptor.runtime;

import io.scriptor.TitanException;
import io.scriptor.ast.Expression;
import io.scriptor.parser.SourceLocation;

public class Function implements IFunction {

    public final SourceLocation location;
    public final String name;
    public final String[] argNames;
    public final boolean hasVarArgs;
    public final Expression body;

    public Function(
            final SourceLocation location,
            final String name,
            final String[] argNames,
            final boolean hasVarArgs,
            final Expression body) {
        assert location != null;
        assert name != null;
        assert argNames != null;
        assert body != null;

        this.location = location;
        this.name = name;
        this.argNames = argNames;
        this.hasVarArgs = hasVarArgs;
        this.body = body;
    }

    @Override
    public SourceLocation location() {
        return location;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int argCount() {
        return argNames.length;
    }

    @Override
    public boolean hasVarArgs() {
        return hasVarArgs;
    }

    @Override
    public Value call(final Env parent, final Value... args) {
        if (args.length < argNames.length)
            throw new TitanException(
                    location,
                    "not enough arguments: provided %d, needs %d",
                    args.length,
                    argNames.length);

        if (!hasVarArgs && args.length > argNames.length)
            throw new TitanException(
                    location,
                    "too many arguments: provided %d, needs %d",
                    args.length,
                    argNames.length);

        final Value[] varargs;
        if (hasVarArgs) {
            varargs = new Value[args.length - argNames.length];
            for (int i = argNames.length; i < args.length; ++i)
                varargs[i - argNames.length] = args[i];
        } else {
            varargs = null;
        }

        final var env = new Env(parent, varargs);
        for (int i = 0; i < argNames.length; ++i)
            env.defineVariable(location, argNames[i], args[i]);

        return body.evaluate(env);
    }
}
