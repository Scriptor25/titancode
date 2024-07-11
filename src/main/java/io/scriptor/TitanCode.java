package io.scriptor;

import java.io.IOException;

import io.scriptor.parser.Parser;
import io.scriptor.runtime.Env;

public class TitanCode {

    public static void main(String[] args) throws IOException {
        final var filename = args[0];
        final var parser = new Parser(filename);
        final var env = new Env();
        for (final var expression : parser) {
            expression.evaluate(env);
        }
        parser.close();

        final double result = env.call("main");
        System.out.printf("Exit Code %f%n", result);
    }
}