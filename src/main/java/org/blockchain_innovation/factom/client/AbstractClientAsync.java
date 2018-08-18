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
import org.blockchain_innovation.factom.client.data.model.rpc.RpcRequest;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractClientAsync {

    private URL url;
    private ExecutorService executorService;

    public static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return runnable -> {
            Thread result = new Thread(runnable, name);
            result.setDaemon(daemon);
            return result;
        };
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public synchronized ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(2, 10, 5, TimeUnit.MINUTES,
                    new SynchronousQueue<>(), threadFactory("FactomApi Dispatcher", false));
        }
        return executorService;
    }

    public synchronized <RpcResult> Future<FactomResponse<RpcResult>> exchange(FactomRequestImpl factomRequest, Class<RpcResult> rpcResultClass) throws FactomException.ClientException {
        return exchange(factomRequest.getRpcRequest(), rpcResultClass);
    }

    public synchronized <RpcResult> Future<FactomResponse<RpcResult>> exchange(RpcRequest.Builder rpcRequestBuilder, Class<RpcResult> rpcResultClass) throws FactomException.ClientException {
        return exchange(rpcRequestBuilder.build(), rpcResultClass);
    }

    public synchronized <RpcResult> Future<FactomResponse<RpcResult>> exchange(RpcRequest rpcRequest, Class<RpcResult> rpcResultClass) throws FactomException.ClientException {
        Exchange<RpcResult> exchange = new Exchange(getUrl(), rpcRequest, rpcResultClass);
        return getExecutorService().submit(exchange);
    }
}
