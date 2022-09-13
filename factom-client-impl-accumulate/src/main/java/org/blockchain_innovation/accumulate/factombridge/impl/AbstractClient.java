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

package org.blockchain_innovation.accumulate.factombridge.impl;

import org.apache.commons.lang3.NotImplementedException;
import org.blockchain_innovation.factom.client.api.FactomRequest;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.LowLevelClient;
import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.ops.EncodeOperations;
import org.blockchain_innovation.factom.client.api.rpc.RpcRequest;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;

import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("PMD.DoNotUseThreads")
public abstract class AbstractClient implements LowLevelClient {

    protected EncodeOperations encodeOperations = new EncodeOperations();
    private URL url;
    private RpcSettings settings;
    private ExecutorService executorService;

    private FactomToAccumulateBridge bridge = new FactomToAccumulateBridge();

    protected static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return runnable -> {
            Thread result = new Thread(runnable, name);
            result.setDaemon(daemon);
            return result;
        };
    }

    public LowLevelClient lowLevelClient() {
        return this;
    }

    @Override
    public RpcSettings getSettings() {
        if (settings == null) {
            throw new FactomException.ClientException("settings not provided");
        }
        return settings;
    }

    @Override
    public LowLevelClient setSettings(RpcSettings settings) {
        this.settings = settings;
        if (settings == null || settings.getServer() == null) {
            throw new FactomException.ClientException("Please provide Factom settings");
        }
        setUrl(settings.getServer().getURL());
        bridge.configure(settings);
        return this;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public LowLevelClient setUrl(URL url) {
        this.url = url;
        return this;
    }

    @Override
    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(FactomRequest factomRequest, Class<RpcResult> rpcResultClass) {
        return exchange(factomRequest.getRpcRequest(), rpcResultClass, true);
    }

    @Override
    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(FactomRequest factomRequest, Class<RpcResult> rpcResultClass, boolean logErrors) {
        return exchange(factomRequest.getRpcRequest(), rpcResultClass, logErrors);
    }

    @Override
    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(RpcRequest.Builder rpcRequestBuilder, Class<RpcResult> rpcResultClass) {
        return exchange(rpcRequestBuilder.build(), rpcResultClass, true);
    }

    @Override
    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(RpcRequest.Builder rpcRequestBuilder, Class<RpcResult> rpcResultClass, boolean logErrors) {
        return exchange(rpcRequestBuilder.build(), rpcResultClass, logErrors);
    }

    @Override
    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(RpcRequest rpcRequest, Class<RpcResult> rpcResultClass) {
        return exchange(rpcRequest, rpcResultClass, true);
    }

    @Override
    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(RpcRequest rpcRequest, Class<RpcResult> rpcResultClass, boolean logErrors) {
        switch (rpcRequest.getMethod()) {
            case HEIGHTS:
                throw new NotImplementedException(); // TODO
            case ACK_TRANSACTION:
                final String ackChainId = (String) rpcRequest.getParams().get("chainid");
                final String hash = (String) rpcRequest.getParams().get("hash");
                return bridge.ackTransaction(ackChainId, hash, logErrors);
            case CHAIN_HEAD:
                final String chChainId = (String) rpcRequest.getParams().get("chainid");
                return bridge.chainHead(chChainId, logErrors);
            case COMMIT_CHAIN:
                return bridge.commitChain((String) rpcRequest.getParams().get("message"));
            case COMMIT_ENTRY:
                return bridge.commitEntry((String) rpcRequest.getParams().get("message"));
            case ENTRY_BLOCK_BY_KEYMR:
                return bridge.queryEntriesByChainId((String) rpcRequest.getParams().get("keymr"),logErrors);
            case CURRENT_MINUTE:
                throw new NotImplementedException(); // TODO
            case ENTRY:
                return bridge.getEntry((String) rpcRequest.getParams().get("hash"),logErrors);
            case ENTRY_CREDIT_BALANCE:
                throw new NotImplementedException(); // TODO
            case ENTRY_CREDIT_RATE:
                throw new NotImplementedException(); // TODO
            case FACTOID_BALANCE:
                throw new NotImplementedException(); // TODO
            case FACTOID_SUBMIT:
                throw new NotImplementedException(); // TODO
            case PENDING_ENTRIES:
                throw new NotImplementedException(); // TODO return 0
            case PENDING_TRANSACTONS:
                throw new NotImplementedException(); // TODO
            case PROPERTIES:
                throw new NotImplementedException(); // TODO
            case RAW_DATA:
                throw new NotImplementedException(); // TODO
            case RECEIPT:
                throw new NotImplementedException(); // TODO
            case REVEAL_CHAIN:
                final String chainEntry = (String) rpcRequest.getParams().get("entry");
                return bridge.revealChain(chainEntry, logErrors);
            case REVEAL_ENTRY:
                final String entry = (String) rpcRequest.getParams().get("entry");
                return bridge.revealEntry(entry, logErrors);
            case SEND_RAW_MESSAGE:
                throw new NotImplementedException(); // TODO
            case TRANSACTION:
                throw new NotImplementedException(); // TODO
            case ADD_FEE:
                throw new NotImplementedException(); // TODO NOOP
            case ADD_INPUT:
                throw new NotImplementedException(); // TODO
            case ADD_OUTPUT:
                throw new NotImplementedException(); // TODO
            case ADDRESS:
                throw new NotImplementedException(); // TODO
            case ALL_ADDRESSES:
                throw new NotImplementedException(); // TODO
            case COMPOSE_TRANSACTION:
                throw new NotImplementedException(); // TODO
            case DELETE_TRANSACTION:
                throw new NotImplementedException(); // TODO
            case GENERATE_FACTOID_ADDRESS:
                throw new NotImplementedException(); // TODO
            case IMPORT_ADDRESSES:
                throw new NotImplementedException(); // TODO
            case IMPORT_KOINIFY:
                throw new NotImplementedException(); // TODO
            case NEW_TRANSACTION:
                throw new NotImplementedException(); // TODO
            case SIGN_TRANSACTION:
                throw new NotImplementedException(); // TODO
            case SUB_FEE:
                throw new NotImplementedException(); // TODO NOOP
            case TMP_TRANSACTIONS:
                throw new NotImplementedException(); // TODO
            case TRANSACTIONS:
                throw new NotImplementedException(); // TODO
            case WALLET_BACKUP:
                throw new NotImplementedException(); // TODO
            default:
                throw new NotSupportedInAccumulateException(rpcRequest.getMethod());
        }
    }

    @Override
    public LowLevelClient setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    @Override
    public ExecutorService getExecutorService() {
        if (executorService == null) {
            this.executorService = LazyExecutorServiceHolder.INSTANCE;
        }
        return executorService;
    }

    /**
     * Initialization-on-demand holder idiom to lazy-load a singleton.
     */
    private static class LazyExecutorServiceHolder {
        static final ExecutorService INSTANCE = new ThreadPoolExecutor(2, 10, 5, TimeUnit.MINUTES,
                new SynchronousQueue<>(), threadFactory("Factom Client Dispatcher", false));
    }
}
