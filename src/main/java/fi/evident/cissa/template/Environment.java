package fi.evident.cissa.template;

import fi.evident.cissa.model.CSSValue;

public final class Environment {
    public CSSValue lookup(String name) {
        throw new UnboundVariableException(name);
    }
}
