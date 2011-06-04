package fi.evident.cissa.template;

import fi.evident.cissa.model.CSSValue;

public interface BinaryOperator {
    CSSValue evaluate(CSSValue left, CSSValue right);
}
