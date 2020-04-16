package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.identiy.did.DIDVersion;
import org.blockchain_innovation.factom.identiy.did.OperationValue;
import org.factomprotocol.identity.did.model.BlockInfo;
import org.factomprotocol.identity.did.model.UpdateRequest;

import java.util.Arrays;

public class UpdateFactomDIDEntry extends AbstractFactomIdentityEntry<UpdateRequest> {

    private final String fullKeyIdentifier;
    private final String signature;

    public UpdateFactomDIDEntry(DIDVersion didVersion, String chainId, String fullKeyIdentifier, byte[] signature, String... additionalTags) {
        super(OperationValue.DID_UPDATE, didVersion, null,
                additionalTags.length == 0 ?
                        new String[]{Encoding.HEX.encode(signature)} :
                        Arrays.asList(Encoding.HEX.encode(signature), additionalTags).toArray(new String[]{}));
        this.chainId = chainId;
        this.fullKeyIdentifier = fullKeyIdentifier;
        this.signature = Encoding.HEX.encode(signature);
        initValidationRules();
    }

    public UpdateFactomDIDEntry(Entry entry, BlockInfo blockInfo) {
        super(entry, UpdateRequest.class, blockInfo);
        this.fullKeyIdentifier = entry.getExternalIds().get(2);
        this.signature = entry.getExternalIds().get(3);
        initValidationRules();
    }


    public String getFullKeyIdentifier() {
        return fullKeyIdentifier;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public void initValidationRules() {
        throw new RuntimeException("FIXME");
    }
}
