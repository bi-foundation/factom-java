package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.ops.Digests;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.factom_protocol.identifiers.did.model.UpdateRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateFactomDIDEntry extends FactomDIDEntry<UpdateRequest> {

    private final String fullKeyIdentifier;
    private final String signature;
    private final String chainId;

    public UpdateFactomDIDEntry(DIDVersion didVersion, String chainId, String fullKeyIdentifier, byte[] signature, String... additionalTags) {
        super(OperationValue.DID_UPDATE, didVersion, null,
                additionalTags.length == 0 ?
                        new String[]{Encoding.HEX.encode(signature)} :
                        Arrays.asList(Encoding.HEX.encode(signature), additionalTags).toArray(new String[]{}));
        this.chainId = chainId;
        this.fullKeyIdentifier = fullKeyIdentifier;
        this.signature = Encoding.HEX.encode(signature);
    }

    public UpdateFactomDIDEntry(Entry entry) {
        super(entry, UpdateRequest.class);
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

}
