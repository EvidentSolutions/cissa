package fi.evident.cissa.model;

import java.util.ArrayList;
import java.util.List;

import static fi.evident.cissa.utils.CollectionUtils.join;

public final class RuleSet {
    private final List<Selector> selectors = new ArrayList<Selector>();
    private final List<Attribute> attributes = new ArrayList<Attribute>();

    public RuleSet(List<Selector> selectors, List<Attribute> attributes) {
        this.selectors.addAll(selectors);
        this.attributes.addAll(attributes);
    }

    @Override
    public String toString() {
        return join(selectors, ", ") + " { " + join(attributes, "; ") + " }";
    }
}
