package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.identiy.did.DIDVersion;
import org.blockchain_innovation.factom.identiy.did.OperationValue;

import java.util.Arrays;

public class DeactivateFactomDIDEntry extends FactomDIDEntry<Void> {
    private final String fullKeyIdentifier;
    private final String signature;
    private final String chainId;

    public DeactivateFactomDIDEntry(DIDVersion didVersion, String chainId, String fullKeyIdentifier, byte[] signature, String... additionalTags) {
        super(OperationValue.DID_DEACTIVATION, didVersion, null,
                additionalTags.length == 0 ?
                        new String[]{Encoding.HEX.encode(signature)} :
                        Arrays.asList(Encoding.HEX.encode(signature), additionalTags).toArray(new String[]{}));
        this.chainId = chainId;
        this.fullKeyIdentifier = fullKeyIdentifier;
        this.signature = Encoding.HEX.encode(signature);
    }

    public DeactivateFactomDIDEntry(Entry entry) {
        super(entry, Void.class);
        this.chainId = entry.getChainId();
        this.fullKeyIdentifier = entry.getExternalIds().get(2);
        this.signature = entry.getExternalIds().get(3);
    }


    public String getFullKeyIdentifier() {
        return fullKeyIdentifier;
    }

    public String getSignature() {
        return signature;
    }

    public String getChainId() {
        return chainId;
    }

    @Override
    public Entry toEntry() {
        return new Entry().setContent(null).setExternalIds(getExternalIds());
    }
}
