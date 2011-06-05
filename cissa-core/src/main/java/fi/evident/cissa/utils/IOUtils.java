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

package fi.evident.cissa.utils;

import java.io.*;
import java.nio.charset.Charset;

public final class IOUtils {

    public static String readFileAsString(String file, Charset charset) throws IOException {
        return readFileAsString(new File(file), charset);
    }

    public static String readFileAsString(File file, Charset charset) throws IOException {
        return readInputStreamAsString(new FileInputStream(file), charset);
    }

    public static String readInputStreamAsString(InputStream in, Charset charset) throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            Reader reader = new InputStreamReader(in, charset);
            char[] buffer = new char[1024];
            int n;

            while ((n = reader.read(buffer)) != -1)
                sb.append(buffer, 0, n);

            return sb.toString();

        } finally {
            in.close();
        }
    }
}
