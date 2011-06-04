package fi.evident.cissa.model;

import java.util.ArrayList;
import java.util.List;

import static fi.evident.cissa.utils.CollectionUtils.join;

public final class RuleSet {
    private final List<Selector> selectors;
    private final List<Attribute> attributes;

    public RuleSet(List<Selector> selectors, List<Attribute> attributes) {
        this.selectors = new ArrayList<Selector>(selectors);
        this.attributes = new ArrayList<Attribute>(attributes);
    }

    @Override
    public String toString() {
        return join(selectors, ", ") + " { " + join(attributes, "; ") + " }";
    }
}
