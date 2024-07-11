package io.scriptor.runtime;

@FunctionalInterface
public interface IOperator {

    Value evaluate(final Value lhs, final Value rhs);
}
