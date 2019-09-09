package org.blockchain_innovation.factom.identiy.did.parse;

/**
 * A rule exception denotes a rule not passing validation
 */
public class RuleException extends Exception {
    public RuleException(String message) {
        super(message);
    }

    public RuleException(String message, Object... arguments) {
        this(String.format(message, arguments));
    }

    public RuleException(Exception e) {
        super(e);
    }
}
