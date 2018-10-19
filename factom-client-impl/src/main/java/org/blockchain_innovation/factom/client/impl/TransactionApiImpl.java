package org.blockchain_innovation.factom.client.impl;

import org.blockchain_innovation.factom.client.api.TransactionApi;
import org.blockchain_innovation.factom.client.api.model.FAT.*;
import org.blockchain_innovation.factom.client.api.ops.Digests;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TransactionApiImpl extends AbstractClient implements TransactionApi {

    public CompletableFuture<TokenTransactionResponse> sendToken(List<Inputs> inputs, List<Outputs> outputs, int milliTimestamp, String salt, List<String> externalIds) {
        TokenTransaction.Builder tokenTransaction = new TokenTransaction.Builder();
        tokenTransaction
                .setExternalIds(externalIds)
                .setInputs(inputs)
                .setOutputs(outputs)
                .setMilliTimestamp(milliTimestamp)
                .setSalt(salt)
                .build();
        return generateTokenTransactionResponse();
    }

    private CompletableFuture<TokenTransactionResponse> generateTokenTransactionResponse() {
        TokenTransactionResponse tokenTransactionResponse = new TokenTransactionResponse();
        tokenTransactionResponse.setTxHash(generateHash());
        CompletableFuture<TokenTransactionResponse> tokenTransactionCompletableFuture = new CompletableFuture<>();
        tokenTransactionCompletableFuture.complete(tokenTransactionResponse);
        return tokenTransactionCompletableFuture;
    }

    private String generateHash() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder stringBuilder = new StringBuilder();

        for (int j = 0; j < 32; j++) {
            stringBuilder.append(alphabet.charAt((int) (Math.random() * 25)));
        }
        String hash =  Arrays.toString(Digests.SHA_256.digest(stringBuilder.toString()));
        return null;
    }
}
