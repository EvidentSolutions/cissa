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

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

public class IOUtilsTest {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Test
    public void readInputStreamAsStringReturnsCorrectString() throws IOException {
        String str = "\u00c1guas de Mar\u00e7o";
        InputStream in = new MockInputStream(str, UTF8);

        assertEquals(str, IOUtils.readInputStreamAsString(in, UTF8));
    }

    @Test
    public void readInputStreamAsStringClosesInputStreamAfterSuccess() throws IOException {
        MockInputStream in = new MockInputStream("foo", UTF8);

        IOUtils.readInputStreamAsString(in, UTF8);

        assertTrue("closed", in.closed);
    }

    @Test
    public void readInputStreamAsStringClosesInputStreamAfterFailure() throws IOException {
        FailingInputStream in = new FailingInputStream();
        try {
            IOUtils.readInputStreamAsString(in, UTF8);
            fail("Expected failure");
        } catch (IOException e) {
            assertSame("exception", in.exception, e);
            assertTrue("closed", in.closed);
        }
    }

    private static class MockInputStream extends ByteArrayInputStream {

        boolean closed = false;

        public MockInputStream(String str, Charset charset) {
            super(str.getBytes(charset));
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }

    private static class FailingInputStream extends InputStream {
        IOException exception = new IOException("failed");
        boolean closed = false;

        @Override
        public int read() throws IOException {
            throw exception;
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }
}
