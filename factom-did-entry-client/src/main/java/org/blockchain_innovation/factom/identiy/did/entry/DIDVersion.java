package org.blockchain_innovation.factom.identiy.did.entry;

import did.DID;
import did.DIDURL;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;

public enum DIDVersion {
    FACTOM_V1_JSON("factom", "1.0.0", "0.2.0");

    private static final EntryOperations ENTRY_OPS = new EntryOperations();

    private final String method;
    private final String protocolVersion;
    private final String schemaVersion;


    DIDVersion(String method, String protocolVersion, String schemaVersion) {
        this.method = method;
        this.protocolVersion = protocolVersion;
        this.schemaVersion = schemaVersion;
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

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
