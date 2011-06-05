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
