package fi.evident.cissa.model;

import fi.evident.cissa.utils.Require;

import java.util.ArrayList;
import java.util.List;

import static fi.evident.cissa.utils.CollectionUtils.join;

public abstract class Selector {

    public static Selector simple(String selector, List<String> specs) {
        return new SimpleSelector(selector, specs);
    }

    public static Selector compound(Selector left, String combinator, Selector right) {
        return new CompoundSelector(left, combinator, right);
    }
}

final class SimpleSelector extends Selector {
    private final String elementName;
    private final List<String> specs;

    SimpleSelector(String elementName, List<String> specs) {
        Require.argumentNotNull("elementName", elementName);

        this.elementName = elementName;
        this.specs = new ArrayList<String>(specs);
    }

    @Override
    public String toString() {
        return elementName + join(specs);
    }
}

final class CompoundSelector extends Selector {
    private final Selector left;
    private final String combinator;
    private final Selector right;

    CompoundSelector(Selector left, String combinator, Selector right) {
        Require.argumentNotNull("left", left);
        Require.argumentNotNull("combinator", combinator);
        Require.argumentNotNull("right", right);

        this.combinator = combinator;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return combinator.isEmpty()
            ? left + " " + right
            : left + " " + combinator + " " + right;
    }
}
