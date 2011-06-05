package fi.evident.cissa.template;

import fi.evident.cissa.model.Attribute;
import fi.evident.cissa.model.RuleSet;
import fi.evident.cissa.model.Selector;

import java.util.ArrayList;
import java.util.List;

public final class RuleSetTemplate {

    private final List<Selector> selectors;
    private final List<VariableDefinition> bindings;
    private final List<AttributeTemplate> attributes;
    private final List<RuleSetTemplate> children;

    public RuleSetTemplate(List<Selector> selectors, List<VariableDefinition> bindings, List<AttributeTemplate> attributes, List<RuleSetTemplate> children) {
        this.selectors = new ArrayList<Selector>(selectors);
        this.bindings = new ArrayList<VariableDefinition>(bindings);
        this.attributes = new ArrayList<AttributeTemplate>(attributes);
        this.children = new ArrayList<RuleSetTemplate>(children);
    }

    public List<RuleSet> evaluate(Environment env) {
        List<RuleSet> result = new ArrayList<RuleSet>();
        SelectorSet context = new SelectorSet(selectors);

        evaluate(result, context, env);

        return result;
    }

    private void evaluate(List<RuleSet> result, SelectorSet context, Environment env) {
        Environment newEnv = env.extend(bindings);

        if (!attributes.isEmpty())
            result.add(new RuleSet(context.selectors, evaluateAttributes(newEnv)));

        for (RuleSetTemplate child : children)
            child.evaluate(result, context.makeExtendedSet(child.selectors), newEnv);
    }

    private List<Attribute> evaluateAttributes(Environment env) {
        List<Attribute> result = new ArrayList<Attribute>(attributes.size());
        for (AttributeTemplate template : attributes)
            result.add(template.evaluate(env));
        return result;
    }

    private static final class SelectorSet {
        private final List<Selector> selectors;

        SelectorSet() {
            this.selectors = new ArrayList<Selector>();
        }

        SelectorSet(List<Selector> selectors) {
            this.selectors = selectors;
        }

        public SelectorSet makeExtendedSet(List<Selector> ss) {
            SelectorSet set = new SelectorSet();

            for (Selector s1 : selectors)
                for (Selector s2 : ss)
                    set.selectors.add(Selector.compound(s1, "", s2));

            return set;
        }
    }
}
