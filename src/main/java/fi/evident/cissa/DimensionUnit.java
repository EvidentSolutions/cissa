package fi.evident.cissa;

import fi.evident.cissa.utils.Require;

public final class DimensionUnit {

    public static final DimensionUnit EMPTY = new DimensionUnit("");
    private final String name;

    private DimensionUnit(String name) {
        this.name = name;
    }

    public DimensionUnit add(DimensionUnit unit) {
        return mergeAdditive("+", unit);
    }

    public DimensionUnit subtract(DimensionUnit unit) {
        return mergeAdditive("-", unit);
    }

    private DimensionUnit mergeAdditive(String op, DimensionUnit unit) {
        Require.argumentNotNull("unit", unit);

        if (this == EMPTY || this.equals(unit)) return unit;
        if (unit == EMPTY) return this;

        throw new IncompatibleUnitsException(this, op, unit);
    }

    public DimensionUnit multiply(DimensionUnit unit) {
        Require.argumentNotNull("unit", unit);

        if (this == EMPTY) return unit;
        if (unit == EMPTY) return this;

        throw new IncompatibleUnitsException(this, "*", unit);
    }

    public DimensionUnit divide(DimensionUnit unit) {
        Require.argumentNotNull("unit", unit);

        if (unit == EMPTY) return this;
        if (unit.equals(this)) return EMPTY;

        throw new IncompatibleUnitsException(this, "/", unit);
    }

    public static DimensionUnit forName(String name) {
        Require.argumentNotNullOrEmpty("name", name);

        return new DimensionUnit(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o instanceof DimensionUnit) {
            DimensionUnit rhs = (DimensionUnit) o;
            return name.equals(rhs.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
