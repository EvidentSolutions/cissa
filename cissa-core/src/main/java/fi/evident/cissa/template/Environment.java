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

import fi.evident.cissa.model.CSSValue;
import fi.evident.cissa.utils.Require;

import java.util.HashMap;
import java.util.Map;

public final class Environment {

    private final Environment parent;
    private final Map<String, CSSValue> bindings = new HashMap<String, CSSValue>();

    public Environment() {
        this(null);
    }

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public CSSValue lookup(String name) {
        Require.argumentNotNull("name", name);

        for (Environment env = this; env != null; env = env.parent) {
            CSSValue value = env.bindings.get(name);
            if (value != null)
                return value;
        }

        throw new UnboundVariableException(name);
    }

    public void bind(String name, CSSValue value) {
        Require.argumentNotNull("name", name);
        Require.argumentNotNull("value", value);
        
        bindings.put(name, value);
    }

    public void bind(VariableDefinition binding) {
        bind(binding.name, binding.exp.evaluate(this));
    }

    public Environment extend(Iterable<VariableDefinition> bindings) {
        Environment newEnv = new Environment(this);

        for (VariableDefinition binding : bindings)
            newEnv.bind(binding);

        return newEnv;
    }
}
