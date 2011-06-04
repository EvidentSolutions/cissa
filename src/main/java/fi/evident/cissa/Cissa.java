package fi.evident.cissa;

import fi.evident.cissa.parser.CissaParser;

public final class Cissa {

    private Cissa() { }

    public static String generate(String markup) {
        return CissaParser.parse(markup).toString();
    }
}
