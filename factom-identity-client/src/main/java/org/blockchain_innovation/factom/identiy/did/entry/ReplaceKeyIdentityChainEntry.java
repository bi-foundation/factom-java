package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.identiy.did.DIDVersion;
import org.blockchain_innovation.factom.identiy.did.OperationValue;
import org.factomprotocol.identity.did.model.BlockInfo;
import org.factomprotocol.identity.did.model.ReplaceKeyEntry;

import java.util.Arrays;

public class ReplaceKeyIdentityChainEntry extends AbstractFactomIdentityEntry<ReplaceKeyEntry> {

    private final String oldKey;
    private final String newKey;
    private final String signature;
    private final String signerKey;


    public ReplaceKeyIdentityChainEntry(String chainId, String oldKey, String newKey, byte[] signature, String signerKey) {
        super(OperationValue.IDENTITY_CHAIN_REPLACE_KEY, DIDVersion.FACTOM_IDENTITY_CHAIN, null,
                Arrays.asList(oldKey, newKey, Encoding.HEX.encode(signature), signerKey).toArray(new String[]{}));
        this.chainId = chainId;
        this.oldKey = oldKey;
        this.newKey = newKey;
        this.signature = Encoding.HEX.encode(signature);
        this.signerKey = signerKey;
        initValidationRules();
    }

    public ReplaceKeyIdentityChainEntry(Entry entry, BlockInfo blockInfo) {
        super(entry, ReplaceKeyEntry.class, blockInfo);
        this.oldKey = entry.getExternalIds().get(1);
        this.newKey = entry.getExternalIds().get(2);
        this.signature = entry.getExternalIds().get(3);
        this.signerKey = entry.getExternalIds().get(4);
        this.didVersion = DIDVersion.FACTOM_IDENTITY_CHAIN;
        initValidationRules();
    }


    public String getOldKey() {
        return oldKey;
    }

    public String getSignature() {
        return signature;
    }

    public String getNewKey() {
        return newKey;
    }

    public String getSignerKey() {
        return signerKey;
    }

    @Override
    public void initValidationRules() {
//        throw new RuntimeException("FIXME");
    }
}
