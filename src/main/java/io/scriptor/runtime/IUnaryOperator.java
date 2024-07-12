package io.scriptor.runtime;

@FunctionalInterface
public interface IUnaryOperator {

    IValue evaluate(final IValue value);
}
