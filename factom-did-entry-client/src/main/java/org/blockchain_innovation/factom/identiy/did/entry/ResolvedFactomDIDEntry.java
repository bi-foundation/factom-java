package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.identiy.did.DIDVersion;
import org.blockchain_innovation.factom.identiy.did.OperationValue;
import org.factomprotocol.identity.did.model.FactomDidContent;

import java.util.Arrays;

public class ResolvedFactomDIDEntry extends FactomDIDEntry<FactomDidContent> {
    private String nonce;

    public ResolvedFactomDIDEntry(DIDVersion didVersion, FactomDidContent content, String nonce, String... additionalTags) {
        super(OperationValue.DID_MANAGEMENT, didVersion, content, additionalTags.length == 0 ? new String[]{nonce} : Arrays.asList(nonce, additionalTags).toArray(new String[]{}));
        this.nonce = nonce;
    }

    public ResolvedFactomDIDEntry(Entry entry) {
        super(entry, FactomDidContent.class);
        this.nonce = entry.getExternalIds().get(2);
    }


    public String getNonce() {
        return nonce;
    }

    @Override
    public Entry toEntry() {
        return super.toEntry().setChainId(getChainId());
    }

    public String getChainId() {
        return Encoding.HEX.encode(ENTRY_OPS.calculateChainId(getExternalIds()));
    }
}
