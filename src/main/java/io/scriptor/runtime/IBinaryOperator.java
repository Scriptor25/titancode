package io.scriptor.runtime;

@FunctionalInterface
public interface IBinaryOperator {

    Value evaluate(final Value lhs, final Value rhs);
}
