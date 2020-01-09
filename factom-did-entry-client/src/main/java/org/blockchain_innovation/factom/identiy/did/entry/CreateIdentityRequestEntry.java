package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.identiy.did.DIDVersion;
import org.blockchain_innovation.factom.identiy.did.IdAddressKeyOps;
import org.factomprotocol.identity.did.model.BlockInfo;
import org.factomprotocol.identity.did.model.CreateIdentityRequest;
import org.factomprotocol.identity.did.model.FactomKey;
import org.factomprotocol.identity.did.model.IdentityEntry;

import java.util.List;


public class CreateIdentityRequestEntry extends ResolvedFactomDIDEntry<IdentityEntry> {
    private List<String> tags;

    public CreateIdentityRequestEntry(CreateIdentityRequest identityRequest) {
        super(DIDVersion.FACTOM_IDENTITY_CHAIN, convert(identityRequest), identityRequest.getTags().get(0), identityRequest.getTags().size() == 1 ? new String[]{} : identityRequest.getTags().subList(1, identityRequest.getTags().size()).toArray(new String[]{}));
        this.tags = identityRequest.getTags();
        initValidationRules();
    }

    public CreateIdentityRequestEntry(Entry entry, BlockInfo blockInfo) {
        super(entry, IdentityEntry.class, blockInfo);
        this.didVersion = DIDVersion.FACTOM_IDENTITY_CHAIN;
        initValidationRules();
    }

    public List<String> getTags() {
        return tags;
    }

    public static IdentityEntry convert(CreateIdentityRequest identityChain) {
        IdentityEntry content = new IdentityEntry();
        content.setVersion(identityChain.getVersion());
        if (identityChain.getKeys() != null) {
            IdAddressKeyOps conversions = new IdAddressKeyOps();
            for (FactomKey factomKey : identityChain.getKeys()) {
                content.addKeysItem(conversions.toIdPubAddress(factomKey));
            }
        }
        return content;
    }
}
