package io.scriptor.runtime;

@FunctionalInterface
public interface IBinaryOperator {

    IValue evaluate(final IValue lhs, final IValue rhs);
}
