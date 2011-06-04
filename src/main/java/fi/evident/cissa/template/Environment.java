package fi.evident.cissa.template;

import fi.evident.cissa.model.CSSValue;
import fi.evident.cissa.utils.Require;

import java.util.HashMap;
import java.util.Map;

public final class Environment {

    private final Environment parent;
    private final Map<String, CSSValue> bindings = new HashMap<String, CSSValue>();

    public Environment() {
        this(null);
    }

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public CSSValue lookup(String name) {
        Require.argumentNotNull("name", name);

        for (Environment env = this; env != null; env = env.parent) {
            CSSValue value = env.bindings.get(name);
            if (value != null)
                return value;
        }

        throw new UnboundVariableException(name);
    }

    public void extend(String name, CSSValue value) {
        Require.argumentNotNull("name", name);
        Require.argumentNotNull("value", value);
        
        bindings.put(name, value);
    }
}
