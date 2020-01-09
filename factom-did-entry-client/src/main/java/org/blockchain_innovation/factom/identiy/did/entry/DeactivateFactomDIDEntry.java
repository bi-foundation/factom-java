package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.identiy.did.DIDVersion;
import org.blockchain_innovation.factom.identiy.did.OperationValue;
import org.factomprotocol.identity.did.model.BlockInfo;

import java.util.Arrays;
import java.util.Optional;

public class DeactivateFactomDIDEntry extends AbstractFactomIdentityEntry<Void> {
    private final String fullKeyIdentifier;
    private final String signature;

    public DeactivateFactomDIDEntry(DIDVersion didVersion, String chainId, String fullKeyIdentifier, byte[] signature, String... additionalTags) {
        super(OperationValue.DID_DEACTIVATION, didVersion, null,
                additionalTags.length == 0 ?
                        new String[]{Encoding.HEX.encode(signature)} :
                        Arrays.asList(Encoding.HEX.encode(signature), additionalTags).toArray(new String[]{}));
        this.chainId = chainId;
        this.fullKeyIdentifier = fullKeyIdentifier;
        this.signature = Encoding.HEX.encode(signature);
        initValidationRules();
    }

    public DeactivateFactomDIDEntry(Entry entry, BlockInfo blockInfo) {
        super(entry, Void.class, blockInfo);
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
    public Entry toEntry(Optional<String> chainId) {
        return new Entry().setChainId(chainId.orElse(this.chainId)).setContent(null).setExternalIds(getExternalIds());
    }

    @Override
    public void initValidationRules() {
        throw new RuntimeException("FIXME");
    }
}
