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

package fi.evident.cissa.servlet;

import fi.evident.cissa.Cissa;
import fi.evident.cissa.utils.Require;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static fi.evident.cissa.utils.IOUtils.readInputStreamAsString;

final class WebProcessor {
    
    private final ServletContext servletContext;
    private final Map<String,String> cache = new ConcurrentHashMap<String, String>();
    private Charset inputCharset = Charset.forName("UTF-8");
    private boolean caching = true;

    WebProcessor(ServletContext servletContext) {
        Require.argumentNotNull("servletContext", servletContext);

        this.servletContext = servletContext;
    }

    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getServletPath().replace(".css", ".cissa");

        String resp = getResponse(path);
        if (resp == null) {
            response.setStatus(404);
        } else {
            response.setContentType("text/css");
            response.getWriter().write(resp);
        }
    }

    private String getResponse(String path) throws IOException {
        if (caching)
            return getCachedResponse(path);
        else
            return getUncachedResponse(path);
    }

    private String getCachedResponse(String path) throws IOException {
        String response = cache.get(path);
        if (response == null) {
            response = getUncachedResponse(path);
            if (response != null)
                cache.put(path, response);
        }
        return response;
    }

    private String getUncachedResponse(String path) throws IOException {
        String resource = readResourceAsString(path);
        if (resource == null)
            return Cissa.generate(resource);
        else    
            return null;
    }

    public void setCache(boolean caching) {
        this.caching = caching;
    }

    public void setInputCharset(Charset inputCharset) {
        Require.argumentNotNull("inputCharset", inputCharset);
        
        this.inputCharset = inputCharset;
    }

    private String readResourceAsString(String path) throws IOException {
        InputStream in = servletContext.getResourceAsStream(path);
        if (in != null) {
            return readInputStreamAsString(in, inputCharset);
        } else {
            return null;
        }
    }
}
