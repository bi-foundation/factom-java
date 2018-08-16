package org.blockchain_innovation.factom.client.data.model.response.walletd;

import java.util.List;

public class TransactionsResponse {

    private List<TransactionResponse> transactions;

    public List<TransactionResponse> getTransactions() {
        return transactions;
    }
}
