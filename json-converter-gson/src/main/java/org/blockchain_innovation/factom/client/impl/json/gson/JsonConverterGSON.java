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

package org.blockchain_innovation.factom.client.impl.json.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.blockchain_innovation.factom.client.api.json.JsonConverter;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcMethod;
import org.blockchain_innovation.factom.client.api.rpc.RpcResponse;

import java.lang.reflect.Type;
import java.util.Properties;

public class JsonConverterGSON implements JsonConverter {
    private Gson gson;

    static {
        Registry.register(JsonConverterGSON.class);
    }

    @Override
    public JsonConverterGSON configure(Properties properties) {
        GsonBuilder builder = builder();

        // Init new properties so we get default values
        if (properties == null) {
            properties = new Properties();
        }
        boolean lenient = Boolean.parseBoolean(properties.getProperty("json.lenient", "true"));
        if (lenient) {
            builder.setLenient();
        }
        boolean prettyprint = Boolean.parseBoolean(properties.getProperty("json.prettyprint", "true"));
        if (prettyprint) {
            builder.setPrettyPrinting();
        }

        this.gson = builder.create();
        return this;
    }

    @Override
    public RpcErrorResponse errorFromJson(String json) {
        return gson().fromJson(json, RpcErrorResponse.class);
    }


    @Override
    public <T> RpcResponse<T> fromJson(String json, Class<T> resultClass) {
        return gson().fromJson(json, TypeToken.getParameterized(RpcResponse.class, resultClass).getType());
    }

    @Override
    public String prettyPrint(String json) {
        // New builder since we will always pretty print. The parser is necesary to force pretty printing the sting input
        return builder().setLenient().setPrettyPrinting().create().toJson(new JsonParser().parse(json));
    }

    @Override
    public String toJson(Object input) {
        return gson().toJson(input);
    }


    /**
     * add naming strategy to handle response with reserved keywords and dashes.
     * Examples are: TmpTransaction#Transaction tx-name and WalletBackupResponse wallet-seed
     *
     * @return custom FieldNamingStrategy
     * @see org.blockchain_innovation.factom.client.api.model.response.walletd.AddressResponse#_public public member of AddressResponse
     */
    private FieldNamingStrategy fieldNamingStrategy() {
        return f -> FieldNamingPolicy.LOWER_CASE_WITH_DASHES.translateName(f).replace("_", "");
    }

    private GsonBuilder builder() {
        return new GsonBuilder().setFieldNamingStrategy(fieldNamingStrategy()).
//                    registerTypeAdapter(RpcMethod.class, new RpcMethodDeserializer()).
        registerTypeAdapter(RpcMethod.class, new RpcMethodSerializer());
    }

    private Gson gson() {
        if (gson == null) {
            configure(null);

        }
        return gson;
    }


    private class RpcMethodSerializer implements JsonSerializer<RpcMethod> {
        public JsonElement serialize(RpcMethod rpcMethod, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(rpcMethod.toJsonValue());
        }
    }

    /*private class RpcMethodDeserializer implements JsonDeserializer<RpcMethod> {
        public RpcMethod deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return RpcMethod.fromJsonValue(json.getAsJsonPrimitive().getAsString());
        }
    }*/
}
