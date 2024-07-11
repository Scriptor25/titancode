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

        final double result = env.call("main", 3, "Hello", "World", "!");
        System.out.printf("Exit Code %.0f%n", result);
    }

    public static void printf(final String format, final Object... args) {
        System.out.printf(format, args);
    }

    public static double random(final double min, final double max) {
        return Math.random() * (max - min) + min;
    }

    public static String readLine() {
        return System.console().readLine();
    }

    public static double number(final String string) {
        return Double.parseDouble(string);
    }
}