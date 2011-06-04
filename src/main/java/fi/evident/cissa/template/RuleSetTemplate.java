package fi.evident.cissa.template;

import fi.evident.cissa.model.RuleSet;
import fi.evident.cissa.model.Selector;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public final class RuleSetTemplate {

    private final List<Selector> selectors;
    private final List<AttributeTemplate> attributes;

    public RuleSetTemplate(List<Selector> selectors, List<AttributeTemplate> attributes) {
        this.selectors = new ArrayList<Selector>(selectors);
        this.attributes = new ArrayList<AttributeTemplate>(attributes);
    }

    public List<RuleSet> evaluate(Environment env) {
        RuleSet ruleSet = new RuleSet(selectors);

        for (AttributeTemplate template : attributes)
            ruleSet.addAttribute(template.evaluate(env));

        return asList(ruleSet);
    }
}
