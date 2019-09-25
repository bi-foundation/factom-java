package org.blockchain_innovation.factom.identiy.did.parse;

import org.blockchain_innovation.factom.identiy.did.DIDVersion;

import java.util.EnumSet;
import java.util.Set;

/**
 * A generic rule that can act as a starting point for custom rules
 *
 * @param <T>
 */
public abstract class AbstractGenericRule<T> implements Rule<T> {
    public boolean supports(DIDVersion didVersion) {
        // Use a template method, so subclasses can easily adjust the returned methods supported (or override this method)
        return getSupportedMethods().contains(didVersion);
    }

    protected Set<DIDVersion> getSupportedMethods() {
        return EnumSet.of(DIDVersion.FACTOM_V1_JSON);
    }
}
