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
import org.blockchain_innovation.factom.client.data.model.rpc.Callback;
import org.blockchain_innovation.factom.client.data.model.rpc.RpcRequest;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExchangeAsync<Result> extends Exchange<Result> implements Runnable {

    private ExecutorService executorService;
    private Callback<Result> callback;
    private Class<Result> rpcResultClass;

    protected ExchangeAsync(URL url, RpcRequest rpcRequest) {
        super(url, rpcRequest);
    }

    public static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread result = new Thread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }

    public synchronized ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), threadFactory("FactomApi Dispatcher", false));
        }
        return executorService;
    }

    public synchronized ExchangeAsync<Result> execute(Class<Result> rpcResultClass, Callback<Result> callback) {
        this.rpcResultClass = rpcResultClass;
        this.callback = callback;
        getExecutorService().execute(this);
        return this;
    }

    @Override
    public void run() {
        try {
            Exchange<Result> result = execute(rpcResultClass);
            callback.onSuccess(result.getFactomResponse());
        } catch (FactomException e) {
            callback.onFailure(e);
        }
    }
}
