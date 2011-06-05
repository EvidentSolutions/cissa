package fi.evident.cissa;

import fi.evident.cissa.model.Document;
import fi.evident.cissa.parser.CissaParser;
import fi.evident.cissa.template.DocumentTemplate;

import java.nio.charset.Charset;

import static fi.evident.cissa.utils.IOUtils.readFileAsString;

public final class Cissa {

    private Cissa() { }

    public static String generate(String markup) {
        DocumentTemplate template = CissaParser.parse(markup);
        Document document = template.evaluate();
        return document.toString();
    }

    public static void main(String[] args) throws Exception {
        Charset charset = Charset.forName("UTF-8");

        for (String arg : args) {
            String source = readFileAsString(arg, charset);
            System.out.println(generate(source));
        }
    }
}
