package io.scriptor.ast;

import java.lang.reflect.Method;
import java.util.Arrays;

import io.scriptor.runtime.Env;
import io.scriptor.runtime.IValue;

public class NativeExpression extends Expression {

    public final String name;
    public final Expression[] args;

    private final Class<?> clazz;
    private final Method method;
    private final boolean isVoid;

    public NativeExpression(final CallExpression expression) {
        name = ((StringExpression) expression.args[0]).value;
        args = Arrays.copyOfRange(expression.args, 1, expression.args.length);

        final var delimIdx = name.lastIndexOf('.');
        final String methodName;
        if (delimIdx >= 0) {
            final var clazzName = name.substring(0, delimIdx);
            methodName = name.substring(delimIdx + 1, name.length());

            try {
                clazz = Class.forName(clazzName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            methodName = name;
            clazz = getClass();
        }

        final var opt = Arrays.stream(clazz.getMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst();
        if (opt.isEmpty())
            throw new IllegalStateException(String.format("undefined native function '%s'", name));
        method = opt.get();
        isVoid = method.getReturnType() == void.class || method.getReturnType() == Void.class;
    }

    @Override
    public IValue evaluate(final Env env) {
        try {
            final var eargs = new Object[args.length];
            for (int i = 0; i < args.length; ++i)
                eargs[i] = args[i].evaluate(env).getValue();

            final var result = method.invoke(null, eargs);
            if (isVoid)
                return null;
            return IValue.fromJava(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
