package io.scriptor.ast;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.parser.RLocation;
import io.scriptor.runtime.Env;
import io.scriptor.runtime.ObjectValue;
import io.scriptor.runtime.Value;

public class ObjectExpression extends Expression {

    public final Map<String, Expression> fields;

    public ObjectExpression(final RLocation location, final Map<String, Expression> fields) {
        super(location);
        this.fields = fields;
    }

    @Override
    public Value evaluate(final Env env) {
        final Map<String, Value> values = new HashMap<>();
        for (final var entry : fields.entrySet())
            values.put(entry.getKey(), entry.getValue().evaluate(env));
        return new ObjectValue(values);
    }
}
