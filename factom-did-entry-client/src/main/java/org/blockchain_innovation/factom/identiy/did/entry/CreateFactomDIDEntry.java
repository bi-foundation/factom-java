package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.identiy.did.DIDVersion;
import org.factomprotocol.identity.did.model.FactomDidContent;


public class CreateFactomDIDEntry extends ResolvedFactomDIDEntry {
    public CreateFactomDIDEntry(DIDVersion didVersion, FactomDidContent content, String nonce, String... additionalTags) {
        super(didVersion, content, nonce, additionalTags);
    }

    public CreateFactomDIDEntry(Entry entry) {
        super(entry);
    }
}
