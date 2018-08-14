package org.blockchain_innovation.factom.client.data.model.response;

public class FactoidTransactionsResponse {

    private String txid;
    private long transactiondate;
    private String transactiondatestring;
    private long blockdate;
    private String blockdatestring;
    private String status;

    public String getTxId() {
        return txid;
    }

    public long getTransactionDate() {
        return transactiondate;
    }

    public String getTransactionDateString() {
        return transactiondatestring;
    }

    public long getBlockDate() {
        return blockdate;
    }

    public String getBlockDateString() {
        return blockdatestring;
    }

    public String getStatus() {
        return status;
    }
}
