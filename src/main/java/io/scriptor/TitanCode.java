package io.scriptor;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import io.scriptor.parser.Parser;
import io.scriptor.runtime.Env;

public class TitanCode {

    private static final Map<String, Closeable> streams = new HashMap<>();

    public static void main(String[] args) throws IOException {
        final var filename = args[0];
        final var env = new Env();

        Parser.parseFile(new File(filename).getCanonicalFile(), new Vector<>(), env);

        final double result = env.call("main", (Object[]) args);
        System.out.printf("Exit Code %.0f%n", result);
    }

    public static void putchar(final char x) {
        System.out.print(x);
    }

    public static void printf(final String format, final Object... args) {
        System.out.printf(format, args);
    }

    public static String readLine() {
        return System.console().readLine();
    }

    public static void fopen(final String filename, final String flags) throws FileNotFoundException {
        if (streams.containsKey(filename))
            return;

        final var write = flags.contains("w");
        final var append = flags.contains("a");

        if (write) {
            streams.put(filename, new FileOutputStream(filename, append));
        } else {
            streams.put(filename, new FileInputStream(filename));
        }
    }

    public static void fclose(final String filename) throws IOException {
        if (!streams.containsKey(filename))
            return;

        final var stream = streams.get(filename);
        stream.close();
        streams.remove(filename);
    }

    public static int fread(final String filename) throws IOException {
        if (!streams.containsKey(filename))
            return -1;

        final var stream = (InputStream) streams.get(filename);
        return stream.read();
    }

    public static void fwrite(final String filename, final int x) throws IOException {
        if (!streams.containsKey(filename))
            return;

        final var stream = (OutputStream) streams.get(filename);
        stream.write(x);
    }

    public static void fprintf(final String filename, final String format, final Object... args) throws IOException {
        if (!streams.containsKey(filename))
            return;

        final var stream = (OutputStream) streams.get(filename);
        final var text = format.formatted(args);
        for (final var c : text.toCharArray())
            stream.write(c);
    }

    public static double number(final String x) {
        return Double.parseDouble(x);
    }

    public static char character(final double x) {
        return (char) x;
    }
}
