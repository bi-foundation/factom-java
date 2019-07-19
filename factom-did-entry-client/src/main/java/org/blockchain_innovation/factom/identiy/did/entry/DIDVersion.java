package org.blockchain_innovation.factom.identiy.did.entry;

import did.DID;
import did.DIDURL;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;

public enum DIDVersion {
    FACTOM_V1("factom", "1.0.0");

    private static final EntryOperations ENTRY_OPS = new EntryOperations();

    private final String method;
    private final String protocolVersion;


    DIDVersion(String method, String protocolVersion) {
        this.method = method;
        this.protocolVersion = protocolVersion;
    }

    public void assertFactomMethod(String didUrl) {
        if (!method.equals(getDid(didUrl).getMethod())) {
            throw new DIDRuntimeException("Method of DID URL is not supported by this version of Factom DIDs: " + didUrl);
        }
    }

    public String getMethodSpecificId(String didUrl) {
       return getDid(didUrl).getMethodSpecificId();
    }

    public DIDURL getDidUrl(String didUrl) {
       return DIDURL.fromString(didUrl);
    }

    public DID getDid(String didUrl) {
        return getDidUrl(didUrl).getDid();
    }


    public String determineChainId(String nonce, Encoding encoding) {
        return determineChainId(encoding.decode(nonce));
    }


    public String determineChainId(byte[] nonce) {
        return Encoding.HEX.encode(ENTRY_OPS.calculateChainId(new CreateDIDOperation(this, nonce).externalIds()));
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
