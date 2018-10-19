package org.blockchain_innovation.factom.client.api.model.FAT;

import java.io.Serializable;

public class TokenTransactionResponse implements Serializable {
    String txHash;

    public TokenTransactionResponse() {
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }
}
