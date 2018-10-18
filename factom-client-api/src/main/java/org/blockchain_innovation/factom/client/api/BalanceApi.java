package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.model.response.factomd.FactoidBalanceResponse;

import java.util.concurrent.CompletableFuture;

public interface BalanceApi {

    CompletableFuture<FactomResponse<FactoidBalanceResponse>> getBalance(String hexAddress);
}
