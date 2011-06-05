package fi.evident.cissa.model;

import fi.evident.cissa.utils.CollectionUtils;
import fi.evident.cissa.utils.Require;

import java.util.ArrayList;
import java.util.List;

public final class Document {

    private final List<RuleSet> ruleSets = new ArrayList<RuleSet>();

    public void addRuleSet(RuleSet ruleSet) {
        Require.argumentNotNull("ruleSet", ruleSet);

        ruleSets.add(ruleSet);
    }

    @Override
    public String toString() {
        return CollectionUtils.join(ruleSets, "\n");
    }
}
