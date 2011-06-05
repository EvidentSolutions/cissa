package fi.evident.cissa.template;

public class UnboundVariableException extends RuntimeException {
    public UnboundVariableException(String name) {
        super("unbound variable '" + name + "'");
    }
}
