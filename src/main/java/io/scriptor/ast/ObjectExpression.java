package io.scriptor.ast;

import java.util.HashMap;
import java.util.Map;

import io.scriptor.SourceLocation;
import io.scriptor.runtime.Environment;
import io.scriptor.runtime.ObjectValue;
import io.scriptor.runtime.Value;

public class ObjectExpression extends Expression {

    private final Map<String, Expression> fields;

    public ObjectExpression(final SourceLocation location, final Map<String, Expression> fields) {
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
                    .append(" = ")
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
    public boolean isConstant() {
        return !fields
                .values()
                .stream()
                .anyMatch(field -> !field.isConstant());
    }

    @Override
    public Expression makeConstant() {
        for (final var entry : fields.entrySet())
            entry.setValue(entry.getValue().makeConstant());
        return super.makeConstant();
    }

    @Override
    public Value evaluate(final Environment env) {
        final Map<String, Value> values = new HashMap<>();
        for (final var entry : fields.entrySet())
            values.put(entry.getKey(), entry.getValue().evaluate(env));
        return new ObjectValue(location, values);
    }
}
