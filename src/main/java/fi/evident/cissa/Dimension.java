package fi.evident.cissa;

import fi.evident.cissa.utils.Require;

import java.math.BigDecimal;

public final class Dimension {

    private final BigDecimal value;
    private final DimensionUnit unit;

    private Dimension(BigDecimal value, DimensionUnit unit) {
        Require.argumentNotNull("value", value);
        Require.argumentNotNull("unit", unit);

        this.value = value;
        this.unit = unit;
    }

    public BigDecimal getValue() {
        return value;
    }

    public DimensionUnit getUnit() {
        return unit;
    }

    public static Dimension dimension(long value) {
        return dimension(value, DimensionUnit.EMPTY);
    }

    public static Dimension dimension(long value, DimensionUnit unit) {
        return dimension(new BigDecimal(value), unit);
    }

    public static Dimension dimension(String value) {
        return dimension(value, DimensionUnit.EMPTY);
    }

    public static Dimension dimension(String value, DimensionUnit unit) {
        return dimension(new BigDecimal(value), unit);
    }

    public static Dimension dimension(BigDecimal value) {
        return dimension(value, DimensionUnit.EMPTY);
    }

    public static Dimension dimension(BigDecimal value, DimensionUnit unit) {
        return new Dimension(value, unit);
    }

    public Dimension add(Dimension rhs) {
        return dimension(value.add(rhs.value), unit.add(rhs.unit));
    }

    public Dimension subtract(Dimension rhs) {
        return dimension(value.subtract(rhs.value), unit.subtract(rhs.unit));
    }

    public Dimension multiply(Dimension rhs) {
        return dimension(value.multiply(rhs.value), unit.multiply(rhs.unit));
    }

    public Dimension divide(Dimension rhs) {
        return dimension(value.divide(rhs.value), unit.divide(rhs.unit));
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof Dimension) {
            Dimension rhs = (Dimension) o;
            return value.equals(rhs.value);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
