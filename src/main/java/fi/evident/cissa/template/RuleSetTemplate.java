package fi.evident.cissa.template;

import fi.evident.cissa.model.RuleSet;
import fi.evident.cissa.model.Selector;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public final class RuleSetTemplate {

    private final List<Selector> selectors;
    private final List<VariableDefinition> variables;
    private final List<AttributeTemplate> attributes;

    public RuleSetTemplate(List<Selector> selectors, List<VariableDefinition> variables, List<AttributeTemplate> attributes) {
        this.selectors = new ArrayList<Selector>(selectors);
        this.variables = new ArrayList<VariableDefinition>(variables);
        this.attributes = new ArrayList<AttributeTemplate>(attributes);
    }

    public List<RuleSet> evaluate(Environment env) {
        RuleSet ruleSet = new RuleSet(selectors);

        Environment newEnv = new Environment(env);

        for (VariableDefinition var : variables)
            var.bindTo(env);

        for (AttributeTemplate template : attributes)
            ruleSet.addAttribute(template.evaluate(env));

        return asList(ruleSet);
    }
}
