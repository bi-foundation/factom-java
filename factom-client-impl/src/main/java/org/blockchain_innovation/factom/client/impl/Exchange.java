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
import org.blockchain_innovation.factom.client.api.json.JsonConverter;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcRequest;
import org.blockchain_innovation.factom.client.api.rpc.RpcResponse;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class Exchange<Result> {

    private final URL url;
    private final RpcSettings settings;
    private final FactomRequest factomRequest;
    private final Class<Result> rpcResultClass;
    private final Logger logger = LoggerFactory.getLogger(Exchange.class);
    private final ExecutorService executorService;
    private HttpURLConnection connection;
    private FactomResponse<Result> factomResponse;


    protected Exchange(LowLevelClient client, RpcRequest rpcRequest, Class<Result> rpcResultClass) {
        this.executorService = client.getExecutorService();
        this.settings = client.getSettings();
        this.url = settings.getServer().getURL();
        this.factomRequest = new FactomRequestImpl(rpcRequest);
        this.rpcResultClass = rpcResultClass;
    }


    public CompletableFuture<FactomResponse<Result>> execute() {

        return CompletableFuture.supplyAsync(() -> {
            connection();
            sendRequest();
            retrieveResponse(rpcResultClass);
            return getFactomResponse();
        }, getExecutorService());
    }


    public FactomRequest getFactomRequest() {
        return factomRequest;
    }

    public RpcRequest getRpcRequest() {
        if (getFactomRequest() == null) {
            return null;
        }
        return getFactomRequest().getRpcRequest();
    }

    public FactomResponse<Result> getFactomResponse() {
        return factomResponse;
    }

    protected HttpURLConnection connection() throws FactomException.ClientException {
        if (connection == null) {
            this.connection = createConnection(url);
        }
        return connection;
    }


    protected void sendRequest() throws FactomException.ClientException {
        if (getFactomRequest().getRpcRequest() != null) {
            try (OutputStream outputStream = connection().getOutputStream(); OutputStreamWriter out = new OutputStreamWriter(outputStream, Charset.defaultCharset())) {
                String json = JsonConverter.Provider.getInstance().toJson(getFactomRequest().getRpcRequest());
                logger.debug("request({}): {} ", getFactomRequest().getRpcRequest().getId(), json);
                out.write(json);
            } catch (IOException e) {
                throw new FactomException.ClientException(String.format("Error while talking to %s: %s", url, e.getMessage()), e);
            }
        }
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    protected FactomResponse<Result> retrieveResponse(Class<Result> rpcResultClass) throws FactomException.ClientException {
        try (InputStream is = connection().getInputStream();
             InputStreamReader streamReader = new InputStreamReader(is, Charset.defaultCharset());
             BufferedReader reader = new BufferedReader(streamReader)) {
            String json = reader.lines().collect(Collectors.joining());
            logger.debug("response({}): {}", getFactomRequest().getRpcRequest().getId(), JsonConverter.Provider.getInstance().prettyPrint(json));
            RpcResponse<Result> rpcResult = JsonConverter.Provider.getInstance().fromJson(json, rpcResultClass);
            this.factomResponse = new FactomResponseImpl<>(this, rpcResult, connection().getResponseCode(), connection().getResponseMessage());
            return factomResponse;
        } catch (IOException e) {
            String error = "<no error response>";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection().getErrorStream(), Charset.defaultCharset()))) {
                error = br.lines().collect(Collectors.joining(System.lineSeparator()));

                RpcErrorResponse errorResponse = JsonConverter.Provider.getInstance().errorFromJson(error);
                this.factomResponse = new FactomResponseImpl<>(this, errorResponse, connection().getResponseCode(), connection().getResponseMessage());

                // No you never log yourself and rethrow an exception. We are however a library so are reliant on the implementor
                // to do proper logging on exception. Hence we bind to debug level to not upset everybody ;)
                if (logger.isDebugEnabled()) {
                    logger.error("RPC Server returned an error response. HTTP code: {}, message: {}", getFactomResponse().getHTTPResponseCode(), getFactomResponse().getHTTPResponseMessage());
                    logger.error("error response({}): {}", getFactomRequest().getRpcRequest().getId(), JsonConverter.Provider.getInstance().prettyPrint(error) + "\n");
                }
            } catch (RuntimeException | IOException e2) {
                logger.error("Error after handling an error response of the server: " + e2.getMessage() + ". Error body: " + error, e2);
                // Fallback to client exception when we could not retrieve the error response
                throw new FactomException.ClientException(e);
            }
            throw new FactomException.RpcErrorException(e, factomResponse);

        }
    }


    protected HttpURLConnection createConnection(URL url) throws FactomException.RpcErrorException {
        HttpURLConnection connection;
        try {
            if (settings.getProxy() == null) {
                connection = (HttpURLConnection) url.openConnection();
            } else {
                RpcSettings.Proxy proxySettings = settings.getProxy();
                connection = (HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxySettings.getHost(), proxySettings.getPort())));
            }
            connection.setConnectTimeout(settings.getServer().getTimeout() * 1000);
            connection.setReadTimeout(settings.getServer().getTimeout() * 1000);
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);

            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");

            return connection;
        } catch (IOException e) {
            throw new FactomException.RpcErrorException(e, getFactomResponse());
        }


    }

    protected ExecutorService getExecutorService() {
        return executorService;
    }


}
