package fi.evident.cissa.parser;

import fi.evident.cissa.model.CSSColor;
import fi.evident.cissa.model.CSSValue;
import fi.evident.cissa.model.Dimension;
import fi.evident.cissa.model.DimensionUnit;
import fi.evident.cissa.template.BinaryOperator;
import fi.evident.cissa.template.ValueExpression;
import org.codehaus.jparsec.OperatorTable;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Pair;
import org.codehaus.jparsec.functors.Unary;

import java.util.ArrayList;
import java.util.List;

import static fi.evident.cissa.parser.CissaLexer.*;
import static fi.evident.cissa.parser.CissaLexer.token;
import static org.codehaus.jparsec.Parsers.*;
import static org.codehaus.jparsec.Scanners.*;
import static org.codehaus.jparsec.pattern.Patterns.regex;

final class ExpressionParser {

    public static Parser<ValueExpression> value() {
        return tuple(exp(), sequence(comma, exp()).many()).map(new Map<Pair<ValueExpression, List<ValueExpression>>, ValueExpression>() {
            public ValueExpression map(Pair<ValueExpression, List<ValueExpression>> pair) {
                if (pair.b.isEmpty())
                    return pair.a;

                List<ValueExpression> exps = new ArrayList<ValueExpression>(1 + pair.b.size());
                exps.add(pair.a);
                exps.addAll(pair.b);
                return ValueExpression.list(exps);
            }
        });
    }

    private static Parser<ValueExpression> exp() {
        Parser.Reference<ValueExpression> ref = Parser.newReference();

        Parser<ValueExpression> unit =
                or(variableValue(), literalExpression(), inParens(ref.lazy()));

        Parser<ValueExpression> parser =
                new OperatorTable<ValueExpression>()
                .infixl(operator(BinaryOperator.ADD), 10)
                .infixl(operator(BinaryOperator.SUBTRACT), 10)
                .infixl(operator(BinaryOperator.MULTIPLY), 20)
                .infixl(operator(BinaryOperator.DIVIDE), 20)
                .prefix(unaryNeg(), 30)
                .build(unit);
        ref.set(parser);
        return parser;
    }

    private static Parser<Unary<ValueExpression>> unaryNeg() {
        Unary<ValueExpression> unaryNegExp =
                new Unary<ValueExpression>() {
                    public ValueExpression map(ValueExpression exp) {
                        return ValueExpression.binary(ValueExpression.ZERO, BinaryOperator.SUBTRACT, exp);
                    }
                };

        return token("-").retn(unaryNegExp);
    }

    private static Parser<Binary<ValueExpression>> operator(final BinaryOperator operator) {
        Binary<ValueExpression> foo = new Binary<ValueExpression>() {
            public ValueExpression map(ValueExpression left, ValueExpression right) {
                return ValueExpression.binary(left, operator, right);
            }
        };
        return token(operator.getSymbol()).retn(foo);
    }

    private static Parser<ValueExpression> variableValue() {
        return variable().map(new Map<String, ValueExpression>() {
            public ValueExpression map(String s) {
                return ValueExpression.variable(s);
            }
        });
    }

    private static Parser<ValueExpression> literalExpression() {
        return literal().map(new Map<CSSValue, ValueExpression>() {
            public ValueExpression map(CSSValue value) {
                return ValueExpression.literal(value);
            }
        });
    }

    private static Parser<CSSValue> literal() {
        Parser.Reference<CSSValue> literal = Parser.newReference();
        literal.set(or(numberLiteral(), colorLiteral(), stringLiteral(), builtinFunctionLiteral(literal.lazy()), identifierLiteral()));
        return literal.get();
    }

    private static Parser<CSSValue> colorLiteral() {
        Parser<CSSValue> hashColor =
            pattern(regex("#([0-9a-zA-Z]{6}|[0-9a-zA-Z]{3})"), "hash-color").source().map(new Map<String, CSSValue>() {
                public CSSValue map(String s) {
                    return CSSColor.parse(s);
                }
            });

        // TODO: rgb colors of form rgb(1,2,3)

        return token(hashColor);
    }

    private static Parser<CSSValue> stringLiteral() {
        return or(DOUBLE_QUOTE_STRING, SINGLE_QUOTE_STRING).map(new Map<String, CSSValue>() {
            public CSSValue map(String s) {
                return CSSValue.string(s.substring(1, s.length()-1));
            }
        }).label("string");
    }

    private static Parser<CSSValue> builtinFunctionLiteral(Parser<CSSValue> literal) {
        Parser<String> start = token(identifier.followedBy(isChar('(')));
        Parser<List<CSSValue>> args = literal.many();
        Parser<String> end = token(")");

        return pair(start, args.followedBy(end)).map(new Map<Pair<String, List<CSSValue>>, CSSValue>() {
            public CSSValue map(Pair<String, List<CSSValue>> p) {
                return CSSValue.apply(p.a, p.b);
            }
        });
    }

    private static Parser<CSSValue> numberLiteral() {
        return dimension().map(new Map<Dimension, CSSValue>() {
            public CSSValue map(Dimension value) {
                return CSSValue.amount(value);
            }
        });
    }

    private static Parser<Dimension> dimension() {
        return token(tuple(Scanners.DECIMAL, dimensionUnit())).map(new Map<Pair<String, DimensionUnit>, Dimension>() {
            public Dimension map(Pair<String, DimensionUnit> p) {
                return Dimension.dimension(p.a, p.b);
            }
        });
    }

    private static Parser<DimensionUnit> dimensionUnit() {
        Parser<String> unit = pattern(regex("([a-zA-Z]+|%)").optional(), "dimension unit").source();

        return unit.map(new Map<String, DimensionUnit>() {
            public DimensionUnit map(String s) {
                return s.isEmpty() ? DimensionUnit.EMPTY : DimensionUnit.forName(s);
            }
        });
    }

    private static Parser<CSSValue> identifierLiteral() {
        return identifier.map(new Map<String, CSSValue>() {
            public CSSValue map(String s) {
                return CSSValue.token(s);
            }
        });
    }
}
