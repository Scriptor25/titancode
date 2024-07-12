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

                        case '"':
                            mode = ParserMode.STRING;
                            break;

                        case '\'':
                            mode = ParserMode.CHAR;
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

                        case '?':
                            value += (char) chr;
                            chr = get();
                            return token = new Token(TokenType.QUEST, value);

                        case '!':
                            value += (char) chr;
                            chr = get();
                            return token = new Token(TokenType.EXCLAM, value);

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

                case STRING:
                    if (chr == '"') {
                        chr = get();
                        return token = new Token(TokenType.STRING, value);
                    }
                    if (chr == '\\') {
                        escape();
                    }
                    value += (char) chr;
                    break;

                case CHAR:
                    if (chr == '\'') {
                        chr = get();
                        return token = new Token(TokenType.CHAR, value);
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

    public Token expect(final TokenType type) throws IOException {
        assert at(type);
        return skip();
    }

    public void expect(final String value) throws IOException {
        assert at(value);
        next();
    }

    public Token skip() throws IOException {
        final var t = token;
        next();
        return t;
    }

    public Expression parse() throws IOException {
        if (at("def"))
            return parseDef();

        return parseBinary();
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

    public Expression parseDef() throws IOException {
        // def <name>

        expect("def");
        final var name = expect(TokenType.ID).value();

        if (at(TokenType.PAREN_OPEN)) {
            // def <name>(<arg>..., ?) = <expression>

            next();

            final List<String> arglist = new Vector<>();
            boolean varargs = false;
            while (!at(TokenType.PAREN_CLOSE)) {
                if (at(TokenType.QUEST)) {
                    next();
                    varargs = true;
                    break;
                }

                arglist.add(expect(TokenType.ID).value());

                if (!at(TokenType.PAREN_CLOSE))
                    expect(TokenType.COMMA);
            }
            expect(TokenType.PAREN_CLOSE);

            expect("=");
            final var expression = parse();

            return new DefFunctionExpression(name, arglist.toArray(String[]::new), varargs, expression);
        }

        if (at(TokenType.BRACKET_OPEN)) {
            // array

            next();
            final var size = parse();
            expect(TokenType.BRACKET_CLOSE);

            expect("=");
            final var expression = parse();

            return new DefVariableExpression(name, size, expression);
        }

        expect("=");
        final var expression = parse();

        return new DefVariableExpression(name, expression);
    }

    public NativeExpression parseNative() throws IOException {
        // native("<name>", <args>...)

        expect("native");
        expect(TokenType.PAREN_OPEN);
        final var name = expect(TokenType.STRING).value();
        final List<Expression> arglist = new Vector<>();
        while (at(TokenType.COMMA)) {
            next();
            arglist.add(parse());
        }
        expect(TokenType.PAREN_CLOSE);

        return new NativeExpression(name, arglist.toArray(Expression[]::new));
    }

    public RangeExpression parseFor() throws IOException {
        // for [<from>, <to>, <step>] -> <id> <expression>

        expect("for");
        expect(TokenType.BRACKET_OPEN);
        final var from = parse();
        expect(TokenType.COMMA);
        final var to = parse();

        final Expression step;
        if (at(TokenType.BRACKET_CLOSE)) {
            next();
            step = null;
        } else {
            expect(TokenType.COMMA);
            step = parse();
            expect(TokenType.BRACKET_CLOSE);
        }

        final String id;
        if (at("->")) {
            next();
            id = expect(TokenType.ID).value();
        } else {
            id = null;
        }

        final var expression = parse();

        return new RangeExpression(from, to, step, id, expression);
    }

    public WhileExpression parseWhile() throws IOException {
        // while [<condition>] <expression>

        expect("while");
        expect(TokenType.BRACKET_OPEN);
        final var condition = parse();
        expect(TokenType.BRACKET_CLOSE);
        final var expression = parse();

        return new WhileExpression(condition, expression);
    }

    public IfExpression parseIf() throws IOException {
        // if [<condition>] <expression> else <expression>

        expect("if");
        expect(TokenType.BRACKET_OPEN);
        final var condition = parse();
        expect(TokenType.BRACKET_CLOSE);
        final var branchTrue = parse();

        final Expression branchFalse;
        if (at("else")) {
            next();
            branchFalse = parse();
        } else {
            branchFalse = null;
        }

        return new IfExpression(condition, branchTrue, branchFalse);
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
            lhs = new BinaryExpression(op, lhs, rhs);
        }
        return lhs;
    }

    public Expression parseCall() throws IOException {
        // <callee>(<arg>...)

        var expression = parseIndex();
        if (!atEOF() && at(TokenType.PAREN_OPEN)) {
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

    public Expression parseIndex() throws IOException {
        // <expression>[<index>]

        var expression = parsePrimary();
        while (!atEOF() && at(TokenType.BRACKET_OPEN)) {
            next();
            final var index = parse();
            expect(TokenType.BRACKET_CLOSE);
            expression = new IndexExpression(expression, index);
        }
        return expression;
    }

    public Expression parsePrimary() throws IOException {

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
            return new IDExpression(name);
        }

        if (at(TokenType.NUMBER)) {
            final var value = skip().value();
            return new NumberExpression(value);
        }

        if (at(TokenType.CHAR)) {
            final var value = skip().value();
            return new CharExpression(value);
        }

        if (at(TokenType.STRING)) {
            final var value = skip().value();
            return new StringExpression(value);
        }

        if (at(TokenType.PAREN_OPEN))
            return parseGroup();

        if (at(TokenType.QUEST)) {
            next();
            return new VarargsExpression();
        }

        if (at(TokenType.EXCLAM)) {
            next();
            final var expression = parse();
            return new UnaryExpression("!", expression);
        }

        throw new IllegalStateException();
    }
}
