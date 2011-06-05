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

import fi.evident.cissa.model.Attribute;
import fi.evident.cissa.model.RuleSet;
import fi.evident.cissa.model.Selector;

import java.util.ArrayList;
import java.util.List;

public final class RuleSetTemplate {

    private final List<Selector> selectors;
    private final List<VariableDefinition> bindings;
    private final List<AttributeTemplate> attributes;
    private final List<RuleSetTemplate> children;

    public RuleSetTemplate(List<Selector> selectors, List<VariableDefinition> bindings, List<AttributeTemplate> attributes, List<RuleSetTemplate> children) {
        this.selectors = new ArrayList<Selector>(selectors);
        this.bindings = new ArrayList<VariableDefinition>(bindings);
        this.attributes = new ArrayList<AttributeTemplate>(attributes);
        this.children = new ArrayList<RuleSetTemplate>(children);
    }

    public List<RuleSet> evaluate(Environment env) {
        List<RuleSet> result = new ArrayList<RuleSet>();
        SelectorSet context = new SelectorSet(selectors);

        evaluate(result, context, env);

        return result;
    }

    private void evaluate(List<RuleSet> result, SelectorSet context, Environment env) {
        Environment newEnv = env.extend(bindings);

        if (!attributes.isEmpty())
            result.add(new RuleSet(context.selectors, evaluateAttributes(newEnv)));

        for (RuleSetTemplate child : children)
            child.evaluate(result, context.makeExtendedSet(child.selectors), newEnv);
    }

    private List<Attribute> evaluateAttributes(Environment env) {
        List<Attribute> result = new ArrayList<Attribute>(attributes.size());
        for (AttributeTemplate template : attributes)
            result.add(template.evaluate(env));
        return result;
    }

    private static final class SelectorSet {
        private final List<Selector> selectors;

        SelectorSet() {
            this.selectors = new ArrayList<Selector>();
        }

        SelectorSet(List<Selector> selectors) {
            this.selectors = selectors;
        }

        public SelectorSet makeExtendedSet(List<Selector> ss) {
            SelectorSet set = new SelectorSet();

            for (Selector s1 : selectors)
                for (Selector s2 : ss)
                    set.selectors.add(Selector.compound(s1, "", s2));

            return set;
        }
    }
}
