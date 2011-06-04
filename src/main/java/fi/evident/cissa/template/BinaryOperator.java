package fi.evident.cissa.template;

import fi.evident.cissa.model.CSSAmount;
import fi.evident.cissa.model.CSSColor;
import fi.evident.cissa.model.CSSValue;
import fi.evident.cissa.model.Dimension;

public enum BinaryOperator {
    ADD("+") {
        @Override
        protected Dimension evaluateDimensions(Dimension left, Dimension right) {
            return left.add(right);
        }

        @Override
        protected CSSColor evaluateColors(CSSColor left, CSSColor right) {
            return left.add(right);
        }
    },

    SUBTRACT("-") {
        @Override
        protected Dimension evaluateDimensions(Dimension left, Dimension right) {
            return left.subtract(right);
        }
    },

    MULTIPLY("*") {
        @Override
        protected Dimension evaluateDimensions(Dimension left, Dimension right) {
            return left.multiply(right);
        }
    },

    DIVIDE("/") {
        @Override
        protected Dimension evaluateDimensions(Dimension left, Dimension right) {
            return left.divide(right);
        }
    };

    private final String symbol;

    private BinaryOperator(String symbol) {
        this.symbol = symbol;
    }

    public CSSValue evaluate(CSSValue left, CSSValue right) {
        if (left instanceof CSSAmount && right instanceof CSSAmount)
            return CSSValue.amount(evaluateDimensions(((CSSAmount) left).getValue(), ((CSSAmount) right).getValue()));

        if (left instanceof CSSColor && right instanceof CSSColor)
            return evaluateColors((CSSColor) left, (CSSColor) right);

        throw new IllegalArgumentException("can't calculate " + left + " " + symbol + " " + right);
    }

    protected abstract Dimension evaluateDimensions(Dimension left, Dimension right);

    protected CSSColor evaluateColors(CSSColor left, CSSColor right) {
        throw new UnsupportedOperationException("operation " + symbol + " is not supported for colors");
    }
}
