package io.scriptor.runtime;

import io.scriptor.SourceLocation;

public class FunctionValue extends Value {

    private IFunction function;

    public FunctionValue(final SourceLocation location, final IFunction function) {
        super(location);
        assert function != null;
        this.function = function;
    }

    public void setValue(final IFunction function) {
        this.function = function;
    }

    @Override
    public IFunction getValue() {
        return function;
    }

    @Override
    public boolean getBoolean() {
        return true;
    }

    @Override
    public Type getType(final SourceLocation location) {
        return Type.getFunction(location);
    }
}
