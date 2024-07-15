package io.scriptor.ast;

import java.util.Arrays;

import io.scriptor.Name;
import io.scriptor.Namespace;
import io.scriptor.SourceLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.Value;

public class IDExpression extends Expression {

    public final Name name;

    public IDExpression(final SourceLocation location, final String... name) {
        super(location);

        assert name != null;
        assert name.length > 0;

        final Namespace namespace;
        if (name.length > 1)
            namespace = new Namespace(Arrays.copyOfRange(name, 0, name.length - 1));
        else
            namespace = new Namespace();

        this.name = Name.get(namespace, name[name.length - 1]);
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public Value evaluate(final Env env) {
        assert env != null;
        return env.getVariable(location, name).value;
    }
}
