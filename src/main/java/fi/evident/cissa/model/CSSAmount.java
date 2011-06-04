package fi.evident.cissa.model;

import fi.evident.cissa.utils.Require;

public final class CSSAmount extends CSSValue {
    private final Dimension value;

    CSSAmount(Dimension value) {
        Require.argumentNotNull("value", value);

        this.value = value;
    }

    public Dimension getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
