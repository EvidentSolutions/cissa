package fi.evident.cissa.template;

import fi.evident.cissa.utils.Require;

public final class VariableDefinition {

    final String name;
    final ValueExpression exp;

    public VariableDefinition(String name, ValueExpression exp) {
        Require.argumentNotNull("name", name);
        Require.argumentNotNull("exp", exp);

        this.name = name;
        this.exp = exp;
    }
}
