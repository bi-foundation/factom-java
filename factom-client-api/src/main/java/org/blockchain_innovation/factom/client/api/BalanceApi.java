package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.model.FAT.TokenBalanceResponse;

import java.util.concurrent.CompletableFuture;

public interface BalanceApi {
    /**
     *
     *
     * @param hexAddress
     * @return List of Tokens
     */
    CompletableFuture<TokenBalanceResponse> getBalance(String hexAddress);

    BalanceApi setFactomdClient(FactomdClient factomdClient);
    BalanceApi setEntryApi(EntryApi entryApi);
}
