package org.blockchain_innovation.factom.client.impl;

import org.blockchain_innovation.factom.client.api.BalanceApi;
import org.blockchain_innovation.factom.client.api.EntryApi;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.FactomdClient;
import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.response.factomd.FactoidBalanceResponse;

import java.util.concurrent.CompletableFuture;

public class BalanceApiImpl extends AbstractClient implements BalanceApi {

    private EntryApi entryApi;
    private FactomdClient factomdClient;

    private FactomdClient getFactomdClient() throws FactomException.ClientException {
        if (factomdClient == null) {
            throw new FactomException.ClientException("factomd client not provided");
        }
        return factomdClient;
    }

    public BalanceApiImpl setFactomdClient(FactomdClient factomdClient) {
        this.factomdClient = factomdClient;
        return this;
    }

    private EntryApi getEntryApi() throws FactomException.ClientException {
        if (entryApi == null) {
            throw new FactomException.ClientException("walletd client not provided");
        }
        return entryApi;
    }

    public BalanceApiImpl setEntryApi(EntryApi entryApi) {
        this.entryApi = entryApi;
        return this;
    }

    public CompletableFuture<FactomResponse<FactoidBalanceResponse>> getBalance(String hexAddress){
        Address address = new Address(hexAddress);
        return factomdClient.factoidBalance(address);
    }
}
