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

import org.blockchain_innovation.factom.client.api.AddressType;
import org.blockchain_innovation.factom.client.api.FactomException;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.response.factomd.AdminBlockResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.ChainHeadResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.DirectoryBlockHeadResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.DirectoryBlockHeightResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.DirectoryBlockResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryBlockResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryCreditBalanceResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryCreditBlockResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryCreditRateResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.FactoidBalanceResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.FactoidBlockResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.FactoidSubmitResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.FactoidTransactionsResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.HeightsResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.PendingEntriesResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.PendingTransactionsResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.PropertiesResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RawDataResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.ReceiptResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.SendRawMessageResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.TransactionResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcMethod;

import java.util.concurrent.CompletableFuture;

public class FactomdClient extends AbstractClient {

    public CompletableFuture<FactomResponse<AdminBlockResponse>> adminBlockByHeight(long height) throws FactomException.ClientException {
        return exchange(RpcMethod.ADMIN_BLOCK_BY_HEIGHT.toRequestBuilder().param("height", height), AdminBlockResponse.class);
    }

    public CompletableFuture<FactomResponse<AdminBlockResponse>> adminBlockByKeyMerkleRoot(String keyMR) throws FactomException.ClientException {
        return exchange(RpcMethod.ADMIN_BLOCK_BY_KEYMR.toRequestBuilder().param("keymr", keyMR), AdminBlockResponse.class);
    }

    public <T> CompletableFuture<FactomResponse<T>> ackTransactions(String hash, String chainId, Class<T> rpcResultClass) throws FactomException.ClientException {
        return exchange(RpcMethod.ACK_TRANSACTION.toRequestBuilder().param("hash", hash).param("chainid", chainId), rpcResultClass);
    }

/*
    public CompletableFuture<FactomResponse<Map> ackFullTransaction(String fullMarshalledTransaction, String chainId) throws FactomException.ClientException {
        return exchange(RpcMethod.ACK_TRANSACTION.toRequestBuilder().param("fulltransaction", fullMarshalledTransaction).param("chainid", chainId), Map.class);
    }
*/

    public CompletableFuture<FactomResponse<FactoidTransactionsResponse>> ackFactoidTransactions(String hash) throws FactomException.ClientException {
        return ackTransactions(hash, "f", FactoidTransactionsResponse.class);
    }

    public CompletableFuture<FactomResponse<EntryTransactionResponse>> ackEntryTransactions(String hash) throws FactomException.ClientException {
        return ackTransactions(hash, "c", EntryTransactionResponse.class);
    }

    public CompletableFuture<FactomResponse<ChainHeadResponse>> chainHead(String chainId) throws FactomException.ClientException {
        return exchange(RpcMethod.CHAIN_HEAD.toRequestBuilder().param("chainid", chainId), ChainHeadResponse.class);
    }

    public CompletableFuture<FactomResponse<CommitChainResponse>> commitChain(String message) throws FactomException.ClientException {
        return exchange(RpcMethod.COMMIT_CHAIN.toRequestBuilder().param("message", message), CommitChainResponse.class);
    }

    public CompletableFuture<FactomResponse<CommitEntryResponse>> commitEntry(String message) throws FactomException.ClientException {
        return exchange(RpcMethod.COMMIT_ENTRY.toRequestBuilder().param("message", message), CommitEntryResponse.class);
    }

    public CompletableFuture<FactomResponse<DirectoryBlockHeightResponse>> directoryBlockByHeight(long height) throws FactomException.ClientException {
        return exchange(RpcMethod.DIRECTORY_BLOCK_BY_HEIGHT.toRequestBuilder().param("height", height), DirectoryBlockHeightResponse.class);
    }

    public CompletableFuture<FactomResponse<DirectoryBlockResponse>> directoryBlockByKeyMerkleRoot(String keyMR) throws FactomException.ClientException {
        return exchange(RpcMethod.DIRECTORY_BLOCK_BY_KEYMR.toRequestBuilder().param("keymr", keyMR), DirectoryBlockResponse.class);
    }

    public CompletableFuture<FactomResponse<DirectoryBlockHeadResponse>> directoryBlockHead() throws FactomException.ClientException {
        return exchange(RpcMethod.DIRECTORY_BLOCK_HEAD.toRequest(), DirectoryBlockHeadResponse.class);
    }

    public CompletableFuture<FactomResponse<EntryCreditBlockResponse>> entryCreditBlockByHeight(int height) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_CREDIT_BLOCK_BY_HEIGH.toRequestBuilder().param("height", height), EntryCreditBlockResponse.class);
    }

    public CompletableFuture<FactomResponse<EntryResponse>> entry(String entryHash) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY.toRequestBuilder().param("hash", entryHash), EntryResponse.class);
    }

    public CompletableFuture<FactomResponse<EntryBlockResponse>> entryBlockByKeyMerkleRoot(String keyMR) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_BLOCK_BY_KEYMR.toRequestBuilder().param("keymr", keyMR), EntryBlockResponse.class);
    }

    public CompletableFuture<FactomResponse<EntryCreditBalanceResponse>> entryCreditBalance(String address) throws FactomException.ClientException {
        AddressType.ENTRY_CREDIT_PUBLIC.assertValid(address);
        return exchange(RpcMethod.ENTRY_CREDIT_BALANCE.toRequestBuilder().param("address", address), EntryCreditBalanceResponse.class);
    }

    public CompletableFuture<FactomResponse<EntryCreditBlockResponse>> entryCreditBlock(String keymr) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_CREDIT_BLOCK.toRequestBuilder().param("keymr", keymr), EntryCreditBlockResponse.class);
    }

    public CompletableFuture<FactomResponse<EntryCreditRateResponse>> entryCreditRate() throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_CREDIT_RATE.toRequest(), EntryCreditRateResponse.class);
    }

    public CompletableFuture<FactomResponse<FactoidBalanceResponse>> factoidBalance(String address) throws FactomException.ClientException {
        AddressType.FACTOID_PUBLIC.assertValid(address);
        return exchange(RpcMethod.FACTOID_BALANCE.toRequestBuilder().param("address", address), FactoidBalanceResponse.class);
    }

    public CompletableFuture<FactomResponse<FactoidBlockResponse>> factoidBlock(String keymr) throws FactomException.ClientException {
        return exchange(RpcMethod.FACTOID_BLOCK.toRequestBuilder().param("keymr", keymr), FactoidBlockResponse.class);
    }

    public CompletableFuture<FactomResponse<FactoidSubmitResponse>> factoidSubmit(String transaction) throws FactomException.ClientException {
        return exchange(RpcMethod.FACTOID_SUBMIT.toRequestBuilder().param("transaction", transaction), FactoidSubmitResponse.class);
    }

    public CompletableFuture<FactomResponse<FactoidBlockResponse>> factoidBlockByHeight(int height) throws FactomException.ClientException {
        return exchange(RpcMethod.FACTOID_BLOCK_BY_HEIGHT.toRequestBuilder().param("height", height), FactoidBlockResponse.class);
    }

    public CompletableFuture<FactomResponse<HeightsResponse>> heights() throws FactomException.ClientException {
        return exchange(RpcMethod.HEIGHTS.toRequest(), HeightsResponse.class);
    }

    public CompletableFuture<FactomResponse<PendingEntriesResponse>> pendingEntries(int height) throws FactomException.ClientException {
        return exchange(RpcMethod.PENDING_ENTRIES.toRequest(), PendingEntriesResponse.class);
    }

    public CompletableFuture<FactomResponse<PendingTransactionsResponse>> pendingTransactions(int height) throws FactomException.ClientException {
        return exchange(RpcMethod.PENDING_TRANSACTONS.toRequestBuilder(), PendingTransactionsResponse.class);
    }

    public CompletableFuture<FactomResponse<PropertiesResponse>> properties() throws FactomException.ClientException {
        return exchange(RpcMethod.PROPERTIES.toRequest(), PropertiesResponse.class);
    }

    public CompletableFuture<FactomResponse<RawDataResponse>> rawData(String hash) throws FactomException.ClientException {
        return exchange(RpcMethod.RAW_DATA.toRequestBuilder().param("hash", hash), RawDataResponse.class);
    }

    public CompletableFuture<FactomResponse<ReceiptResponse>> receipt(String hash) throws FactomException.ClientException {
        return exchange(RpcMethod.RECEIPT.toRequestBuilder().param("hash", hash), ReceiptResponse.class);
    }

    public CompletableFuture<FactomResponse<RevealResponse>> revealChain(String entry) throws FactomException.ClientException {
        return exchange(RpcMethod.REVEAL_CHAIN.toRequestBuilder().param("entry", entry), RevealResponse.class);
    }

    public CompletableFuture<FactomResponse<RevealResponse>> revealEntry(String entry) throws FactomException.ClientException {
        return exchange(RpcMethod.REVEAL_ENTRY.toRequestBuilder().param("entry", entry), RevealResponse.class);
    }

    public CompletableFuture<FactomResponse<SendRawMessageResponse>> sendRawMessage(String message) throws FactomException.ClientException {
        return exchange(RpcMethod.SEND_RAW_MESSAGE.toRequestBuilder().param("message", message), SendRawMessageResponse.class);
    }

    public CompletableFuture<FactomResponse<TransactionResponse>> transaction(String hash) throws FactomException.ClientException {
        return exchange(RpcMethod.TRANSACTION.toRequestBuilder().param("hash", hash), TransactionResponse.class);
    }
}
