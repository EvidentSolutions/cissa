package fi.evident.cissa;

import fi.evident.cissa.model.Document;

public final class Cissa {

    private Cissa() { }

    public static String generate(String markup) {
        return process(markup).toString();
    }

    private static Document process(String markup) {
        throw new RuntimeException("unsupported markup: " + markup);
    }
}
