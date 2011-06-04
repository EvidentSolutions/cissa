package fi.evident.cissa.model;

import java.util.ArrayList;
import java.util.List;

import static fi.evident.cissa.utils.CollectionUtils.join;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public final class Attribute {
    private final String name;
    private final List<CSSValue> values;
    private final boolean important;

    public Attribute(String name, CSSValue... values) {
        this(name, asList(values), false);
    }

    public Attribute(String name, List<CSSValue> values, boolean important) {
        this.name = name;
        this.values = unmodifiableList(new ArrayList<CSSValue>(values));
        this.important = important;
    }

    @Override
    public String toString() {
        return name + ": " + join(values, " ") + (important ? " !important" : "");
    }
}
