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

package org.blockchain_innovation.factom.client.data.model.rpc;

import org.blockchain_innovation.factom.client.data.FactomRuntimeException;
import org.blockchain_innovation.factom.client.data.conversion.StringUtils;

import java.util.Collection;

public enum RpcMethod {
    HEIGHTS("heights"), ADMIN_BLOCK_BY_HEIGHT("ablock-by-height"), ADMINBLOCK_BY_KEYMR("admin-block"), ACK_TRANSACTION("ack"), CHAIN_HEAD("chain-head"), COMMIT_CHAIN("commit-chain"), COMMIT_ENTRY("commit-entry"),
    DIRECTORY_BLOCK_BY_HEIGHT("dblock-by-height"), DIRECTORY_BLOCK_BY_KEYMR("directory-block"), DIRECTORY_BLOCK_HEAD("directory-block-head"),
    ENTRY("entry"), ENTRY_BLOCK_BY_KEYMR("entry-block")
    ;

    private final String method;

    RpcMethod(String method) {
        this.method = method;
    }

    public RpcRequest toRequest() {
        return new RpcRequest(this);
    }

    public RpcRequest.Builder toRequestBuilder() {
        return new RpcRequest.Builder(this);
    }

    public RpcRequest toRequest(RpcRequest.Param<?> param) {
        return new RpcRequest(this, param);
    }

    public RpcRequest toRequest(RpcRequest.Param<?> param, RpcRequest.Param<?>... extraParams) {
        return new RpcRequest(this, param, extraParams);
    }

    public RpcRequest toRequest(Collection<RpcRequest.Param<?>> params) {
        return new RpcRequest(this, params);
    }


    public String toJsonValue() {
        return method;
    }


    public static RpcMethod fromJsonValue(String value) {
        if (StringUtils.isEmpty(value)) {
            throw new FactomRuntimeException("Cannot have a null rpc method");
        }
        for (RpcMethod method : RpcMethod.values()) {
            if (method.toJsonValue().equalsIgnoreCase(value)) {
                return method;
            }
        }

        return valueOf(value.toUpperCase());
    }


}
