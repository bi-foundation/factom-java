package org.blockchain_innovation.factom.client.data.model.response.factomd;

public class FactoidSubmitResponse {

    private String message;
    private String txid;

    public String getMessage() {
        return message;
    }

    public String getTxId() {
        return txid;
    }
}
