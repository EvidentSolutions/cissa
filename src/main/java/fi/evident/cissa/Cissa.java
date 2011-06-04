package fi.evident.cissa;

import fi.evident.cissa.model.Document;
import fi.evident.cissa.parser.CissaParser;
import fi.evident.cissa.template.DocumentTemplate;

public final class Cissa {

    private Cissa() { }

    public static String generate(String markup) {
        DocumentTemplate template = CissaParser.parse(markup);
        Document document = template.evaluate();
        return document.toString();
    }
}
