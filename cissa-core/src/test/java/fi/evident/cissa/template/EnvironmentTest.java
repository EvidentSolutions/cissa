package fi.evident.cissa.template;

import fi.evident.cissa.model.CSSValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EnvironmentTest {

    private Environment parent = new Environment();
    private Environment env = new Environment(parent);

    @Test(expected = UnboundVariableException.class)
    public void lookingUpUnboundVariableThrowsException() {
        env.lookup("foo");
    }

    @Test
    public void extendingEnvironmentMakesVariableAvailable() {
        CSSValue value = CSSValue.token("bar");
        env.bind("foo", value);

        assertEquals(value, env.lookup("foo"));
    }

    @Test
    public void lookupsAreDelegatedToParentIfBindingIsNotFound() {
        CSSValue value = CSSValue.token("bar");
        parent.bind("foo", value);

        assertEquals(value, env.lookup("foo"));
    }

    @Test
    public void childBindingsArePreferredToParent() {
        CSSValue bar = CSSValue.token("bar");
        CSSValue baz = CSSValue.token("baz");

        parent.bind("foo", bar);
        env.bind("foo", baz);

        assertEquals(baz, env.lookup("foo"));
    }
}
