package io.scriptor;

import io.scriptor.parser.SourceLocation;

public class TitanException extends RuntimeException {

    public TitanException() {
        super();
    }

    public TitanException(final SourceLocation location, final Throwable cause) {
        super(String.format("%s", location), cause);
    }

    public TitanException(final SourceLocation location, final String format, final Object... args) {
        super(String.format("%s: %s", location, format.formatted(args)));
    }
}
