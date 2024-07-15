package io.scriptor.runtime;

import java.lang.reflect.Method;
import java.util.Arrays;

import io.scriptor.Name;
import io.scriptor.SourceLocation;
import io.scriptor.TitanException;

public class NativeFunction implements IFunction {

    public final SourceLocation location;
    public final String nativeName;
    public final Name name;
    public final int argCount;

    public final Class<?> clazz;
    public final Method method;
    public final boolean isVoid;

    public NativeFunction(
            final SourceLocation location,
            final String nativeName,
            final Name name,
            final int argCount,
            final boolean hasVarArgs) {
        assert location != null;
        assert nativeName != null;
        assert name != null;
        assert argCount >= 0;

        this.location = location;
        this.nativeName = nativeName;
        this.name = name;
        this.argCount = argCount;

        final var delimIdx = nativeName.lastIndexOf('.');
        final String methodName;
        if (delimIdx >= 0) {
            final var clazzName = nativeName.substring(0, delimIdx);
            methodName = nativeName.substring(delimIdx + 1, nativeName.length());

            try {
                clazz = Class.forName(clazzName);
            } catch (final ClassNotFoundException e) {
                throw new TitanException(location, e);
            }
        } else {
            methodName = nativeName;
            clazz = getClass();
        }

        final var opt = Arrays.stream(clazz.getMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst();
        if (!opt.isPresent())
            throw new TitanException(location, "no such method: %s", nativeName);

        method = opt.get();
        isVoid = method.getReturnType() == void.class || method.getReturnType() == Void.class;

        assert method.isVarArgs() == hasVarArgs;
        assert (method.isVarArgs() ? method.getParameterCount() - 1 : method.getParameterCount()) == argCount;
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
        return argCount;
    }

    @Override
    public boolean hasVarArgs() {
        return method.isVarArgs();
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public Value call(final Environment parent, final Value... args) {
        try {
            final var params = method.getParameterTypes();
            final Object[] objects = new Object[params.length];

            if (hasVarArgs()) {
                final var paramsCount = params.length - 1;
                final var varargs = new Object[args.length - paramsCount];

                int i = 0;
                for (; i < paramsCount; ++i)
                    objects[i] = args[i].getValue();
                for (final int f = i; i < args.length; ++i)
                    varargs[i - f] = args[i].getValue();

                objects[paramsCount] = varargs;
            } else {
                for (int i = 0; i < args.length; ++i)
                    objects[i] = args[i].getValue();
            }

            final var result = method.invoke(null, objects);
            if (isVoid)
                return null;

            return Value.fromJava(location, result);
        } catch (final Exception e) {
            throw new TitanException(location, e);
        }
    }
}
