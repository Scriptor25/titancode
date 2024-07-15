package io.scriptor.runtime;

import io.scriptor.Name;
import io.scriptor.SourceLocation;
import io.scriptor.TitanException;
import io.scriptor.ast.Expression;

public class Function implements IFunction {

    public final SourceLocation location;
    public final Name name;
    public final String[] argNames;
    public final boolean hasVarArgs;
    public Expression body;

    public Function(
            final SourceLocation location,
            final Name name,
            final String[] argNames,
            final boolean hasVarArgs,
            final Expression body) {
        assert location != null;
        assert name != null;
        assert argNames != null;

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
    public Name name() {
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
    public boolean isComplete() {
        return body != null;
    }

    @Override
    public Value call(final Environment parent, final Value... args) {
        assert isComplete();

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

        final var env = new Environment(parent, varargs);
        for (int i = 0; i < argNames.length; ++i)
            env.defineVariable(location, Name.get(argNames[i]), args[i]);

        return body.evaluate(env);
    }
}
