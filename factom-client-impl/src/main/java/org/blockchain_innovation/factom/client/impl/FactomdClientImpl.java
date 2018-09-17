/*
 * Copyright 2018 Blockchain Innovation Foundation <https://blockchain-innovation.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.blockchain_innovation.factom.client.impl;

import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.FactomdClient;
import org.blockchain_innovation.factom.client.api.model.response.factomd.*;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.rpc.RpcMethod;

import java.util.concurrent.CompletableFuture;

public class FactomdClientImpl extends AbstractClient implements FactomdClient {

    @Override
    public CompletableFuture<FactomResponse<AdminBlockResponse>> adminBlockByHeight(long height) throws FactomException.ClientException {
        return exchange(RpcMethod.ADMIN_BLOCK_BY_HEIGHT.toRequestBuilder().param("height", height), AdminBlockResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<AdminBlockResponse>> adminBlockByKeyMerkleRoot(String keyMR) throws FactomException.ClientException {
        return exchange(RpcMethod.ADMIN_BLOCK_BY_KEYMR.toRequestBuilder().param("keymr", keyMR), AdminBlockResponse.class);
    }

    @Override
    public <T> CompletableFuture<FactomResponse<T>> ackTransactions(String hash, String chainId, Class<T> rpcResultClass) throws FactomException.ClientException {
        return exchange(RpcMethod.ACK_TRANSACTION.toRequestBuilder().param("hash", hash).param("chainid", chainId), rpcResultClass);
    }

/*
    public CompletableFuture<FactomResponse<Map> ackFullTransaction(String fullMarshalledTransaction, String chainId) throws FactomException.ClientException {
        return exchange(RpcMethod.ACK_TRANSACTION.toRequestBuilder().param("fulltransaction", fullMarshalledTransaction).param("chainid", chainId), Map.class);
    }
*/

    @Override
    public CompletableFuture<FactomResponse<FactoidTransactionsResponse>> ackFactoidTransactions(String txId) throws FactomException.ClientException {
        return ackTransactions(txId, "f", FactoidTransactionsResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<EntryTransactionResponse>> ackEntryTransactions(String hash) throws FactomException.ClientException {
        return ackTransactions(hash, "c", EntryTransactionResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<ChainHeadResponse>> chainHead(String chainId) throws FactomException.ClientException {
        return exchange(RpcMethod.CHAIN_HEAD.toRequestBuilder().param("chainid", chainId), ChainHeadResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<CommitChainResponse>> commitChain(String message) throws FactomException.ClientException {
        return exchange(RpcMethod.COMMIT_CHAIN.toRequestBuilder().param("message", message), CommitChainResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<CommitEntryResponse>> commitEntry(String message) throws FactomException.ClientException {
        return exchange(RpcMethod.COMMIT_ENTRY.toRequestBuilder().param("message", message), CommitEntryResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<DirectoryBlockHeightResponse>> directoryBlockByHeight(long height) throws FactomException.ClientException {
        return exchange(RpcMethod.DIRECTORY_BLOCK_BY_HEIGHT.toRequestBuilder().param("height", height), DirectoryBlockHeightResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<DirectoryBlockResponse>> directoryBlockByKeyMerkleRoot(String keyMR) throws FactomException.ClientException {
        return exchange(RpcMethod.DIRECTORY_BLOCK_BY_KEYMR.toRequestBuilder().param("keymr", keyMR), DirectoryBlockResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<DirectoryBlockHeadResponse>> directoryBlockHead() throws FactomException.ClientException {
        return exchange(RpcMethod.DIRECTORY_BLOCK_HEAD.toRequest(), DirectoryBlockHeadResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<EntryCreditBlockResponse>> entryCreditBlockByHeight(int height) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_CREDIT_BLOCK_BY_HEIGH.toRequestBuilder().param("height", height), EntryCreditBlockResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<EntryResponse>> entry(String entryHash) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY.toRequestBuilder().param("hash", entryHash), EntryResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<EntryBlockResponse>> entryBlockByKeyMerkleRoot(String keyMR) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_BLOCK_BY_KEYMR.toRequestBuilder().param("keymr", keyMR), EntryBlockResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<EntryCreditBalanceResponse>> entryCreditBalance(String entryCreditAddress) throws FactomException.ClientException {
        AddressType.ENTRY_CREDIT_PUBLIC.assertValid(entryCreditAddress);
        return exchange(RpcMethod.ENTRY_CREDIT_BALANCE.toRequestBuilder().param("address", entryCreditAddress), EntryCreditBalanceResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<EntryCreditBlockResponse>> entryCreditBlock(String keymr) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_CREDIT_BLOCK.toRequestBuilder().param("keymr", keymr), EntryCreditBlockResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<EntryCreditRateResponse>> entryCreditRate() throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_CREDIT_RATE.toRequest(), EntryCreditRateResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<FactoidBalanceResponse>> factoidBalance(String factoidAddress) throws FactomException.ClientException {
        AddressType.FACTOID_PUBLIC.assertValid(factoidAddress);
        return exchange(RpcMethod.FACTOID_BALANCE.toRequestBuilder().param("address", factoidAddress), FactoidBalanceResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<FactoidBlockResponse>> factoidBlock(String keymr) throws FactomException.ClientException {
        return exchange(RpcMethod.FACTOID_BLOCK.toRequestBuilder().param("keymr", keymr), FactoidBlockResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<FactoidSubmitResponse>> factoidSubmit(String factoidTransaction) throws FactomException.ClientException {
        return exchange(RpcMethod.FACTOID_SUBMIT.toRequestBuilder().param("transaction", factoidTransaction), FactoidSubmitResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<FactoidBlockResponse>> factoidBlockByHeight(int height) throws FactomException.ClientException {
        return exchange(RpcMethod.FACTOID_BLOCK_BY_HEIGHT.toRequestBuilder().param("height", height), FactoidBlockResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<HeightsResponse>> heights() throws FactomException.ClientException {
        return exchange(RpcMethod.HEIGHTS.toRequest(), HeightsResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<PendingEntriesResponse>> pendingEntries(int height) throws FactomException.ClientException {
        return exchange(RpcMethod.PENDING_ENTRIES.toRequest(), PendingEntriesResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<PendingTransactionsResponse>> pendingTransactions(int height) throws FactomException.ClientException {
        return exchange(RpcMethod.PENDING_TRANSACTONS.toRequestBuilder(), PendingTransactionsResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<PropertiesResponse>> properties() throws FactomException.ClientException {
        return exchange(RpcMethod.PROPERTIES.toRequest(), PropertiesResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<RawDataResponse>> rawData(String hash) throws FactomException.ClientException {
        return exchange(RpcMethod.RAW_DATA.toRequestBuilder().param("hash", hash), RawDataResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<ReceiptResponse>> receipt(String hash) throws FactomException.ClientException {
        return exchange(RpcMethod.RECEIPT.toRequestBuilder().param("hash", hash), ReceiptResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<RevealResponse>> revealChain(String entry) throws FactomException.ClientException {
        return exchange(RpcMethod.REVEAL_CHAIN.toRequestBuilder().param("entry", entry), RevealResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<RevealResponse>> revealEntry(String entry) throws FactomException.ClientException {
        return exchange(RpcMethod.REVEAL_ENTRY.toRequestBuilder().param("entry", entry), RevealResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<SendRawMessageResponse>> sendRawMessage(String message) throws FactomException.ClientException {
        return exchange(RpcMethod.SEND_RAW_MESSAGE.toRequestBuilder().param("message", message), SendRawMessageResponse.class);
    }

    @Override
    public CompletableFuture<FactomResponse<TransactionResponse>> transaction(String hash) throws FactomException.ClientException {
        return exchange(RpcMethod.TRANSACTION.toRequestBuilder().param("hash", hash), TransactionResponse.class);
    }
}
