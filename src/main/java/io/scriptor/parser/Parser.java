package io.scriptor.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import io.scriptor.TitanException;
import io.scriptor.ast.ArrayExpression;
import io.scriptor.ast.BinaryExpression;
import io.scriptor.ast.CallExpression;
import io.scriptor.ast.CharExpression;
import io.scriptor.ast.ConstExpression;
import io.scriptor.ast.DefFunctionExpression;
import io.scriptor.ast.DefVariableExpression;
import io.scriptor.ast.Expression;
import io.scriptor.ast.ForExpression;
import io.scriptor.ast.GroupExpression;
import io.scriptor.ast.IDExpression;
import io.scriptor.ast.IfExpression;
import io.scriptor.ast.IndexExpression;
import io.scriptor.ast.MemberExpression;
import io.scriptor.ast.NumberExpression;
import io.scriptor.ast.ObjectExpression;
import io.scriptor.ast.SizedArrayExpression;
import io.scriptor.ast.StringExpression;
import io.scriptor.ast.UnaryExpression;
import io.scriptor.ast.VarArgsExpression;
import io.scriptor.ast.WhileExpression;
import io.scriptor.runtime.Env;

public class Parser implements AutoCloseable, Iterable<Expression> {

    public static void parseFile(final File file, final List<File> parsed, final Env env) throws IOException {
        if (parsed.contains(file))
            return;
        parsed.add(file);

        final var parser = new Parser(file, parsed, env);
        for (final var expression : parser) {
            expression.evaluate(env);
        }
        parser.close();
    }

    private static final Map<String, Integer> precedences = new HashMap<>();

    static {
        precedences.clear();
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

    private final File file;
    private final List<File> parsed;
    private final Env env;

    private final InputStream stream;

    private int chr = -1;
    private int row = 1;
    private int column = 0;
    private RToken token;

    private Parser(final File file, final List<File> parsed, final Env env) throws IOException {
        this.file = file;
        this.parsed = parsed;
        this.env = env;

        this.stream = new FileInputStream(file);
        next();
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
                    while (at("include"))
                        parseInclude();

                    return parse();
                } catch (IOException e) {
                    throw new NoSuchElementException(e);
                }
            }
        };
    }

    private int get() throws IOException {
        ++column;
        return stream.read();
    }

    private void escape() throws IOException {
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

    private void newline() {
        column = 0;
        ++row;
    }

    private RToken next() throws IOException {
        if (chr < 0)
            chr = get();

        while (0x00 <= chr && chr <= 0x20) {
            if (chr == '\n')
                newline();
            chr = get();
        }

        var mode = ParserMode.NORMAL;
        var value = new String();
        SourceLocation loc = null;

        while (chr >= 0) {
            switch (mode) {
                case NORMAL:
                    switch (chr) {
                        case '#':
                            mode = ParserMode.COMMENT;
                            break;

                        case '"':
                            loc = new SourceLocation(file, row, column);
                            mode = ParserMode.STRING;
                            break;

                        case '\'':
                            loc = new SourceLocation(file, row, column);
                            mode = ParserMode.CHAR;
                            break;

                        default:
                            if (chr <= 0x20) {
                                if (chr == '\n')
                                    newline();
                                break;
                            }

                            if (isDigit(chr)) {
                                loc = new SourceLocation(file, row, column);
                                mode = ParserMode.NUMBER;
                                value += (char) chr;
                                break;
                            }

                            if (isID(chr)) {
                                loc = new SourceLocation(file, row, column);
                                mode = ParserMode.ID;
                                value += (char) chr;
                                break;
                            }

                            if (isOp(chr)) {
                                loc = new SourceLocation(file, row, column);
                                mode = ParserMode.BINARY_OPERATOR;
                                value += (char) chr;
                                break;
                            }

                            loc = new SourceLocation(file, row, column);
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

    private boolean at(final TokenType type) {
        assert token != null;
        return token.type() == type;
    }

    private boolean at(final String value) {
        assert token != null;
        return token.value().equals(value);
    }

    private boolean atEOF() {
        return token == null;
    }

    private RToken expect(final TokenType type) throws IOException {
        if (at(type))
            return skip();

        throw new TitanException(
                token.location(),
                "unexpected token '%s' (%s), expected %s",
                token.value(),
                token.type(),
                type);
    }

    private void expect(final String value) throws IOException {
        if (at(value)) {
            next();
            return;
        }

        throw new TitanException(
                token.location(),
                "unexpected token '%s' (%s), expected '%s'",
                token.value(),
                token.type(),
                value);
    }

    private RToken skip() throws IOException {
        final var t = token;
        next();
        return t;
    }

    private void parseInclude() throws IOException {
        // include "<filename>"

        expect("include");
        final var filename = expect(TokenType.STRING).value();
        var file = new File(filename);
        if (!file.isAbsolute())
            file = new File(this.file.getParentFile(), filename);

        parseFile(file.getCanonicalFile(), parsed, env);
    }

    private Expression parse() throws IOException {
        if (at("def"))
            return parseDef();

        final var expression = parseBinary();
        return ConstExpression.makeConst(expression);
    }

    private Expression parseDef() throws IOException {
        // def <name>

        final var location = token.location();

        expect("def");

        final String nativeName;
        if (at("native")) {
            next();
            expect("(");
            nativeName = expect(TokenType.STRING).value();
            expect(")");
        } else {
            nativeName = null;
        }

        final var name = expect(TokenType.ID).value();

        if (!atEOF() && at("(")) {
            // def <name>(<arg>..., ?) = <expression>

            next();

            final List<String> argNames = new Vector<>();
            boolean hasVarArgs = false;
            while (!at(")")) {
                if (at("?")) {
                    next();
                    hasVarArgs = true;
                    break;
                }

                argNames.add(expect(TokenType.ID).value());

                if (!at(")"))
                    expect(",");
            }
            expect(")");

            final Expression body;
            if (!atEOF() && at("=")) {
                next();
                body = parse();
            } else {
                body = null;
            }

            return new DefFunctionExpression(
                    location,
                    nativeName,
                    name,
                    argNames.toArray(String[]::new),
                    hasVarArgs,
                    body);
        }

        if (!atEOF() && at("[")) {
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

            return new DefVariableExpression(location, nativeName, name, size, expression);
        }

        // def <name> = <expression>

        final Expression expression;
        if (!atEOF() && at("=")) {
            next();
            expression = parse();
        } else {
            expression = null;
        }

        return new DefVariableExpression(location, nativeName, name, expression);
    }

    private ForExpression parseFor() throws IOException {
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

        return new ForExpression(location, from, to, step, id, expression);
    }

    private WhileExpression parseWhile() throws IOException {
        // while [<condition>] <expression>

        final var location = token.location();

        expect("while");
        expect("[");
        final var condition = parse();
        expect("]");
        final var expression = parse();

        return new WhileExpression(location, condition, expression);
    }

    private IfExpression parseIf() throws IOException {
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

    private GroupExpression parseGroup() throws IOException {
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

    private Expression parseBinary() throws IOException {
        return parseBinary(parseCall(), 0);
    }

    private Expression parseBinary(Expression lhs, int minPrecedence) throws IOException {
        // <expression> <operator> <expression>

        while (!atEOF() && at(TokenType.BINARY_OPERATOR) && precedences.get(token.value()) >= minPrecedence) {
            final var op = skip().value();
            final var opPrecedence = precedences.get(op);
            var rhs = parseCall();
            while (!atEOF() && at(TokenType.BINARY_OPERATOR) && precedences.get(token.value()) > opPrecedence) {
                final var laPrecedence = precedences.get(token.value());
                rhs = parseBinary(rhs, opPrecedence + (laPrecedence > opPrecedence ? 1 : 0));
            }
            lhs = new BinaryExpression(lhs.location, op, lhs, rhs);
        }
        return lhs;
    }

    private Expression parseCall() throws IOException {
        // <callee>(<arg>...)

        var callee = parseIndex();
        if (!atEOF() && at("(")) {
            next();

            final List<Expression> args = new Vector<>();
            while (!at(")")) {
                args.add(parse());
                if (!at(")"))
                    expect(",");
            }
            next();

            callee = new CallExpression(callee.location, callee, args.toArray(Expression[]::new));
        }

        return callee;
    }

    private Expression parseIndex() throws IOException {
        // <expression>[<index>]

        var expression = parseMember();
        while (!atEOF() && at("[")) {
            next();

            final var index = parse();
            expect("]");

            expression = new IndexExpression(expression.location, expression, index);
        }

        return expression;
    }

    private Expression parseMember() throws IOException {
        return parseMember(parsePrimary());
    }

    private Expression parseMember(Expression object) throws IOException {
        // <expression>.<expression>

        while (!atEOF() && at(".")) {
            next();

            final var member = expect(TokenType.ID).value();
            object = new MemberExpression(object.location, object, member);
        }

        return object;
    }

    private Expression parsePrimary() throws IOException {

        final var location = token.location();

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
            return new VarArgsExpression(location);
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

        if (at("{")) {
            next();
            final Map<String, Expression> fields = new HashMap<>();
            while (!atEOF() && !at("}")) {
                final var name = expect(TokenType.ID).value();

                final Expression value;
                if (at("[")) {
                    final var loc = token.location();

                    next();
                    final var size = parse();
                    expect("]");

                    final Expression init;
                    if (at("=")) {
                        next();
                        init = parse();
                    } else {
                        init = null;
                    }

                    value = new SizedArrayExpression(loc, size, init);
                } else {
                    expect("=");
                    value = parse();
                }

                fields.put(name, value);

                if (!at("}"))
                    expect(",");
            }
            expect("}");
            return new ObjectExpression(location, fields);
        }

        if (at("[")) {
            next();
            final List<Expression> values = new Vector<>();
            while (!atEOF() && !at("]")) {
                values.add(parse());
                if (!at("]"))
                    expect(",");
            }
            expect("]");
            return new ArrayExpression(location, values.toArray(Expression[]::new));
        }

        throw new TitanException(
                location,
                "unhandled token '%s' (%s)",
                token.value(),
                token.type());
    }
}
