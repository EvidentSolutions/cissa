/*
 * Copyright (c) 2011 Evident Solutions Oy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package fi.evident.cissa.template;

import fi.evident.cissa.model.*;

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
            return evaluateAmounts((CSSAmount) left, (CSSAmount) right);
        else if (left instanceof CSSColor && right instanceof CSSColor)
            return evaluateColors((CSSColor) left, (CSSColor) right);
        else if (left instanceof CSSColor && right instanceof CSSAmount && this == MULTIPLY)
            return multiplyColor((CSSColor) left, (CSSAmount) right);
        else if (left instanceof CSSAmount && right instanceof CSSColor && this == MULTIPLY)
            return multiplyColor((CSSColor) right, (CSSAmount) left);
        else
            throw new IncompatibleUnitsException();
    }

    private static CSSValue multiplyColor(CSSColor color, CSSAmount scalar) {
        Dimension value = scalar.getValue();
        if (value.getUnit() == DimensionUnit.EMPTY)
            return color.multiply(value.getValue().doubleValue());
        else
            throw new IncompatibleUnitsException();
    }

    protected abstract Dimension evaluateDimensions(Dimension left, Dimension right);

    private CSSValue evaluateAmounts(CSSAmount left, CSSAmount right) {
        return CSSValue.amount(evaluateDimensions(left.getValue(), right.getValue()));
    }

    protected CSSColor evaluateColors(CSSColor left, CSSColor right) {
        throw new IncompatibleUnitsException();
    }

    @Override
    public String toString() {
        return symbol;
    }
}
