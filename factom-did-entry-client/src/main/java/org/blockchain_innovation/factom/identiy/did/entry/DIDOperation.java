package org.blockchain_innovation.factom.identiy.did.entry;

import java.util.List;

public abstract class DIDOperation {

    protected final DIDVersion didVersion;
    protected final byte[] nonce;
    protected final String keyId;

    protected DIDOperation(DIDVersion didVersion, byte[] nonce) {
        this(didVersion, null, nonce);
    }

    protected DIDOperation(DIDVersion didVersion, String keyId) {
        this(didVersion, keyId, null);
    }

    protected DIDOperation(DIDVersion didVersion, String keyId, byte[] nonce) {
        this.didVersion = didVersion;
        this.nonce = nonce;
        this.keyId = keyId;
    }

    public abstract List<String> externalIds();

}
