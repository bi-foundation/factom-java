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
    // factomd api
    HEIGHTS("heights"), ADMIN_BLOCK_BY_HEIGHT("ablock-by-height"), ADMIN_BLOCK_BY_KEYMR("admin-block"), ACK_TRANSACTION("ack"), CHAIN_HEAD("chain-head"), COMMIT_CHAIN("commit-chain"), COMMIT_ENTRY("commit-entry"),
    DIRECTORY_BLOCK_BY_HEIGHT("dblock-by-height"), DIRECTORY_BLOCK_BY_KEYMR("directory-block"), DIRECTORY_BLOCK_HEAD("directory-block-head"),
    ENTRY("entry"), ENTRY_BLOCK_BY_KEYMR("entry-block"), ENTRY_CREDIT_BLOCK_BY_HEIGH("ecblock-by-height"), ENTRY_CREDIT_BALANCE("entry-credit-balance"), ENTRY_CREDIT_BLOCK("entrycredit-block"),
    ENTRY_CREDIT_RATE("entry-credit-rate"), FACTOID_BALANCE("factoid-balance"), FACTOID_BLOCK("factoid-block"), FACTOID_SUBMIT("factoid-submit"), FACTOID_BLOCK_BY_HEIGHT("fblock-by-height"),
    PENDING_ENTRIES("pending-entries"), PENDING_TRANSACTONS("pending-transactions"), PROPERTIES("properties"), RAW_DATA("raw-data"), RECEIPT("receipt"),
    REVEAL_CHAIN("reveal-chain"), REVEAL_ENTRY("reveal-entry"), SEND_RAW_MESSAGE("send-raw-message"), TRANSACTION("transaction"),


    // wallet api
    ADD_ENTRY_CREDIT_OUTPUT("add-ec-output"),
    ADD_FEE("add-fee"),
    ADD_INPUT("add-input"),
    ADD_OUTPUT("add-output"),
    ADDRESS("address"),
    ALL_ADDRESSES("all-addresses"),
    COMPOSE_CHAIN("compose-chain"),
    COMPOSE_ENTRY("compose-entry"),
    COMPOSE_TRANSACTION("compose-transaction"),
    DELETE_TRANSACTION("delete-transaction"),
    GENERATE_ENTRY_CREDIT_ADDRESS("generate-ec-address"),
    GENERATE_FACTOID_ADDRESS("generate-factoid-address"),
    GET_HEIGHT("get-height"),
    IMPORT_ADDRESSES("import-addresses"),
    IMPORT_KOINIFY("import-koinify"),
    NEW_TRANSACTION("new-transaction"),
    // PROPERTIES("properties"),
    SIGN_TRANSACTION("sign-transaction"),
    SUB_FEE("sub-fee"),
    TMP_TRANSACTIONS("tmp-transactions"),
    TRANSACTIONS("transactions"),
    WALLET_BACKUP("wallet-backup")
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
