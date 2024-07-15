package io.scriptor.runtime;

@FunctionalInterface
public interface IBinOp {

    Value evaluate(final Value lhs, final Value rhs);
}
