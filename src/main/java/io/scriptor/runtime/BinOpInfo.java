package io.scriptor.runtime;

public record BinOpInfo(IBinOp operator, Type result, boolean reassign) {
}
