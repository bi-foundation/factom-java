package org.blockchain_innovation.factom.client.data.model.response.walletd;

import java.util.List;

public class ExecutedTransactionResponse extends TransactionResponse {

    private long feespaid;

    public long getFeespaid() {
        return feespaid;
    }
}
