package org.blockchain_innovation.factom.client.data.model.response.factomd;

public class RevealResponse {

    private String message;
    private String entryhash;
    private String chainid;

    public String getMessage() {
        return message;
    }

    public String getEntryHash() {
        return entryhash;
    }

    public String getChainId() {
        return chainid;
    }
}
