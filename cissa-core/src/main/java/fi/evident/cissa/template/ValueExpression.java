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

import fi.evident.cissa.model.CSSValue;
import fi.evident.cissa.model.Dimension;
import fi.evident.cissa.model.IncompatibleUnitsException;
import sun.awt.SunHints;

import java.util.ArrayList;
import java.util.List;

public abstract class ValueExpression {

    public static final ValueExpression ZERO = literal(CSSValue.amount(Dimension.ZERO));

    public abstract CSSValue evaluate(Environment env);

    public static ValueExpression variable(final String name, final SourceRange range) {
        return new ValueExpression() {
            @Override
            public CSSValue evaluate(Environment env) {
                CSSValue value = env.lookup(name);
                if (value != null)
                    return value;
                else
                    throw new UnboundVariableException(name, range);
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

    public static ValueExpression binary(final ValueExpression left, final BinaryOperator op, final ValueExpression right, final SourceRange range) {
        return new ValueExpression() {
            @Override
            public CSSValue evaluate(Environment env) {
                CSSValue lhs = left.evaluate(env);
                CSSValue rhs = right.evaluate(env);
                try {
                    return op.evaluate(lhs, rhs);
                } catch (ArithmeticException e) {
                    throw new EvaluationException(e.getMessage() + " occurred in " + lhs + " " + op + " " + rhs, range);
                } catch (IncompatibleUnitsException e) {
                    throw new EvaluationException("incompatible units " + lhs + " " + op + " " + rhs, range);
                }
            }
        };
    }
}
