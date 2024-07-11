package io.scriptor.runtime;

@FunctionalInterface
public interface IOperator {

    IValue evaluate(final IValue lhs, final IValue rhs);
}
