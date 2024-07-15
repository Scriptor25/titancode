package io.scriptor.runtime;

@FunctionalInterface
public interface IUnOp {

    Value evaluate(final Value value);
}
