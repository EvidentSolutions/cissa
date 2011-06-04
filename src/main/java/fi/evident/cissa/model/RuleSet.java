package fi.evident.cissa.model;

import fi.evident.cissa.utils.Require;

import java.util.ArrayList;
import java.util.List;

import static fi.evident.cissa.utils.CollectionUtils.join;

public final class RuleSet {
    private final List<Selector> selectors = new ArrayList<Selector>();
    private final List<Attribute> attributes = new ArrayList<Attribute>();

    public RuleSet(List<Selector> selectors) {
        this.selectors.addAll(selectors);
    }

    public void addAttribute(Attribute attribute) {
        Require.argumentNotNull("attribute", attribute);

        attributes.add(attribute);
    }

    @Override
    public String toString() {
        return join(selectors, ", ") + " { " + join(attributes, "; ") + " }";
    }
}
