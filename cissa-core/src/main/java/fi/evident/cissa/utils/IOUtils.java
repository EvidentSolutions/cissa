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
