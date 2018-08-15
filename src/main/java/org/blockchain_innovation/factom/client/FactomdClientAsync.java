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
import org.blockchain_innovation.factom.client.data.model.response.AdminBlockResponse;
import org.blockchain_innovation.factom.client.data.model.rpc.RpcMethod;

import java.util.concurrent.Future;

public class FactomdClientAsync extends AbstractClientAsync {

    public Future<FactomResponse<AdminBlockResponse>> adminBlockByHeight(long height) throws FactomException.ClientException {
        return exchange(RpcMethod.ADMIN_BLOCK_BY_HEIGHT.toRequestBuilder().param("height", height), AdminBlockResponse.class);
    }

    public Future<FactomResponse<AdminBlockResponse>> adminBlockByKeyMerkleRoot(String keyMR) throws FactomException.ClientException {
        return exchange(RpcMethod.ADMIN_BLOCK_BY_KEYMR.toRequestBuilder().param("keymr", keyMR), AdminBlockResponse.class);
    }
}
