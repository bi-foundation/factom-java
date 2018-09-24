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
abstract class AbstractClient implements LowLevelClient {

    protected EncodeOperations encodeOperations = new EncodeOperations();
    private URL url;
    private RpcSettings settings;
    private ExecutorService executorService;

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
        setUrl(settings.getServer().getURL());
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
        return exchange(factomRequest.getRpcRequest(), rpcResultClass);
    }

    @Override
    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(RpcRequest.Builder rpcRequestBuilder, Class<RpcResult> rpcResultClass) {
        return exchange(rpcRequestBuilder.build(), rpcResultClass);
    }

    @Override
    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(RpcRequest rpcRequest, Class<RpcResult> rpcResultClass) {
        Exchange<RpcResult> exchange = new Exchange<>(this, rpcRequest, rpcResultClass);
        return exchange.execute();

    }

    @Override
    public LowLevelClient setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    @Override
    public synchronized ExecutorService getExecutorService() {
        if (executorService == null) {
            this.executorService = new ThreadPoolExecutor(2, 10, 5, TimeUnit.MINUTES,
                    new SynchronousQueue<>(), threadFactory("Factom Client Dispatcher", false));
        }
        return executorService;
    }
}
