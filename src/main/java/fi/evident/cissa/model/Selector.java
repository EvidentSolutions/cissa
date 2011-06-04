package fi.evident.cissa.model;

import fi.evident.cissa.utils.Require;

public final class Selector {
    private final String selector;

    public Selector(String selector) {
        Require.argumentNotNull("selector", selector);

        this.selector = selector;
    }

    @Override
    public String toString() {
        return selector;
    }
}
