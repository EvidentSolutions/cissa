package fi.evident.cissa.template;

import fi.evident.cissa.model.Attribute;

import java.util.ArrayList;
import java.util.List;

public final class AttributeTemplate {
    private final String name;
    private final List<ValueExpression> values;
    private final boolean important;

    public AttributeTemplate(String name, List<ValueExpression> values, boolean important) {
        this.name = name;
        this.values = new ArrayList<ValueExpression>(values);
        this.important = important;
    }

    public Attribute evaluate(Environment env) {
        Attribute attribute = new Attribute(name, important);

        for (ValueExpression exp : values)
            attribute.addValue(exp.evaluate(env));

        return attribute;
    }
}
