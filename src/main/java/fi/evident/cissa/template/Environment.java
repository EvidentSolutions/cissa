package fi.evident.cissa.template;

import fi.evident.cissa.model.CSSValue;
import fi.evident.cissa.utils.Require;

import java.util.HashMap;
import java.util.Map;

public final class Environment {

    private final Map<String, CSSValue> env = new HashMap<String, CSSValue>();

    public CSSValue lookup(String name) {
        Require.argumentNotNull("name", name);

        CSSValue value = env.get(name);
        if (value != null)
            return value;
        else
            throw new UnboundVariableException(name);
    }

    public void extend(String name, CSSValue value) {
        Require.argumentNotNull("name", name);
        Require.argumentNotNull("value", value);
        
        env.put(name, value);
    }
}
