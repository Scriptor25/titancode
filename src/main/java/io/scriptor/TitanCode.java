package io.scriptor;

import java.io.IOException;
import java.util.Arrays;

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
        for (int i = 0; i < args.length; ++i)
            if (args[i] instanceof Object[])
                args[i] = Arrays.toString((Object[]) args[i]);
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

    public static String string(final Object object) {
        return object.toString();
    }

    public static void putchar(final char x) {
        System.out.print(x);
    }
}