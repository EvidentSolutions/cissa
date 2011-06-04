package fi.evident.cissa.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CSSColorTest {

    @Test
    public void constructorParametersAreSavedToFields() {
        CSSColor color = new CSSColor(42, 123, 54);
        assertEquals(42, color.r);
        assertEquals(123, color.g);
        assertEquals(54, color.b);
    }

    @Test
    public void hashedRepresentationIsUsedForToString() {
        CSSColor color = new CSSColor(42, 123, 54);

        assertEquals("#2a7b36", color.toString());
    }

    @Test
    public void colorsCanBeAdded() {
        CSSColor color1 = new CSSColor(0, 100, 200);
        CSSColor color2 = new CSSColor(4, 35, 42);

        CSSColor color3 = color1.add(color2);
        assertEquals(4, color3.r);
        assertEquals(135, color3.g);
        assertEquals(242, color3.b);
    }

    @Test
    public void addingCanOverflowColor() {
        CSSColor color1 = new CSSColor(0, 100, 200);
        CSSColor color2 = new CSSColor(4, 35, 142);

        CSSColor color3 = color1.add(color2);
        assertEquals(4, color3.r);
        assertEquals(135, color3.g);
        assertEquals(342, color3.b);
    }

    @Test
    public void overflowedComponentsAreClampedInStringRepresentation() {
        CSSColor color = new CSSColor(42, 423, 54);

        assertEquals("#2aff36", color.toString());
    }

    @Test
    public void colorsCanBeParsedFromLongHexRepresentation() {
        CSSColor color = CSSColor.parse("#2a7b36");

        assertEquals(42, color.r);
        assertEquals(123, color.g);
        assertEquals(54, color.b);
    }

    @Test
    public void colorsCanBeParsedFromShortHexRepresentation() {
        CSSColor color = CSSColor.parse("#2f8");

        assertEquals(34, color.r);
        assertEquals(255, color.g);
        assertEquals(136, color.b);
    }
}
