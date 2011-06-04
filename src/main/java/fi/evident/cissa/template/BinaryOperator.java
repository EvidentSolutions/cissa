package fi.evident.cissa.template;

import fi.evident.cissa.model.CSSAmount;
import fi.evident.cissa.model.CSSValue;
import fi.evident.cissa.model.Dimension;

public enum BinaryOperator {
    ADD("+") {
        @Override
        protected Dimension evaluate(Dimension left, Dimension right) {
            return left.add(right);
        }
    },

    SUBTRACT("-") {
        @Override
        protected Dimension evaluate(Dimension left, Dimension right) {
            return left.subtract(right);
        }
    },

    MULTIPLY("*") {
        @Override
        protected Dimension evaluate(Dimension left, Dimension right) {
            return left.multiply(right);
        }
    },

    DIVIDE("/") {
        @Override
        protected Dimension evaluate(Dimension left, Dimension right) {
            return left.divide(right);
        }
    };

    private final String symbol;

    private BinaryOperator(String symbol) {
        this.symbol = symbol;
    }

    public CSSValue evaluate(CSSValue left, CSSValue right) {
        if (left instanceof CSSAmount && right instanceof CSSAmount)
            return CSSValue.amount(evaluate(((CSSAmount) left).getValue(), ((CSSAmount) right).getValue()));

        throw new IllegalArgumentException("can't calculate " + left + " " + symbol + " " + right);
    }

    protected abstract Dimension evaluate(Dimension left, Dimension right);
}
