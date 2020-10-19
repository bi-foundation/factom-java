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
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcRequest;
import org.blockchain_innovation.factom.client.api.rpc.RpcResponse;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;

import java.io.*;
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
    private static final Logger logger = LogFactory.getLogger(Exchange.class);
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
        }, getExecutorService()).exceptionally(throwable -> {
            logger.error(throwable.getMessage(), throwable);
            return getFactomResponse();
        });
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
                String json = JsonConverter.Provider.newInstance().toRpcJson(getFactomRequest().getRpcRequest());
                logger.debug("request(%d): %s ", getFactomRequest().getRpcRequest().getId(), json);
                out.write(json);
            } catch (IOException e) {
                throw new FactomException.ClientException(String.format("Error while talking to %s: %s", url, e.getMessage()), e);
            }
        }
    }

    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.AvoidCatchingGenericException"})
    protected FactomResponse<Result> retrieveResponse(Class<Result> rpcResultClass) throws FactomException.ClientException {
        try (InputStream is = connection().getInputStream();
             InputStreamReader streamReader = new InputStreamReader(is, Charset.defaultCharset());
             BufferedReader reader = new BufferedReader(streamReader)) {

            String json = reader.lines().collect(Collectors.joining());
            logger.debug("response(%d): %s", getFactomRequest().getRpcRequest().getId(), JsonConverter.Provider.newInstance().prettyPrint(json));
            RpcResponse<Result> rpcResult = JsonConverter.Provider.newInstance().responseFromJson(json, rpcResultClass);
            if (rpcResult.getResult() == null) {
                RpcErrorResponse errorResponse = JsonConverter.Provider.newInstance().errorFromJson(json);
                if (errorResponse.getError() != null) {
                    this.factomResponse = new FactomResponseImpl<>(this, errorResponse, connection().getResponseCode(), connection().getResponseMessage());
                }
            }
            if (factomResponse == null) {
                this.factomResponse = new FactomResponseImpl<>(this, rpcResult, connection().getResponseCode(), connection().getResponseMessage());
            }
            return factomResponse;
        } catch (IOException e) {
            String error = "<no error response>";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection().getErrorStream(), Charset.defaultCharset()))) {
                error = br.lines().collect(Collectors.joining(System.lineSeparator()));

                RpcErrorResponse errorResponse = JsonConverter.Provider.newInstance().errorFromJson(error);
                this.factomResponse = new FactomResponseImpl<>(this, errorResponse, connection().getResponseCode(), connection().getResponseMessage());

                // No you never log yourself and rethrow an exception. We are however a library so are reliant on the implementor
                // to do proper logging on exception. Hence we bind to debug level to not upset everybody ;)
                if (logger.isDebugEnabled()) {
                    logger.error("RPC Server returned an error response. HTTP code: %s, message: %s", getFactomResponse().getHTTPResponseCode(), getFactomResponse().getHTTPResponseMessage());
                    logger.error("error response(%d): %s", getFactomRequest().getRpcRequest().getId(), JsonConverter.Provider.newInstance().prettyPrint(error) + "\n");
                }
            } catch (RuntimeException | IOException e2) {
                logger.error("Error after handling an error response of the server: %s. Error body: %s", e2, e2.getMessage(), error);
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
            int timeout = Math.max(5000, settings.getServer().getTimeout() * 1000);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
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
