package org.blockchain_innovation.factom.client.data.model.response.walletd;

import java.util.List;

public class BlockHeightTransactionsResponse {

    private List<BlockHeightTransactionResponse> transactions;

    public List<BlockHeightTransactionResponse> getTransactions() {
        return transactions;
    }
}
