package io.scriptor.runtime;

public record UnOpInfo(IUnOp operator, Type result, boolean reassign) {
}
