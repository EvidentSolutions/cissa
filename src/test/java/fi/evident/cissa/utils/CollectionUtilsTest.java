package fi.evident.cissa.utils;

import org.junit.Test;

import java.util.ArrayList;

import static fi.evident.cissa.utils.CollectionUtils.join;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class CollectionUtilsTest {

    @Test
    public void joiningWillUseSeparatorBetweenItems() {
        assertEquals("foo, bar, baz", join(asList("foo", "bar", "baz"), ", "));
    }

    @Test
    public void joiningSingleItem() {
        assertEquals("foo", join(asList("foo"), ", "));
    }

    @Test
    public void joiningEmptyCollectionWillReturnEmptyString() {
        assertEquals("", join(new ArrayList<Object>(), "foo"));
    }

    @Test
    public void joiningWithNullsWillPrintNull() {
        assertEquals("foo, null, baz", join(asList("foo", null, "baz"), ", "));
    }
}
