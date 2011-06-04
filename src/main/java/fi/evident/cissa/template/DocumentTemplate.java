package fi.evident.cissa.template;

import fi.evident.cissa.model.Document;
import fi.evident.cissa.model.RuleSet;

import java.util.ArrayList;
import java.util.List;

public final class DocumentTemplate {

    private final List<VariableDefinition> variables;
    private final List<RuleSetTemplate> ruleSets;

    public DocumentTemplate(List<VariableDefinition> variables, List<RuleSetTemplate> ruleSets) {
        this.variables = new ArrayList<VariableDefinition>(variables);
        this.ruleSets = new ArrayList<RuleSetTemplate>(ruleSets);
    }

    public Document evaluate() {
        Environment env = new Environment();

        for (VariableDefinition var : variables)
            var.bindTo(env);

        Document doc = new Document();

        for (RuleSetTemplate template : ruleSets)
            for (RuleSet ruleSet : template.evaluate(env))
                doc.addRuleSet(ruleSet);

        return doc;
    }
}
