package org.blockchain_innovation.factom.identiy.did.parse;

import org.blockchain_innovation.factom.client.api.model.Chain;

/**
 * A rule that can work with Chain objects
 *
 * @param <T>
 */
public abstract class AbstractChainRule<T> extends AbstractEntryRule<T> {
    private final Chain chain;

    protected AbstractChainRule(Chain chain) {
        super(chain.getFirstEntry());
        this.chain = chain;
    }

    public Chain getChain() {
        return chain;
    }

    protected void assertChain() throws RuleException {
        if (chain == null) {
            throw new RuleException("A chain rule needs a chain input");
        }
    }

}
