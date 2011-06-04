package fi.evident.cissa.template;

import fi.evident.cissa.model.Document;
import fi.evident.cissa.model.RuleSet;

import java.util.ArrayList;
import java.util.List;

public final class DocumentTemplate {
    private final List<RuleSetTemplate> ruleSets;

    public DocumentTemplate(List<RuleSetTemplate> ruleSets) {
        this.ruleSets = new ArrayList<RuleSetTemplate>(ruleSets);
    }

    public Document evaluate() {
        Environment env = new Environment();

        Document doc = new Document();

        for (RuleSetTemplate template : ruleSets)
            for (RuleSet ruleSet : template.evaluate(env))
                doc.addRuleSet(ruleSet);

        return doc;
    }
}
