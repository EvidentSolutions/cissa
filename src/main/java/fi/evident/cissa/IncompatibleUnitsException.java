package fi.evident.cissa;

public class IncompatibleUnitsException extends RuntimeException {

    public IncompatibleUnitsException(DimensionUnit left, String op, DimensionUnit right) {
        super("units are incompatible: " + left + " " + op + " " + right);
    }
}
