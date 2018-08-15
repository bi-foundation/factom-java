package org.blockchain_innovation.factom.client.data.model.response.factomd;

public class PendingEntriesResponse {

    private String entryhash;
    private String chainid;
    private String status;

    public String getEntryHash() {
        return entryhash;
    }

    public String getChainId() {
        return chainid;
    }

    public String getStatus() {
        return status;
    }
}
