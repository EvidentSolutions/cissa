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

package fi.evident.cissa.template;

import fi.evident.cissa.model.Document;
import fi.evident.cissa.model.RuleSet;

import java.util.ArrayList;
import java.util.List;

public final class DocumentTemplate {

    private final List<VariableDefinition> variables;
    private final List<RuleSetTemplate> ruleSets;

    public DocumentTemplate(List<VariableDefinition> variables, List<RuleSetTemplate> ruleSets) {
        this.variables = new ArrayList<VariableDefinition>(variables);
        this.ruleSets = new ArrayList<RuleSetTemplate>(ruleSets);
    }

    public Document evaluate() {
        Environment env = new Environment();

        for (VariableDefinition var : variables)
            env.bind(var);

        Document doc = new Document();

        for (RuleSetTemplate template : ruleSets)
            for (RuleSet ruleSet : template.evaluate(env))
                doc.addRuleSet(ruleSet);

        return doc;
    }
}
