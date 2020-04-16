package org.blockchain_innovation.factom.identiy.did.parse;

import org.blockchain_innovation.factom.client.api.model.Entry;

import java.util.List;
import java.util.Optional;

/**
 * A rule that checks whether the amount of external Ids on an entry is between an optional min and optional max (included)
 */
public class ExternalIdsSizeRule extends AbstractEntryRule<Integer> {

    private final Optional<Integer> min;
    private final Optional<Integer> max;

    public ExternalIdsSizeRule(Entry entry, Optional<Integer> min, Optional<Integer> max) {
        super(entry);
        this.min = min;
        this.max = max;
    }

    public ExternalIdsSizeRule(Entry entry, Optional<Integer> min) {
        this(entry, min, Optional.empty());
    }

    @Override
    public Integer execute() throws RuleException {
        assertEntry();
        int size = 0;
        List<String> extIds = getEntry().getExternalIds();
        if (extIds != null) {
            size = extIds.size();
        }
        if (min.isPresent() && size < min.get()) {
            throw new RuleException("Amount of externalIds present '%d' in entry is smaller than minimum amount of '%d'", size, min.get());
        } else if (max.isPresent() && size > max.get()) {
            throw new RuleException("Amount of externalIds present '%d' in entry is bigger than maximum amount of '%d'", size, max.get());
        }
        return size;
    }

    public Optional<Integer> getMin() {
        return min;
    }

    public Optional<Integer> getMax() {
        return max;
    }
}
