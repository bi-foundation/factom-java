package org.blockchain_innovation.factom.identiy.did.parse;

import org.blockchain_innovation.factom.client.api.model.Entry;

/**
 * A rule that can check an entry
 *
 * @param <T>
 */
public abstract class AbstractEntryRule<T> extends AbstractGenericRule<T> {

    private final Entry entry;


    protected AbstractEntryRule(Entry entry) {
        this.entry = entry;
    }

    public Entry getEntry() {
        return entry;
    }

    protected void assertEntry() throws RuleException {
        if (entry == null) {
            throw new RuleException("An entry rule needs an entry input");
        }
    }

}
