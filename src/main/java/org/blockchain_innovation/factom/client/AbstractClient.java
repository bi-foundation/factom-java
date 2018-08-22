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
import org.blockchain_innovation.factom.client.settings.RpcSettings;

import java.net.URL;

abstract class AbstractClient {
    private RpcSettings settings;

    public RpcSettings getSettings() throws FactomException.ClientException {
        if (settings == null) {
            throw new FactomException.ClientException("settings not provided");
        }
        return settings;
    }

    public void setSettings(RpcSettings settings) {
        this.settings = settings;
        setUrl(settings.getServer().getURL());
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    private URL url;

    public <RpcResult> FactomResponse<RpcResult> exchange(FactomRequestImpl factomRequest, Class<RpcResult> rpcResultClass) throws FactomException.ClientException {
        return exchange(factomRequest.getRpcRequest(), rpcResultClass);
    }

    public <RpcResult> FactomResponse<RpcResult> exchange(RpcRequest.Builder rpcRequestBuilder, Class<RpcResult> rpcResultClass) throws FactomException.ClientException {
        return exchange(rpcRequestBuilder.build(), rpcResultClass);
    }

    public <RpcResult> FactomResponse<RpcResult> exchange(RpcRequest rpcRequest, Class<RpcResult> rpcResultClass) throws FactomException.ClientException {
        Exchange<RpcResult> exchange = new Exchange<>(getSettings(), rpcRequest, rpcResultClass);
        return exchange.execute();
    }
}
