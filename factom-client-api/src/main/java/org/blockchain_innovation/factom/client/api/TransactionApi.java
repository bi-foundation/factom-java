package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.model.FAT.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransactionApi {

    CompletableFuture<TokenTransactionResponse> sendToken(List<Inputs> inputs, List<Outputs> outputs, int milliTimestamp, String salt, List<String> externalIds);
}
