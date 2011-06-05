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

package fi.evident.cissa.maven;

import fi.evident.cissa.Cissa;
import fi.evident.cissa.utils.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes Cissa-templates.
 *
 * @goal process-cissa-templates
 * @phase generate-resources
 */
public class ProcessCissaTemplatesMojo
    extends AbstractMojo
{

    /**
     * Single directory for extra files to include in the WAR. This is where
     * you place your JSP files.
     *
     * @parameter default-value="${basedir}/src/main/webapp"
     * @required
     */
    private File warSourceDirectory;

    /**
     * The directory where the webapp is built.
     *
     * @parameter default-value="${project.build.directory}/${project.build.finalName}"
     * @required
     */
    private File webappDirectory;

    /**
     * Encoding of Cissa files.
     *
     * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
     */
    private String encoding;

    public void execute() throws MojoExecutionException {
        for (File template : findCissaTemplates()) {
            String css = cissaProcess(template);
            writeFile(targetFor(template), css);
        }
    }

    private File targetFor(File template) {
        String originalPath = template.getAbsolutePath();
        String pathInTargetDir = originalPath.replace(warSourceDirectory.getAbsolutePath(), webappDirectory.getAbsolutePath());
        String finalPath = pathInTargetDir.replaceFirst("\\.cissa$", ".css");

        return new File(finalPath);
    }

    private String cissaProcess(File file) throws MojoExecutionException {
        String template = readFile(file);
        try {
            return Cissa.generate(template);
        } catch (Exception e) {
            throw new MojoExecutionException("Error when processing Cissa template " + file + ":" + e, e);
        }
    }

    private void writeFile(File file, String data) throws MojoExecutionException {
        try {
            file.getParentFile().mkdirs();
            IOUtils.writeFile(file, data, getCharset());
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write file: " + file, e);
        }
    }

    private String readFile(File file) throws MojoExecutionException {
        try {
            return IOUtils.readFileAsString(file, getCharset());
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to read file: " + file, e);
        }
    }

    private Charset getCharset() {
        return (encoding == null) ? Charset.defaultCharset() : Charset.forName(encoding);
    }

    private List<File> findCissaTemplates() {
        List<File> files = new ArrayList<File>();
        findCissaFiles(files, warSourceDirectory);
        return files;
    }

    private void findCissaFiles(List<File> result, File dir) {
        for (File child : dir.listFiles()) {
            if (child.isDirectory())
                findCissaFiles(result, child);
            else if (child.isFile() && child.getName().endsWith(".cissa"))
                result.add(child);
        }
    }
}
