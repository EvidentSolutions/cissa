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
