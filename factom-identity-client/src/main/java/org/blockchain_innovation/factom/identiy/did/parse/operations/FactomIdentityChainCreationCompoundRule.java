package org.blockchain_innovation.factom.identiy.did.parse.operations;

import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.identiy.did.OperationValue;
import org.blockchain_innovation.factom.identiy.did.parse.*;
import org.factomprotocol.identity.did.model.IdentityEntry;

import java.util.Optional;

/**
 * A compound rule that checks everything related to a first entry to create an identity chain
 */
public class FactomIdentityChainCreationCompoundRule extends AbstractChainRule<IdentityEntry> {
    public FactomIdentityChainCreationCompoundRule(Chain chain) {
        super(chain);
    }

    @Override
    public IdentityEntry execute() throws RuleException {
        assertChain();
        assertEntry();

        new ExternalIdsSizeRule(getEntry(), Optional.of(2), Optional.empty()).execute();
        new AssertOperationRule(getEntry(), OperationValue.IDENTITY_CHAIN_CREATION).execute();
        ContentDeserializationRule<IdentityEntry> contentDeserializationRule = new ContentDeserializationRule<>(getEntry(), IdentityEntry.class);
        IdentityEntry factomIdentityChainContent = contentDeserializationRule.execute();

        return factomIdentityChainContent;
    }
}
