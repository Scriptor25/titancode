package io.scriptor;

@FunctionalInterface
public interface IOperator {

    Value evaluate(final Value lhs, final Value rhs);
}
