/*
 * Copyright (c) 2011 Evident Solutions Oy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
