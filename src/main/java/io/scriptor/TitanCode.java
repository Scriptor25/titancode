package io.scriptor;

import java.io.IOException;

public class TitanCode {

    public static void main(String[] args) throws IOException {
        final var filename = args[0];
        final var parser = new Parser(filename);
        final var env = new Env();
        for (final var expression : parser) {
            System.out.println(expression);
            System.out.println(expression.evaluate(env));
        }
        parser.close();
    }
}