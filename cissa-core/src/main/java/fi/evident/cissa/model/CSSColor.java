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

import static java.lang.Math.max;
import static java.lang.Math.min;

public final class CSSColor extends CSSValue {

    private final double r;
    private final double g;
    private final double b;

    public CSSColor(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getR() {
        return (int) r;
    }

    public int getG() {
        return (int) g;
    }

    public int getB() {
        return (int) b;
    }

    public CSSColor add(CSSColor c) {
        return new CSSColor(r + c.r, g + c.g, b + c.b);
    }

    public static CSSColor parse(String s) {
        int rgb = Integer.parseInt(normalize(s), 16);
        return new CSSColor((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
    }

    private static String normalize(String s) {
        if (s.startsWith("#") && s.length() == 7)
            return s.substring(1);
        else if (s.startsWith("#") && s.length() == 4)
            return new String(new char[] { s.charAt(1), s.charAt(1), s.charAt(2), s.charAt(2), s.charAt(3), s.charAt(3) });
        else
            throw new IllegalArgumentException("invalid number: " + s);
    }

    @Override
    void writeTo(CSSWriter writer) {
        writer.write("#").writeHexByte(clamp(getR())).writeHexByte(clamp(getG())).writeHexByte(clamp(getB()));
    }

    private static int clamp(int c) {
        return min(max(c, 0), 255);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof CSSColor) {
            CSSColor c = (CSSColor) o;

            return r == c.r && g == c.g && b == c.b;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (int) (((r * 79) + g) * 7 + b);
    }
}
