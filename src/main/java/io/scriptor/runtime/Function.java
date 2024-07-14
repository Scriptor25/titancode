package io.scriptor.runtime;

import io.scriptor.ast.Expression;

public class Function {

    public final String name;
    public final String[] args;
    public final boolean varargs;
    public final Expression expression;

    public Function(final String name, final String[] args, final boolean varargs, final Expression expression) {
        assert name != null;
        assert args != null;
        assert expression != null;
        this.name = name;
        this.args = args;
        this.varargs = varargs;
        this.expression = expression;
    }
}
