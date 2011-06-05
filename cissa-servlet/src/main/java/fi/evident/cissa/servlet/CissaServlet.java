package fi.evident.cissa.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

import static java.lang.Boolean.parseBoolean;

public class CissaServlet extends HttpServlet {

    private WebProcessor processor;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processor.execute(req, resp);
    }

    @Override
    public void init() throws ServletException {
        processor = new WebProcessor(getServletContext());

        String cache = getInitParameter("cache");
        if (cache != null)
            processor.setCache(parseBoolean(cache));

        String inputCharset = getInitParameter("inputCharset");
        if (inputCharset != null)
            processor.setInputCharset(Charset.forName(inputCharset));
    }
}
