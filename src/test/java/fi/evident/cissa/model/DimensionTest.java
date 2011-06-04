package fi.evident.cissa.model;

import org.junit.Test;

import java.math.BigDecimal;

import static fi.evident.cissa.model.Dimension.dimension;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class DimensionTest {

    @Test
    public void shouldHaveSensibleStringRepresentation() {
        assertEquals("8", dimension(8).toString());
        assertEquals("5", dimension("5").toString());
        assertEquals("5.123", dimension("5.123").toString());
        assertEquals("-1.2", dimension("-1.2").toString());
    }

    @Test
    public void stringRepresentationShouldIncludeUnit() {
        assertEquals("8m", dimension(8, DimensionUnit.forName("m")).toString());
    }

    @Test(expected = Exception.class)
    public void shouldNotBeConstructableFromNull() {
        dimension((BigDecimal) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeConstructableFromEmptyString() {
        dimension("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeConstructableFromStringNotRepresentingNumber() {
        dimension("foo");
    }

    @Test
    public void shouldDefineEquality() {
        assertThat("equal with same value", dimension("8.424"), equalTo(dimension("8.424")));
        assertThat("not equal with different value", dimension("8.4"), not(equalTo(dimension("4.5"))));
        assertThat("not equal with null", dimension("123"), not(equalTo(null)));
        assertFalse("not equal with other type", dimension("42").equals("foo"));
    }

    @Test
    public void definesConsistentHashCode() {
        assertEquals(dimension("5.4").hashCode(), dimension("5.4").hashCode());
    }

    @Test
    public void shouldSupportBasicArithmetic() {
        assertEquals(dimension("8.6"), dimension("6.1").add(dimension("2.5")));
        assertEquals(dimension("3.6"), dimension("6.1").subtract(dimension("2.5")));
        assertEquals(dimension("15.25"), dimension("6.1").multiply(dimension("2.5")));
        assertEquals(dimension("2.44"), dimension("6.1").divide(dimension("2.5")));
        assertEquals(dimension("2.5"), dimension("5").divide(dimension("2")));
    }

    @Test
    public void byDefaultThereIsNoUnit() {
        assertEquals(DimensionUnit.EMPTY, dimension("5.5").getUnit());
    }

    @Test
    public void shouldSupportExtractingValueAndUnit() {
        BigDecimal value = new BigDecimal("24.5");
        DimensionUnit unit = DimensionUnit.forName("foo");

        Dimension dim = dimension(value, unit);
        assertEquals(value, dim.getValue());
        assertEquals(unit, dim.getUnit());
    }

    @Test
    public void shouldCalculateCorrectUnits() {
        DimensionUnit m = DimensionUnit.forName("m");

        assertEquals(dimension(10, m), dimension(5, m).add(dimension(5, m)));
        assertEquals(dimension(10, m), dimension(5).add(dimension(5, m)));
        assertEquals(dimension(10, m), dimension(5, m).add(dimension(5)));
    }
}
