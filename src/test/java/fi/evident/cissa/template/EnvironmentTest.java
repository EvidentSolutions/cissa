package fi.evident.cissa.template;

import fi.evident.cissa.model.CSSValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EnvironmentTest {

    private Environment env = new Environment();

    @Test(expected = UnboundVariableException.class)
    public void lookingUpUnboundVariableThrowsException() {
        env.lookup("foo");
    }

    @Test
    public void extendingEnvironmentMakesVariableAvailable() {
        CSSValue value = CSSValue.token("bar");
        env.extend("foo", value);

        assertEquals(value, env.lookup("foo"));
    }
}
