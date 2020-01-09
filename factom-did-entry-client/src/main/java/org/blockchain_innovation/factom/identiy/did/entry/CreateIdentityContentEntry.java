package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.identiy.did.DIDVersion;
import org.factomprotocol.identity.did.model.BlockInfo;
import org.factomprotocol.identity.did.model.IdentityEntry;

import java.util.List;


public class CreateIdentityContentEntry extends ResolvedFactomDIDEntry<IdentityEntry> {
    private int version;
    private List<String> keys;

    public CreateIdentityContentEntry(IdentityEntry content, String nonce, String... additionalTags) {
        super(DIDVersion.FACTOM_IDENTITY_CHAIN, content, nonce, additionalTags);
        this.version = content.getVersion();
        this.keys = content.getKeys();
        initValidationRules();
    }

    public CreateIdentityContentEntry(Entry entry, BlockInfo blockInfo) {
        super(entry, IdentityEntry.class, blockInfo);
        this.didVersion = DIDVersion.FACTOM_IDENTITY_CHAIN;
        this.version = getContent().getVersion();
        this.keys = getContent().getKeys();

        initValidationRules();
    }

    public int getVersion() {
        return version;
    }

    public List<String> getKeys() {
        return keys;
    }
}
