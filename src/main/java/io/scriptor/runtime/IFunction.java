package io.scriptor.runtime;

import io.scriptor.Name;
import io.scriptor.SourceLocation;

public interface IFunction {

    SourceLocation location();

    Name name();

    int argCount();

    boolean hasVarArgs();

    Value call(final Env parent, final Value... args);
}