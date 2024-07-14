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
    public String toString() {
        if (fields.isEmpty())
            return "{}";

        final var builder = new StringBuilder()
                .append("{");

        ++depth;
        var spaces = getSpaces();

        boolean first = true;
        for (final var field : fields.entrySet()) {
            if (first)
                first = false;
            else
                builder.append(',');
            builder
                    .append('\n')
                    .append(spaces)
                    .append(field.getKey())
                    .append('=')
                    .append(field.getValue());
        }

        --depth;
        spaces = getSpaces();

        return builder
                .append('\n')
                .append(spaces)
                .append('}')
                .toString();
    }

    @Override
    public Value evaluate(final Env env) {
        final Map<String, Value> values = new HashMap<>();
        for (final var entry : fields.entrySet())
            values.put(entry.getKey(), entry.getValue().evaluate(env));
        return new ObjectValue(values);
    }
}
