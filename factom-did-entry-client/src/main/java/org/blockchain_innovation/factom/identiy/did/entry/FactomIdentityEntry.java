package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.identiy.did.DIDVersion;
import org.blockchain_innovation.factom.identiy.did.OperationValue;
import org.blockchain_innovation.factom.identiy.did.parse.RuleException;
import org.factomprotocol.identity.did.model.BlockInfo;

import java.util.List;
import java.util.Optional;

public interface FactomIdentityEntry<T> {
    List<String> getExternalIds();

    T getContent();

    OperationValue getOperationValue();

    Entry toEntry(Optional<String> chainId);

    void validate() throws RuleException;

    DIDVersion getDidVersion();

    String getChainId();

    Optional<BlockInfo> getBlockInfo();
}
