package fi.evident.cissa.model;

import fi.evident.cissa.utils.Require;

import java.util.ArrayList;
import java.util.List;

import static fi.evident.cissa.utils.CollectionUtils.join;

public final class Attribute {
    private final String name;
    private final List<CSSValue> values = new ArrayList<CSSValue>();
    private final boolean important;

    public Attribute(String name, boolean important) {
        this.name = name;
        this.important = important;
    }

    public void addValue(CSSValue value) {
        Require.argumentNotNull("value", value);
        
        values.add(value);
    }

    @Override
    public String toString() {
        return name + ": " + join(values, " ") + (important ? " !important" : "");
    }
}
