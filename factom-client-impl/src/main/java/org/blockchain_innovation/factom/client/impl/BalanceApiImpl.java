package org.blockchain_innovation.factom.client.impl;

import org.blockchain_innovation.factom.client.api.BalanceApi;
import org.blockchain_innovation.factom.client.api.EntryApi;
import org.blockchain_innovation.factom.client.api.FactomdClient;
import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.FAT.Token;
import org.blockchain_innovation.factom.client.api.model.FAT.TokenBalanceResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

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

    public CompletableFuture<TokenBalanceResponse> getBalance(String hexAddress) {
        Address address = new Address(hexAddress);
        CompletableFuture<TokenBalanceResponse> completableFuture = new CompletableFuture<>();
        completableFuture.complete(generateBalance());
        return completableFuture;
    }

    private TokenBalanceResponse generateBalance(){
        TokenBalanceResponse tokenBalanceResponse = new TokenBalanceResponse();
        tokenBalanceResponse.setBalance(generateTokens());
        return tokenBalanceResponse;
    }

    private List<Token> generateTokens(){
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int randomTokenAmount = (int) (Math.random() * 8 + 1);
        List<Token> tokenList = new ArrayList<>();

        for(int i = 0; i < randomTokenAmount; i++){
            StringBuilder stringBuilder = new StringBuilder();
            for(int j = 0; j < 3; j++){
                stringBuilder.append(alphabet.charAt((int) (Math.random() * 25)));
            }
            tokenList.add(new Token(stringBuilder.toString(), (long) ThreadLocalRandom.current().nextLong(0, 1000000)));
        }
        return tokenList;
    }

}
