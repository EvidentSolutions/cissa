package fi.evident.cissa.parser;

import fi.evident.cissa.model.Selector;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Pair;

import java.util.List;

import static fi.evident.cissa.parser.CissaLexer.*;
import static fi.evident.cissa.parser.CissaLexer.token;
import static org.codehaus.jparsec.Parsers.*;
import static org.codehaus.jparsec.Scanners.isChar;
import static org.codehaus.jparsec.pattern.Patterns.regex;

final class SelectorParser {

    private static final Parser<String> combinator = token(isChar('+').or(isChar('>')).source());

    // selectors:
    //      selector (',' selector)*
    public static Parser<List<Selector>> selectors() {
        return commaSep(selector()).followedBy(optSpaces);
    }

    private static Parser<Selector> selector() {
        Parser.Reference<Selector> self = Parser.newReference();
        Parser<String> comb = combinator.optional("");

        self.set(tuple(simpleSelector(), tuple(comb, self.lazy()).optional()).map(new Map<Pair<Selector, Pair<String, Selector>>, Selector>() {
            public Selector map(Pair<Selector, Pair<String, Selector>> p) {
                return (p.b == null)
                    ? p.a
                    : Selector.compound(p.a, p.b.a, p.b.b);
            }
        }));

        return self.get();
    }

    private static Parser<Selector> simpleSelector() {
        Parser<String> idRef = Scanners.pattern(regex("#[-a-zA-Z0-9_]*"), "idref").source();
        Parser<String> classRef = sequence(isChar('.'), identifier).source();
        Parser<String> pseudo = sequence(isChar(':'), identifier).source();
        Parser<String> spec = or(idRef, classRef, attrib(), pseudo);
        Parser<String> elementName = or(isChar('*').source(), identifier);

        Parser<Selector> withElement =
           tuple(elementName, spec.many()).map(new Map<Pair<String, List<String>>, Selector>() {
               public Selector map(Pair<String, List<String>> p) {
                   return Selector.simple(p.a, p.b);
               }
           });

        Parser<Selector> withoutElement =
            spec.many1().map(new Map<List<String>, Selector>() {
                public Selector map(List<String> specs) {
                    return Selector.simple("", specs);
                }
            });

        return token(or(withElement, withoutElement));
    }

    private static Parser<String> attrib() {
        Parser<String> attributeValue = token(identifier); // TODO: or string

        Parser<String> op = or(token("="), token("~="), token("!="));

        Parser<String> rest = pair(op, attributeValue.optional("")).map(new Map<Pair<String, String>, String>() {
            public String map(Pair<String, String> p) {
                return p.a + p.b;
            }
        });

        Parser<String> contents = pair(token(identifier), rest.optional("")).map(new Map<Pair<String, String>, String>() {
            public String map(Pair<String, String> p) {
                return p.a + p.b;
            }
        });

        return inBrackets(contents).map(new Map<String, String>() {
            public String map(String s) {
                return "[" + s + "]";
            }
        });
    }

    private static <T> Parser<List<T>> commaSep(Parser<T> p) {
        return p.sepBy(comma);
    }

    private static <T> Parser<T> inBrackets(Parser<T> p) {
        return between(openingBracket, p, closingBracket);
    }
}
