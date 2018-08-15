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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
                System.err.println("reg: "+ json);
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
                System.err.println(json);
                RpcResponse<Result> rpcResult = JsonConverter.Registry.newInstance().fromJson(json, rpcResultClass);
                this.factomResponse = new FactomResponseImpl(this, rpcResult, connection().getResponseCode(), connection().getResponseMessage());
                return factomResponse;
            }
        } catch (IOException e) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection().getErrorStream(), Charset.defaultCharset()))) {
                String error = br.lines().collect(Collectors.joining(System.lineSeparator()));
                RpcErrorResponse errorResponse = JsonConverter.Registry.newInstance().errorFromJson(error);
                this.factomResponse = new FactomResponseImpl(this, errorResponse, connection().getResponseCode(), connection().getResponseMessage());
                throw new FactomException.RpcErrorException(e, factomResponse);
            } catch (IOException e1) {
                // TODO: 09/08/2018                  e1.printStackTrace();
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
