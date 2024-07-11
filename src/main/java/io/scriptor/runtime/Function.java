package io.scriptor.runtime;

import io.scriptor.ast.Expression;

public class Function {

    public final String name;
    public final String[] args;
    public final Expression expression;

    public Function(final String name, final String[] args, final Expression expression) {
        this.name = name;
        this.args = args;
        this.expression = expression;
    }
}
