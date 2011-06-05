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

package fi.evident.cissa.model;

import fi.evident.cissa.utils.CollectionUtils;
import fi.evident.cissa.utils.Require;

import java.util.List;

public abstract class CSSValue {

    public static CSSValue amount(Dimension value) {
        return new CSSAmount(value);
    }

    public static CSSValue token(String token) {
        return new CSSToken(token);
    }

    public static CSSValue list(List<CSSValue> values) {
        return new CSSList(values);
    }

    public static CSSValue apply(String name, List<CSSValue> args) {
        return new CSSBuiltinApply(name, args);
    }

    public static CSSValue string(String s) {
        return new CSSString(s);
    }
}

final class CSSBuiltinApply extends CSSValue {

    private final String name;
    private final List<CSSValue> args;

    CSSBuiltinApply(String name, List<CSSValue> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public String toString() {
        return name + "(" + CollectionUtils.join(args, ", ") + ")";
    }
}

final class CSSToken extends CSSValue {
    private final String token;

    CSSToken(String token) {
        Require.argumentNotNullOrEmpty("token", token);

        this.token = token;
    }

    @Override
    public String toString() {
        return token;
    }
}

final class CSSList extends CSSValue {

    private final List<CSSValue> values;

    CSSList(List<CSSValue> values) {
        Require.argumentNotNullOrEmpty("values", values);

        this.values = values;
    }

    @Override
    public String toString() {
        return CollectionUtils.join(values, ", ");
    }
}

final class CSSString extends CSSValue {

    private final String value;

    CSSString(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        // TODO: escaping
        return "\"" + value + "\"";
    }
}
