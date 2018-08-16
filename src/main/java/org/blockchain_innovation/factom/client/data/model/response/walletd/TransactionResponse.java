package org.blockchain_innovation.factom.client.data.model.response.walletd;

import java.util.List;

public class TransactionResponse extends AbstractTransactionResponse {

    private long feesrequired;
    private String txid;

    public long getFeesRequired() {
        return feesrequired;
    }

    public String getTxId() {
        return txid;
    }
}
