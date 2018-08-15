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

package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.data.FactomException;
import org.blockchain_innovation.factom.client.data.model.response.factomd.AdminBlockResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.ChainHeadResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.DirectoryBlockHeadResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.DirectoryBlockResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.EntryBlockResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.EntryCreditBalanceResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.EntryCreditBlockResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.EntryCreditRateResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.EntryResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.EntryTransactionsResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.FactoidBalanceResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.FactoidBlockResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.FactoidSubmitResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.FactoidTransactionsResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.HeightsResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.PendingEntriesResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.PendingTransactionsResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.PropertiesResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.RawDataResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.ReceiptResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.SendRawMessageResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.TransactionResponse;
import org.blockchain_innovation.factom.client.data.model.rpc.RpcMethod;

public class FactomdClient extends AbstractClient {

    public FactomResponse<AdminBlockResponse> adminBlockByHeight(long height) throws FactomException.ClientException {
        return exchange(RpcMethod.ADMIN_BLOCK_BY_HEIGHT.toRequestBuilder().param("height", height), AdminBlockResponse.class);
    }

    public FactomResponse<AdminBlockResponse> adminBlockByKeyMerkleRoot(String keyMR) throws FactomException.ClientException {
        return exchange(RpcMethod.ADMIN_BLOCK_BY_KEYMR.toRequestBuilder().param("keymr", keyMR), AdminBlockResponse.class);
    }

    public <T> FactomResponse<T> ackTransactions(String hash, String chainId, Class<T> rpcResultClass) throws FactomException.ClientException {
        return exchange(RpcMethod.ACK_TRANSACTION.toRequestBuilder().param("hash", hash).param("chainid", chainId), rpcResultClass);
    }

/*
    public FactomResponse<Map> ackFullTransaction(String fullMarshalledTransaction, String chainId) throws FactomException.ClientException {
        return exchange(RpcMethod.ACK_TRANSACTION.toRequestBuilder().param("fulltransaction", fullMarshalledTransaction).param("chainid", chainId), Map.class);
    }
*/

    public FactomResponse<FactoidTransactionsResponse> ackFactoidTransactions(String hash) throws FactomException.ClientException {
        return ackTransactions(hash, "f", FactoidTransactionsResponse.class);
    }

    public FactomResponse<EntryTransactionsResponse> ackEntryTransactions(String hash) throws FactomException.ClientException {
        return ackTransactions(hash, "c", EntryTransactionsResponse.class);
    }

    public FactomResponse<ChainHeadResponse> chainHead(String chainId) throws FactomException.ClientException {
        return exchange(RpcMethod.CHAIN_HEAD.toRequestBuilder().param("chainid", chainId), ChainHeadResponse.class);
    }

    public FactomResponse<CommitChainResponse> commitChain(String message) throws FactomException.ClientException {
        return exchange(RpcMethod.COMMIT_CHAIN.toRequestBuilder().param("message", message), CommitChainResponse.class);
    }

    public FactomResponse<CommitEntryResponse> commitEntry(String message) throws FactomException.ClientException {
        return exchange(RpcMethod.COMMIT_ENTRY.toRequestBuilder().param("message", message), CommitEntryResponse.class);
    }

    public FactomResponse<DirectoryBlockResponse> directoryBlockByHeight(long height) throws FactomException.ClientException {
        return exchange(RpcMethod.DIRECTORY_BLOCK_BY_HEIGHT.toRequestBuilder().param("height", height), DirectoryBlockResponse.class);
    }

    public FactomResponse<DirectoryBlockResponse> directoryBlockByKeyMerkleRoot(String keyMR) throws FactomException.ClientException {
        return exchange(RpcMethod.DIRECTORY_BLOCK_BY_KEYMR.toRequestBuilder().param("keymr", keyMR), DirectoryBlockResponse.class);
    }

    public FactomResponse<DirectoryBlockHeadResponse> directoryBlockHead() throws FactomException.ClientException {
        return exchange(RpcMethod.DIRECTORY_BLOCK_HEAD.toRequest(), DirectoryBlockHeadResponse.class);
    }

    public FactomResponse<EntryCreditBlockResponse> entryCreditBlockByHeight(int height) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_CREDIT_BLOCK_BY_HEIGH.toRequestBuilder().param("height", height), EntryCreditBlockResponse.class);
    }

    public FactomResponse<EntryResponse> entry(String entryHash) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY.toRequestBuilder().param("hash", entryHash), EntryResponse.class);
    }

    public FactomResponse<EntryBlockResponse> entryBlockByKeyMerkleRoot(String keyMR) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_BLOCK_BY_KEYMR.toRequestBuilder().param("keymr", keyMR), EntryBlockResponse.class);
    }

    public FactomResponse<EntryCreditBalanceResponse> entryCreditBalance(String address) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_CREDIT_BALANCE.toRequestBuilder().param("address", address), EntryCreditBalanceResponse.class);
    }

    public FactomResponse<EntryCreditBlockResponse> entryCreditBlock(String keymr) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_CREDIT_BLOCK.toRequestBuilder().param("keymr", keymr), EntryCreditBlockResponse.class);
    }

    public FactomResponse<EntryCreditRateResponse> entryCreditRate() throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_CREDIT_RATE.toRequestBuilder(), EntryCreditRateResponse.class);
    }

    public FactomResponse<FactoidBalanceResponse> factoidBalance(String address) throws FactomException.ClientException {
        return exchange(RpcMethod.FACTOID_BALANCE.toRequestBuilder().param("address", address), FactoidBalanceResponse.class);
    }

    public FactomResponse<FactoidBlockResponse> factoidBlock(String keymr) throws FactomException.ClientException {
        return exchange(RpcMethod.FACTOID_BLOCK.toRequestBuilder().param("keymr", keymr), FactoidBlockResponse.class);
    }

    public FactomResponse<FactoidSubmitResponse> factoidSubmit(String transaction) throws FactomException.ClientException {
        return exchange(RpcMethod.FACTOID_SUBMIT.toRequestBuilder().param("transaction", transaction), FactoidSubmitResponse.class);
    }

    public FactomResponse<FactoidBlockResponse> factoidBlockByHeight(int height) throws FactomException.ClientException {
        return exchange(RpcMethod.FACTOID_BLOCK_BY_HEIGHT.toRequestBuilder().param("height", height), FactoidBlockResponse.class);
    }

    public FactomResponse<HeightsResponse> heights() throws FactomException.ClientException {
        return exchange(RpcMethod.HEIGHTS.toRequestBuilder(), HeightsResponse.class);
    }

    /* toRequestBuilder or toRequest....
    public FactomResponse<HeightsResponse> heights() throws FactomException.ClientException {
        return exchange(RpcMethod.HEIGHTS.toRequest(), HeightsResponse.class);
    }
    */

    public FactomResponse<PendingEntriesResponse> pendingEntries(int height) throws FactomException.ClientException {
        return exchange(RpcMethod.PENDING_ENTRIES.toRequestBuilder(), PendingEntriesResponse.class);
    }

    public FactomResponse<PendingTransactionsResponse> pendingTransactions(int height) throws FactomException.ClientException {
        return exchange(RpcMethod.PENDING_TRANSACTONS.toRequestBuilder(), PendingTransactionsResponse.class);
    }

    public FactomResponse<PropertiesResponse> properties() throws FactomException.ClientException {
        return exchange(RpcMethod.PROPERTIES.toRequestBuilder(), PropertiesResponse.class);
    }

    public FactomResponse<RawDataResponse> rawData(String hash) throws FactomException.ClientException {
        return exchange(RpcMethod.RAW_DATA.toRequestBuilder().param("hash", hash), RawDataResponse.class);
    }

    public FactomResponse<ReceiptResponse> receipt(String hash) throws FactomException.ClientException {
        return exchange(RpcMethod.RECEIPT.toRequestBuilder().param("hash", hash), ReceiptResponse.class);
    }

    public FactomResponse<RevealResponse> revealChain(String entry) throws FactomException.ClientException {
        return exchange(RpcMethod.REVEAL_CHAIN.toRequestBuilder().param("entry", entry), RevealResponse.class);
    }

    public FactomResponse<RevealResponse> revealEntry(String entry) throws FactomException.ClientException {
        return exchange(RpcMethod.REVEAL_ENTRY.toRequestBuilder().param("entry", entry), RevealResponse.class);
    }

    public FactomResponse<SendRawMessageResponse> sendRawMessage(String message) throws FactomException.ClientException {
        return exchange(RpcMethod.SEND_RAW_MESSAGE.toRequestBuilder().param("message", message), SendRawMessageResponse.class);
    }

    public FactomResponse<TransactionResponse> transaction(String hash) throws FactomException.ClientException {
        return exchange(RpcMethod.TRANSACTION.toRequestBuilder().param("hash", hash), TransactionResponse.class);
    }
}
