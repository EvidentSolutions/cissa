package fi.evident.cissa.utils;

import java.util.Collection;

public final class Require {

    private Require() { }

    public static void argumentNotNull(String name, Object value) {
        if (value == null)
            throw new IllegalArgumentException("null value for argument '" + name + "'");
    }

    public static void argumentNotNullOrEmpty(String name, String value) {
        argumentNotNull(name, value);

        if (value.isEmpty())
            throw new IllegalArgumentException("empty value for argument '" + name + "'");
    }

    public static void argumentNotNullOrEmpty(String name, Collection value) {
        argumentNotNull(name, value);

        if (value.isEmpty())
            throw new IllegalArgumentException("empty value for argument '" + name + "'");
    }
}
