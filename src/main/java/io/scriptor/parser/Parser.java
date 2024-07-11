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
import io.scriptor.ast.DefExpression;
import io.scriptor.ast.Expression;
import io.scriptor.ast.GroupExpression;
import io.scriptor.ast.IDExpression;
import io.scriptor.ast.NumberExpression;
import io.scriptor.ast.RangeExpression;

public class Parser implements AutoCloseable, Iterable<Expression> {

    private static boolean isDigit(final int chr) {
        return 0x30 <= chr && chr <= 0x39;
    }

    private static boolean isAlpha(final int chr) {
        return (0x41 <= chr && chr <= 0x5A) || (0x61 <= chr && chr <= 0x7A);
    }

    private static boolean isAlnum(final int chr) {
        return isDigit(chr) || isAlpha(chr);
    }

    private static boolean isID(final int chr) {
        return isAlnum(chr) || chr == '_';
    }

    private static boolean isOp(final int chr) {
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

    public final String filename;
    public final InputStream stream;
    public final Map<String, Integer> precedences = new HashMap<>();

    public int chr = -1;
    public Token token;

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
        return stream.read();
    }

    public Token next() throws IOException {
        if (chr < 0)
            chr = get();

        while (0x00 <= chr && chr <= 0x20)
            chr = get();

        var mode = ParserMode.NORMAL;
        var value = new String();

        while (chr >= 0) {
            switch (mode) {
                case NORMAL:
                    switch (chr) {
                        case '#':
                            mode = ParserMode.COMMENT;
                            break;

                        case '(':
                            value += (char) chr;
                            chr = get();
                            return token = new Token(TokenType.PAREN_OPEN, value);

                        case ')':
                            value += (char) chr;
                            chr = get();
                            return token = new Token(TokenType.PAREN_CLOSE, value);

                        case '{':
                            value += (char) chr;
                            chr = get();
                            return token = new Token(TokenType.BRACE_OPEN, value);

                        case '}':
                            value += (char) chr;
                            chr = get();
                            return token = new Token(TokenType.BRACE_CLOSE, value);

                        case '[':
                            value += (char) chr;
                            chr = get();
                            return token = new Token(TokenType.BRACKET_OPEN, value);

                        case ']':
                            value += (char) chr;
                            chr = get();
                            return token = new Token(TokenType.BRACKET_CLOSE, value);

                        case '.':
                            value += (char) chr;
                            chr = get();
                            return token = new Token(TokenType.DOT, value);

                        case ',':
                            value += (char) chr;
                            chr = get();
                            return token = new Token(TokenType.COMMA, value);

                        case ';':
                            value += (char) chr;
                            chr = get();
                            return token = new Token(TokenType.SEMICOLON, value);

                        case ':':
                            value += (char) chr;
                            chr = get();
                            return token = new Token(TokenType.COLON, value);

                        default:
                            if (chr <= 0x20)
                                break;

                            if (isDigit(chr)) {
                                mode = ParserMode.NUMBER;
                                value += (char) chr;
                                break;
                            }

                            if (isID(chr)) {
                                mode = ParserMode.ID;
                                value += (char) chr;
                                break;
                            }

                            if (isOp(chr)) {
                                mode = ParserMode.BINARY_OPERATOR;
                                value += (char) chr;
                                break;
                            }

                            break;
                    }
                    break;

                case COMMENT:
                    if (chr == '#')
                        mode = ParserMode.NORMAL;
                    break;

                case NUMBER:
                    if (!isDigit(chr))
                        return token = new Token(TokenType.NUMBER, value);
                    value += (char) chr;
                    break;

                case ID:
                    if (!isID(chr))
                        return token = new Token(TokenType.ID, value);
                    value += (char) chr;
                    break;

                case BINARY_OPERATOR:
                    if (!isOp(chr))
                        return token = new Token(TokenType.BINARY_OPERATOR, value);
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
        return token != null && token.type() == type;
    }

    public boolean at(final String value) {
        return token != null && token.value().equals(value);
    }

    public boolean at(final String... values) {
        if (token != null)
            for (final var value : values)
                if (at(value))
                    return true;
        return false;
    }

    public Token expect(final TokenType type) throws IOException {
        if (at(type)) {
            return skip();
        }
        throw new IllegalStateException();
    }

    public void expect(final String value) throws IOException {
        if (at(value)) {
            next();
            return;
        }
        throw new IllegalStateException();
    }

    public Token skip() throws IOException {
        final var t = token;
        next();
        return t;
    }

    public Expression parse() throws IOException {
        if (at(TokenType.PAREN_OPEN))
            return parseGroup();
        if (at(TokenType.BRACKET_OPEN))
            return parseRange();
        if (at("def"))
            return parseDef();

        return parseBinary(parseCall(), 0);
    }

    public GroupExpression parseGroup() throws IOException {
        // (<expression>...)

        expect(TokenType.PAREN_OPEN);
        final List<Expression> expressions = new Vector<>();
        while (!at(TokenType.PAREN_CLOSE)) {
            expressions.add(parse());
        }
        next();

        return new GroupExpression(expressions.toArray(Expression[]::new));
    }

    public RangeExpression parseRange() throws IOException {
        // [<from>, <to>] {<id>} <expression>

        expect(TokenType.BRACKET_OPEN);
        final var from = parse();
        expect(TokenType.COMMA);
        final var to = parse();
        expect(TokenType.BRACKET_CLOSE);

        final String id;
        if (at(TokenType.BRACE_OPEN)) {
            next();
            id = expect(TokenType.ID).value();
            expect(TokenType.BRACE_CLOSE);
        } else {
            id = null;
        }

        final var expression = parse();

        return new RangeExpression(from, to, id, expression);
    }

    public DefExpression parseDef() throws IOException {
        // def <name>(<arg>...) = <expression>

        expect("def");
        final var name = expect(TokenType.ID).value();

        final String[] args;
        if (at(TokenType.PAREN_OPEN)) {
            next();

            final List<String> arglist = new Vector<>();
            while (!at(TokenType.PAREN_CLOSE)) {
                arglist.add(expect(TokenType.ID).value());
                if (!at(TokenType.PAREN_CLOSE))
                    expect(TokenType.COMMA);
            }
            next();
        
            args = arglist.toArray(String[]::new);
        }
        else {
            args = null;
        }

        final Expression expression;
        if (at("=")) {
            next();
            expression = parse();
        } else {
            expression = null;
        }

        return new DefExpression(name, args, expression);
    }

    public Expression parseBinary(Expression lhs, int minPrecedence) throws IOException {
        while (at(TokenType.BINARY_OPERATOR) && precedences.get(token.value()) >= minPrecedence) {
            final var op = skip().value();
            final var opPrecedence = precedences.get(op);
            var rhs = parseCall();
            while (at(TokenType.BINARY_OPERATOR) && precedences.get(token.value()) > opPrecedence) {
                final var laPrecedence = precedences.get(token.value());
                rhs = parseBinary(rhs, opPrecedence + (laPrecedence > opPrecedence ? 1 : 0));
            }
            lhs = new BinaryExpression(op, lhs, rhs);
        }
        return lhs;
    }

    public Expression parseCall() throws IOException {
        var expression = parsePrimary();
        if (at(TokenType.PAREN_OPEN)) {
            next();
            final List<Expression> args = new Vector<>();
            while (!at(TokenType.PAREN_CLOSE)) {
                args.add(parse());
                if (!at(TokenType.PAREN_CLOSE))
                    expect(TokenType.COMMA);
            }
            next();
            expression = new CallExpression(expression, args.toArray(Expression[]::new));
        }
        return expression;
    }

    public Expression parsePrimary() throws IOException {

        if (at(TokenType.ID)) {
            final var name = skip().value();
            return new IDExpression(name);
        }

        if (at(TokenType.NUMBER)) {
            final var value = skip().value();
            return new NumberExpression(value);
        }

        if (at(TokenType.PAREN_OPEN)) {
            next();
            final var value = parse();
            expect(TokenType.PAREN_CLOSE);
            return value;
        }

        throw new IllegalStateException();
    }
}
