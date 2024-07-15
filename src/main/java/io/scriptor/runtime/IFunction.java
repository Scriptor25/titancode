package io.scriptor.runtime;

import io.scriptor.parser.SourceLocation;

public interface IFunction {

    SourceLocation location();

    String name();

    int argCount();

    boolean hasVarArgs();

    Value call(final Env parent, final Value... args);
}
