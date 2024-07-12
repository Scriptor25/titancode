package io.scriptor.runtime;

@FunctionalInterface
public interface IUnaryOperator {

    Value evaluate(final Value value);
}
