package fi.evident.cissa.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

import static java.lang.Boolean.parseBoolean;

public class CissaFilter implements Filter {

    private WebProcessor processor;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        processor.execute((HttpServletRequest) request, (HttpServletResponse) response);
    }

    public void init(FilterConfig config) throws ServletException {
        processor = new WebProcessor(config.getServletContext());

        String cache = config.getInitParameter("cache");
        if (cache != null)
            processor.setCache(parseBoolean(cache));

        String inputCharset = config.getInitParameter("inputCharset");
        if (inputCharset != null)
            processor.setInputCharset(Charset.forName(inputCharset));
    }

    public void destroy() {
    }
}
