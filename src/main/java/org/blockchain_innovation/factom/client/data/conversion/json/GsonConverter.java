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

package org.blockchain_innovation.factom.client.data.conversion.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.blockchain_innovation.factom.client.data.model.rpc.RpcErrorResponse;
import org.blockchain_innovation.factom.client.data.model.rpc.RpcMethod;
import org.blockchain_innovation.factom.client.data.model.rpc.RpcResponse;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Properties;

public class GsonConverter implements JsonConverter {
    private Gson gson;
    private Reader reader;
    private Writer writer;

    static {
        Registry.register(GsonConverter.class);
    }

    //// TODO: 06/08/2018 Implement readers/writers

    @Override
    public GsonConverter configure(Properties properties) {
        this.gson = new GsonBuilder().setPrettyPrinting().setFieldNamingStrategy(fieldNamingStrategy()).create();
        return this;
    }

    @Override
    public JsonConverter setJsonReader(Reader reader) {
        this.reader = reader;
        return this;
    }

    @Override
    public Reader getJsonReader() {
        return reader;
    }

    @Override
    public JsonConverter setJsonWriter(Writer writer) {
        this.writer = writer;
        return this;
    }

    @Override
    public Writer getJsonWriter() {
        return writer;
    }

    @Override
    public RpcErrorResponse errorFromJson(Reader reader) {
        return gson().fromJson(reader, RpcErrorResponse.class);
    }

    @Override
    public RpcErrorResponse errorFromJson(String json) {
        return gson().fromJson(json, RpcErrorResponse.class);
    }


    @Override
    public <T> RpcResponse<T> fromJson(Reader reader, Class<T> resultClass) {
        return gson().fromJson(reader, TypeToken.getParameterized(RpcResponse.class, resultClass).getType());
    }

    @Override
    public <T> RpcResponse<T> fromJson(String json, Class<T> resultClass) {
        return gson().fromJson(json, TypeToken.getParameterized(RpcResponse.class, resultClass).getType());
    }

    @Override
    public String prettyPrint(String json) {
        return toJson(new JsonParser().parse(json));
    }

    @Override
    public String toJson(Object input) {
        return gson().toJson(input);
    }

    @Override
    public GsonConverter toJson(Object source, Writer writer) {
        gson().toJson(source, writer);
        return this;
    }

    /**
     * add naming strategy to handle response with reserved keywords and dashes.
     * Examples are: TmpTransaction#Transaction tx-name and WalletBackupResponse wallet-seed
     * @see org.blockchain_innovation.factom.client.data.model.response.walletd.AddressResponse#_public public member of AddressResponse
     *
     * @return custom FieldNamingStrategy
     */
    private FieldNamingStrategy fieldNamingStrategy() {
        return f -> FieldNamingPolicy.LOWER_CASE_WITH_DASHES.translateName(f).replace("_", "");
    }

    private Gson gson() {
        if (gson == null) {
            this.gson = new GsonBuilder().setFieldNamingStrategy(fieldNamingStrategy()).
                    registerTypeAdapter(RpcMethod.class, new RpcMethodDeserializer()).
                    registerTypeAdapter(RpcMethod.class, new RpcMethodSerializer()).
                    setPrettyPrinting().setLenient().create();
        }
        return gson;
    }


    private class RpcMethodSerializer implements JsonSerializer<RpcMethod> {
        public JsonElement serialize(RpcMethod rpcMethod, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(rpcMethod.toJsonValue());
        }
    }

    private class RpcMethodDeserializer implements JsonDeserializer<RpcMethod> {
        public RpcMethod deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return RpcMethod.fromJsonValue(json.getAsJsonPrimitive().getAsString());
        }
    }
}
