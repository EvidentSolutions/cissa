package fi.evident.cissa.template;

import fi.evident.cissa.model.CSSValue;

import java.util.ArrayList;
import java.util.List;

public abstract class ValueExpression {
    public abstract CSSValue evaluate(Environment env);

    public static ValueExpression variable(final String name) {
        return new ValueExpression() {
            @Override
            public CSSValue evaluate(Environment env) {
                return env.lookup(name);
            }
        };
    }

    public static ValueExpression literal(final CSSValue value) {
        return new ValueExpression() {
            @Override
            public CSSValue evaluate(Environment env) {
                return value;
            }
        };
    }

    public static ValueExpression list(final List<ValueExpression> exps) {
        return new ValueExpression() {
            @Override
            public CSSValue evaluate(Environment env) {
                List<CSSValue> values = new ArrayList<CSSValue>(exps.size());
                for (ValueExpression exp : exps)
                    values.add(exp.evaluate(env));
                return CSSValue.list(values);
            }
        };
    }

    public static ValueExpression binary(final ValueExpression left, final BinaryOperator op, final ValueExpression right) {
        return new ValueExpression() {
            @Override
            public CSSValue evaluate(Environment env) {
                return op.evaluate(left.evaluate(env), right.evaluate(env));
            }
        };
    }
}
