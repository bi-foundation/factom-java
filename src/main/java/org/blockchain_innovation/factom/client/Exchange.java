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
import org.blockchain_innovation.factom.client.data.conversion.json.GsonConverter;
import org.blockchain_innovation.factom.client.data.conversion.json.JsonConverter;
import org.blockchain_innovation.factom.client.data.model.rpc.RpcErrorResponse;
import org.blockchain_innovation.factom.client.data.model.rpc.RpcRequest;
import org.blockchain_innovation.factom.client.data.model.rpc.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Exchange<Result> implements Callable<FactomResponse<Result>> {

    private HttpURLConnection connection;
    private final URL url;
    private final FactomRequestImpl factomRequest;
    private FactomResponse<Result> factomResponse;
    private final Class<Result> rpcResultClass;

    private final Logger logger = LoggerFactory.getLogger(Exchange.class);

    //// FIXME: 06/08/2018 Only needed now to iinit the converter
    GsonConverter conv = new GsonConverter();


    protected Exchange(URL url, RpcRequest rpcRequest, Class<Result> rpcResultClass) {
        this.url = url;
        this.factomRequest = new FactomRequestImpl(rpcRequest);
        this.rpcResultClass = rpcResultClass;
    }

    @Override
    public FactomResponse<Result> call() throws Exception {
        return execute();
    }

    public FactomResponse<Result> execute() throws FactomException.ClientException {
        connection();
        sendRequest();
        retrieveResponse(rpcResultClass);
        return getFactomResponse();
    }


    public FactomRequestImpl getFactomRequest() {
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
        try {
            if (getFactomRequest().getRpcRequest() != null) {
                OutputStreamWriter out = new OutputStreamWriter(connection().getOutputStream());
                String json = JsonConverter.Registry.newInstance().toJson(getFactomRequest().getRpcRequest());
                logger.debug("request({}): {} ", getFactomRequest().getRpcRequest().getId(), json);
                out.write(json);
                out.close();
            }
        } catch (IOException e) {
            throw new FactomException.ClientException(String.format("Error while talking to %s: %s", url, e.getMessage()), e);
        }
    }


    protected FactomResponse<Result> retrieveResponse(Class<Result> rpcResultClass) throws FactomException.ClientException {
        try (InputStream is = connection().getInputStream()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String json = reader.lines().collect(Collectors.joining());
                logger.debug("response({}): {}", getFactomRequest().getRpcRequest().getId(), JsonConverter.Registry.newInstance().prettyPrint(json));
                RpcResponse<Result> rpcResult = JsonConverter.Registry.newInstance().fromJson(json, rpcResultClass);
                this.factomResponse = new FactomResponseImpl<>(this, rpcResult, connection().getResponseCode(), connection().getResponseMessage());
                return factomResponse;
            }
        } catch (IOException e) {
            String error = "<no error response>";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection().getErrorStream(), Charset.defaultCharset()))) {
                error = br.lines().collect(Collectors.joining(System.lineSeparator()));

                RpcErrorResponse errorResponse = JsonConverter.Registry.newInstance().errorFromJson(error);
                this.factomResponse = new FactomResponseImpl<>(this, errorResponse, connection().getResponseCode(), connection().getResponseMessage());

                // No you never log yourself and rethrow an exception. We are however a library so are reliant on the implementor to do proper logging on exception. Hence we bind to debug level to not upset everybody ;)
                if (logger.isDebugEnabled()) {
                    logger.error("RPC Server returned an error response. HTTP code: {}, message: {}", getFactomResponse().getHTTPResponseCode(), getFactomResponse().getHTTPResponseMessage());
                    logger.error("error response({}): {}", getFactomRequest().getRpcRequest().getId(), JsonConverter.Registry.newInstance().prettyPrint(error) + "\n");
                }
                throw new FactomException.RpcErrorException(e, factomResponse);
            } catch (RuntimeException | IOException e2) {
                logger.error("Error after handling an error response of the server: " + e2.getMessage() + ". Error body: " + error, e2);
            }

            // Fallback to client exception when we could not retrieve the error response
            throw new FactomException.ClientException(e);
        }
    }


    protected HttpURLConnection createConnection(URL url) throws FactomException.ClientException {
        HttpURLConnection connection;
        try {
            if (true) {
                connection = (HttpURLConnection) url.openConnection();
            } else {
                // FIXME: 6-7-2018 Settings
                connection = (HttpURLConnection) url.openConnection(null);
            }

            // TODO: 6-7-2018 Make settings
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);

            connection.setDoOutput(true);

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");

            return connection;
        } catch (IOException e) {
            throw new FactomException.ClientException(e);
        }


    }


}
