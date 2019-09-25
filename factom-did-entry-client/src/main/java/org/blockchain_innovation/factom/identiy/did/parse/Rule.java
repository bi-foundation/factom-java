package org.blockchain_innovation.factom.identiy.did.parse;

import org.blockchain_innovation.factom.identiy.did.DIDVersion;

/**
 * Chose a command like design pattern. A visitor pattern is more efficient probably, but also more complex for most people.
 * With not all moving parts known yet and a big amount of objects and hierarchies don't want to couple it too much at this time
 *
 * @param <T> The result type of the execute method
 */
public interface Rule<T> {

    /**
     * Returns whether a rule supports a certain version of DIDs
     *
     * @param didVersion The DID version
     * @return Whether the rules supports checks for the respective version
     */
    boolean supports(DIDVersion didVersion);

    /**
     * Executes the validation logic of the rule in question
     *
     * @return The object type for this rule
     * @throws RuleException Whenever a rule does not pas validation
     */
    T execute() throws RuleException;
}
