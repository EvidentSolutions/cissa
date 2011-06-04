package fi.evident.cissa.parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;

import static org.codehaus.jparsec.Parsers.*;
import static org.codehaus.jparsec.Scanners.isChar;
import static org.codehaus.jparsec.pattern.Patterns.regex;

final class CissaLexer {
    static final Parser<?> optSpaces =
        or(Scanners.WHITESPACES, Scanners.JAVA_LINE_COMMENT, Scanners.JAVA_BLOCK_COMMENT).many_();
    static final Parser<?> colon = token(':');
    static final Parser<?> comma = token(',');
    static final Parser<?> semicolon = token(';');
    static final Parser<?> openingBrace = token('{');
    static final Parser<?> closingBrace = token('}');
    static final Parser<?> openingParen = token('(');
    static final Parser<?> closingParen = token(')');
    static final Parser<?> openingBracket = token('[');
    static final Parser<?> closingBracket = token(']');

    static final Parser<String> identifier =
        Scanners.pattern(regex("-?[a-zA-Z][-a-zA-Z0-9_]*"), "identifier").source();


    static Parser<String> variable() {
        return token(sequence(isChar('@'), identifier)).label("variable");
    }

    static <T> Parser<T> inBraces(Parser<T> p) {
        return between(openingBrace, p, closingBrace);
    }

    static <T> Parser<T> inParens(Parser<T> p) {
        return between(openingParen, p, closingParen);
    }

    static <T> Parser<T> token(Parser<T> p) {
        return p.followedBy(optSpaces);
    }

    static Parser<?> token(char c) {
        return token(isChar(c));
    }

    static Parser<String> token(String s) {
        return token(Scanners.string(s).source());
    }
}
