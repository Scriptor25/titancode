package io.scriptor.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import io.scriptor.ast.BinaryExpression;
import io.scriptor.ast.CallExpression;
import io.scriptor.ast.CharExpression;
import io.scriptor.ast.ConstExpression;
import io.scriptor.ast.DefFunctionExpression;
import io.scriptor.ast.DefVariableExpression;
import io.scriptor.ast.Expression;
import io.scriptor.ast.GroupExpression;
import io.scriptor.ast.IDExpression;
import io.scriptor.ast.IfExpression;
import io.scriptor.ast.IndexExpression;
import io.scriptor.ast.NativeExpression;
import io.scriptor.ast.NumberExpression;
import io.scriptor.ast.RangeExpression;
import io.scriptor.ast.StringExpression;
import io.scriptor.ast.UnaryExpression;
import io.scriptor.ast.VarargsExpression;
import io.scriptor.ast.WhileExpression;

public class Parser implements AutoCloseable, Iterable<Expression> {

    public static boolean isDigit(final int chr) {
        return 0x30 <= chr && chr <= 0x39;
    }

    public static boolean isAlpha(final int chr) {
        return (0x41 <= chr && chr <= 0x5A) || (0x61 <= chr && chr <= 0x7A);
    }

    public static boolean isAlnum(final int chr) {
        return isDigit(chr) || isAlpha(chr);
    }

    public static boolean isID(final int chr) {
        return isAlnum(chr) || chr == '_';
    }

    public static boolean isOp(final int chr) {
        return chr == '+'
                || chr == '-'
                || chr == '*'
                || chr == '/'
                || chr == '%'
                || chr == '&'
                || chr == '|'
                || chr == '^'
                || chr == '='
                || chr == '<'
                || chr == '>';
    }

    public static RuntimeException error(final RToken token, final String format, final Object... args) {
        final var message = "%s: %s%n"
                .formatted(token.location(), format
                        .replaceAll("\\$value", token.value())
                        .replaceAll("\\$type", token.type().toString())
                        .formatted(args));
        return new IllegalStateException(message);
    }

    public final String filename;
    public final InputStream stream;
    public final Map<String, Integer> precedences = new HashMap<>();

    public int chr = -1;
    public int row = 1;
    public int column = 0;
    public RToken token;

    public Parser(final String filename) throws IOException {
        this.filename = filename;
        this.stream = new FileInputStream(filename);
        next();

        precedences.put("=", 0);
        precedences.put("<<=", 0);
        precedences.put(">>=", 0);
        precedences.put(">>>=", 0);
        precedences.put("+=", 0);
        precedences.put("-=", 0);
        precedences.put("*=", 0);
        precedences.put("/=", 0);
        precedences.put("%=", 0);
        precedences.put("&=", 0);
        precedences.put("|=", 0);
        precedences.put("&&", 1);
        precedences.put("||", 1);
        precedences.put("<", 2);
        precedences.put(">", 2);
        precedences.put("<=", 2);
        precedences.put(">=", 2);
        precedences.put("==", 2);
        precedences.put("&", 3);
        precedences.put("|", 3);
        precedences.put("^", 3);
        precedences.put("<<", 4);
        precedences.put(">>", 4);
        precedences.put(">>>", 4);
        precedences.put("+", 5);
        precedences.put("-", 5);
        precedences.put("*", 6);
        precedences.put("/", 6);
        precedences.put("%", 6);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public Iterator<Expression> iterator() {
        return new Iterator<Expression>() {

            @Override
            public boolean hasNext() {
                return token != null;
            }

            @Override
            public Expression next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                try {
                    return parse();
                } catch (IOException e) {
                    throw new NoSuchElementException(e);
                }
            }
        };
    }

    public int get() throws IOException {
        ++column;
        return stream.read();
    }

    public void escape() throws IOException {
        if (chr != '\\')
            return;

        chr = get();
        switch (chr) {
            case 'b' -> {
                chr = '\b';
                return;
            }
            case 'f' -> {
                chr = '\f';
                return;
            }
            case 'n' -> {
                chr = '\n';
                return;
            }
            case 'r' -> {
                chr = '\r';
                return;
            }
            case 't' -> {
                chr = '\t';
                return;
            }
            case 'x' -> {
                chr = get();
                String value = "";
                value += (char) chr;
                chr = get();
                value += (char) chr;
                chr = Integer.parseInt(value, 16);
                return;
            }
        }
    }

    public void newline() {
        column = 0;
        ++row;
    }

    public RToken next() throws IOException {
        if (chr < 0)
            chr = get();

        while (0x00 <= chr && chr <= 0x20) {
            if (chr == '\n')
                newline();
            chr = get();
        }

        var mode = ParserMode.NORMAL;
        var value = new String();
        RLocation loc = null;

        while (chr >= 0) {
            switch (mode) {
                case NORMAL:
                    switch (chr) {
                        case '#':
                            mode = ParserMode.COMMENT;
                            break;

                        case '"':
                            loc = new RLocation(filename, row, column);
                            mode = ParserMode.STRING;
                            break;

                        case '\'':
                            loc = new RLocation(filename, row, column);
                            mode = ParserMode.CHAR;
                            break;

                        default:
                            if (chr <= 0x20) {
                                if (chr == '\n')
                                    newline();
                                break;
                            }

                            if (isDigit(chr)) {
                                loc = new RLocation(filename, row, column);
                                mode = ParserMode.NUMBER;
                                value += (char) chr;
                                break;
                            }

                            if (isID(chr)) {
                                loc = new RLocation(filename, row, column);
                                mode = ParserMode.ID;
                                value += (char) chr;
                                break;
                            }

                            if (isOp(chr)) {
                                loc = new RLocation(filename, row, column);
                                mode = ParserMode.BINARY_OPERATOR;
                                value += (char) chr;
                                break;
                            }

                            loc = new RLocation(filename, row, column);
                            value += (char) chr;
                            chr = get();
                            return token = new RToken(loc, TokenType.OTHER, value);
                    }
                    break;

                case COMMENT:
                    if (chr == '#') {
                        mode = ParserMode.NORMAL;
                    }
                    break;

                case STRING:
                    if (chr == '"') {
                        chr = get();
                        return token = new RToken(loc, TokenType.STRING, value);
                    }
                    if (chr == '\\') {
                        escape();
                    }
                    value += (char) chr;
                    break;

                case CHAR:
                    if (chr == '\'') {
                        chr = get();
                        return token = new RToken(loc, TokenType.CHAR, value);
                    }
                    if (chr == '\\') {
                        escape();
                    }
                    value += (char) chr;
                    break;

                case NUMBER:
                    if (chr == '.') {
                        value += (char) chr;
                        break;
                    }
                    if (!isDigit(chr))
                        return token = new RToken(loc, TokenType.NUMBER, value);
                    value += (char) chr;
                    break;

                case ID:
                    if (!isID(chr))
                        return token = new RToken(loc, TokenType.ID, value);
                    value += (char) chr;
                    break;

                case BINARY_OPERATOR:
                    if (!isOp(chr))
                        return token = new RToken(loc, TokenType.BINARY_OPERATOR, value);
                    value += (char) chr;
                    break;

                default:
                    break;
            }

            chr = get();
        }

        return token = null;
    }

    public boolean at(final TokenType type) {
        assert token != null;
        return token.type() == type;
    }

    public boolean at(final String value) {
        assert token != null;
        return token.value().equals(value);
    }

    public boolean at(final String... values) {
        if (token != null)
            for (final var value : values)
                if (at(value))
                    return true;
        return false;
    }

    public boolean atEOF() {
        return token == null;
    }

    public RToken expect(final TokenType type) throws IOException {
        if (at(type))
            return skip();

        throw error(token, "unexpected token '$value' ($type), expected %s", type);
    }

    public void expect(final String value) throws IOException {
        if (at(value)) {
            next();
            return;
        }

        throw error(token, "unexpected token '$value' ($type), expected '%s'", value);
    }

    public RToken skip() throws IOException {
        final var t = token;
        next();
        return t;
    }

    public Expression parse() throws IOException {
        if (at("def"))
            return parseDef();

        final var expression = parseBinary();
        return ConstExpression.makeConst(expression);
    }

    public GroupExpression parseGroup() throws IOException {
        // (<expression>...)

        final var location = token.location();

        expect("(");
        final List<Expression> expressions = new Vector<>();
        while (!at(")")) {
            expressions.add(parse());
        }
        next();

        return new GroupExpression(location, expressions.toArray(Expression[]::new));
    }

    public Expression parseDef() throws IOException {
        // def <name>

        final var location = token.location();

        expect("def");
        final var name = expect(TokenType.ID).value();

        if (at("(")) {
            // def <name>(<arg>..., ?) = <expression>

            next();

            final List<String> arglist = new Vector<>();
            boolean varargs = false;
            while (!at(")")) {
                if (at("?")) {
                    next();
                    varargs = true;
                    break;
                }

                arglist.add(expect(TokenType.ID).value());

                if (!at(")"))
                    expect(",");
            }
            expect(")");

            expect("=");
            final var expression = parse();

            return new DefFunctionExpression(location, name, arglist.toArray(String[]::new), varargs, expression);
        }

        if (at("[")) {
            // def <name>[<size>] = <expression>

            next();
            final var size = parse();
            expect("]");

            final Expression expression;
            if (at("=")) {
                next();
                expression = parse();
            } else {
                expression = null;
            }

            return new DefVariableExpression(location, name, size, expression);
        }

        // def <name> = <expression>

        final Expression expression;
        if (at("=")) {
            next();
            expression = parse();
        } else {
            expression = null;
        }

        return new DefVariableExpression(location, name, expression);
    }

    public NativeExpression parseNative() throws IOException {
        // native("<name>", <args>...)

        final var location = token.location();

        expect("native");
        expect("(");
        final var name = expect(TokenType.STRING).value();
        final List<Expression> arglist = new Vector<>();
        while (at(",")) {
            next();
            arglist.add(parse());
        }
        expect(")");

        return new NativeExpression(location, name, arglist.toArray(Expression[]::new));
    }

    public RangeExpression parseFor() throws IOException {
        // for [<from>, <to>, <step>] -> <id> <expression>

        final var location = token.location();

        expect("for");
        expect("[");
        final var from = parse();
        expect(",");
        final var to = parse();

        final Expression step;
        if (at("]")) {
            next();
            step = null;
        } else {
            expect(",");
            step = parse();
            expect("]");
        }

        final String id;
        if (at("->")) {
            next();
            id = expect(TokenType.ID).value();
        } else {
            id = null;
        }

        final var expression = parse();

        return new RangeExpression(location, from, to, step, id, expression);
    }

    public WhileExpression parseWhile() throws IOException {
        // while [<condition>] <expression>

        final var location = token.location();

        expect("while");
        expect("[");
        final var condition = parse();
        expect("]");
        final var expression = parse();

        return new WhileExpression(location, condition, expression);
    }

    public IfExpression parseIf() throws IOException {
        // if [<condition>] <expression> else <expression>

        final var location = token.location();

        expect("if");
        expect("[");
        final var condition = parse();
        expect("]");
        final var branchTrue = parse();

        final Expression branchFalse;
        if (at("else")) {
            next();
            branchFalse = parse();
        } else {
            branchFalse = null;
        }

        return new IfExpression(location, condition, branchTrue, branchFalse);
    }

    public Expression parseBinary() throws IOException {
        return parseBinary(parseCall(), 0);
    }

    public Expression parseBinary(Expression lhs, int minPrecedence) throws IOException {
        // <expression> <operator> <expression>

        while (!atEOF() && at(TokenType.BINARY_OPERATOR) && precedences.get(token.value()) >= minPrecedence) {
            final var op = skip().value();
            final var opPrecedence = precedences.get(op);
            var rhs = parseCall();
            while (at(TokenType.BINARY_OPERATOR) && precedences.get(token.value()) > opPrecedence) {
                final var laPrecedence = precedences.get(token.value());
                rhs = parseBinary(rhs, opPrecedence + (laPrecedence > opPrecedence ? 1 : 0));
            }
            lhs = new BinaryExpression(lhs.location, op, lhs, rhs);
        }
        return lhs;
    }

    public Expression parseCall() throws IOException {
        // <callee>(<arg>...)

        var expression = parseIndex();
        if (!atEOF() && at("(")) {
            next();
            final List<Expression> args = new Vector<>();
            while (!at(")")) {
                args.add(parse());
                if (!at(")"))
                    expect(",");
            }
            next();
            expression = new CallExpression(expression.location, expression, args.toArray(Expression[]::new));
        }
        return expression;
    }

    public Expression parseIndex() throws IOException {
        // <expression>[<index>]

        var expression = parsePrimary();
        while (!atEOF() && at("[")) {
            next();
            final var index = parse();
            expect("]");
            expression = new IndexExpression(expression.location, expression, index);
        }
        return expression;
    }

    public Expression parsePrimary() throws IOException {

        final var location = token.location();

        if (at("native"))
            return parseNative();

        if (at("for"))
            return parseFor();

        if (at("while"))
            return parseWhile();

        if (at("if"))
            return parseIf();

        if (at(TokenType.ID)) {
            final var name = skip().value();
            return new IDExpression(location, name);
        }

        if (at(TokenType.NUMBER)) {
            final var value = skip().value();
            return new NumberExpression(location, value);
        }

        if (at(TokenType.CHAR)) {
            final var value = skip().value();
            return new CharExpression(location, value);
        }

        if (at(TokenType.STRING)) {
            final var value = skip().value();
            return new StringExpression(location, value);
        }

        if (at("("))
            return parseGroup();

        if (at("?")) {
            next();
            return new VarargsExpression(location);
        }

        if (at("!")) {
            next();
            final var expression = parse();
            return new UnaryExpression(location, "!", expression);
        }

        if (at("-")) {
            next();
            final var expression = parse();
            return new UnaryExpression(location, "-", expression);
        }

        throw error(token, "unhandled token '$value' ($type)");
    }
}
