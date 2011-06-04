package fi.evident.cissa.model;

import fi.evident.cissa.Dimension;
import fi.evident.cissa.utils.CollectionUtils;
import fi.evident.cissa.utils.Require;

import java.util.List;

public abstract class CSSValue {

    public static CSSValue amount(Dimension value) {
        return new CSSAmount(value);
    }

    public static CSSValue token(String token) {
        return new CSSToken(token);
    }

    public static CSSValue list(List<CSSValue> values) {
        return new CSSList(values);
    }
}

final class CSSAmount extends CSSValue {
    private final Dimension value;

    CSSAmount(Dimension value) {
        Require.argumentNotNull("value", value);

        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

final class CSSToken extends CSSValue {
    private final String token;

    CSSToken(String token) {
        Require.argumentNotNull("token", token);

        this.token = token;
    }

    @Override
    public String toString() {
        return token;
    }
}

final class CSSList extends CSSValue {

    private final List<CSSValue> values;

    CSSList(List<CSSValue> values) {
        Require.argumentNotNull("values", values);

        this.values = values;
    }

    @Override
    public String toString() {
        return CollectionUtils.join(values, ", ");
    }
}
