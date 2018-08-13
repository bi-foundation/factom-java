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
import org.bif.factom.client.data.model.response.*;
import org.blockchain_innovation.factom.client.data.model.rpc.RpcMethod;
import org.blockchain_innovation.factom.client.data.model.response.*;

import java.util.Map;

public class FactomdClient extends AbstractClient {


    public FactomResponse<HeightsResponse> heights() throws FactomException.ClientException {
        return exchange(RpcMethod.HEIGHTS.toRequest(), HeightsResponse.class).getFactomResponse();
    }


    public FactomResponse<AdminBlockResponse> adminBlockByHeight(long height) throws FactomException.ClientException {
        return exchange(RpcMethod.ADMIN_BLOCK_BY_HEIGHT.toRequestBuilder().param("height", height), AdminBlockResponse.class).getFactomResponse();
    }

    public FactomResponse<AdminBlockResponse> adminBlockByKeyMerkleRoot(String keyMR) throws FactomException.ClientException {
        return exchange(RpcMethod.ADMIN_BLOCK_BY_HEIGHT.toRequestBuilder().param("keymr", keyMR), AdminBlockResponse.class).getFactomResponse();
    }


    public FactomResponse<Map> ackTransactions(String hash, String chainId) throws FactomException.ClientException {
        return exchange(RpcMethod.ACK_TRANSACTION.toRequestBuilder().param("hash", hash).param("chainid", chainId), Map.class).getFactomResponse();
    }

/*
    public FactomResponse<Map> ackFullTransaction(String fullMarshalledTransaction, String chainId) throws FactomException.ClientException {
        return exchange(RpcMethod.ACK_TRANSACTION.toRequestBuilder().param("fulltransaction", fullMarshalledTransaction).param("chainid", chainId), Map.class).getFactomResponse();
    }
*/

    public FactomResponse<Map> ackFactoidTransactions(String chainId) throws FactomException.ClientException {
        return ackTransactions("f", chainId);
    }

    public FactomResponse<Map> ackEntryTransactions(String chainId) throws FactomException.ClientException {
        return ackTransactions("c", chainId);
    }

    public FactomResponse<ChainHeadResponse> chainHead(String chainId) throws FactomException.ClientException {
        return exchange(RpcMethod.CHAIN_HEAD.toRequestBuilder().param("chainid", chainId), ChainHeadResponse.class).getFactomResponse();
    }

    public FactomResponse<CommitChainResponse> commitChain(String message) throws FactomException.ClientException {
        return exchange(RpcMethod.COMMIT_CHAIN.toRequestBuilder().param("message", message), CommitChainResponse.class).getFactomResponse();
    }

    public FactomResponse<CommitEntryResponse> commitEntry(String message) throws FactomException.ClientException {
        return exchange(RpcMethod.COMMIT_ENTRY.toRequestBuilder().param("message", message), CommitEntryResponse.class).getFactomResponse();
    }

    public FactomResponse<DirectoryBlockResponse> directoryBlockByHeight(long height) throws FactomException.ClientException {
        return exchange(RpcMethod.DIRECTORY_BLOCK_BY_HEIGHT.toRequestBuilder().param("height", height), DirectoryBlockResponse.class).getFactomResponse();
    }

    public FactomResponse<DirectoryBlockResponse> directoryBlockByKeyMerkleRoot(String keyMR) throws FactomException.ClientException {
        return exchange(RpcMethod.DIRECTORY_BLOCK_BY_KEYMR.toRequestBuilder().param("keymr", keyMR), DirectoryBlockResponse.class).getFactomResponse();
    }

    public FactomResponse<DirectoryBlockHeadResponse> directoryBlockHead() throws FactomException.ClientException {
        return exchange(RpcMethod.DIRECTORY_BLOCK_HEAD.toRequest(), DirectoryBlockHeadResponse.class).getFactomResponse();
    }

    public FactomResponse<EntryResponse> entry(String entryHash) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY.toRequestBuilder().param("hash", entryHash), EntryResponse.class).getFactomResponse();
    }

    public FactomResponse<EntryBlockResponse> entryBlockByKeyMerkleRoot(String keyMR) throws FactomException.ClientException {
        return exchange(RpcMethod.ENTRY_BLOCK_BY_KEYMR.toRequestBuilder().param("keymr", keyMR), EntryBlockResponse.class).getFactomResponse();
    }
}
