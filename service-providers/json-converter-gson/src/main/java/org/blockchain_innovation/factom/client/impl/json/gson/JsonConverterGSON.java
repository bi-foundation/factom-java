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

import javax.inject.Named;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Named
public class JsonConverterGSON implements JsonConverter {

    public static final String NAME = "GSON";
    private Gson gson;
    private Map<Type, Object> adapters = new HashMap<>();

    @Override
    public void addAdapter(Type type, Object adapter) {
        adapters.put(type, adapter);
    }

    @Override
    public JsonConverterGSON configure(Properties properties) {
        GsonBuilder builder = builder();

        // Init new properties so we get default values
        Properties props = properties == null ? new Properties() : properties;

        boolean lenient = Boolean.parseBoolean(props.getProperty("json.lenient", "true"));
        if (lenient) {
            builder.setLenient();
        }
        boolean prettyprint = Boolean.parseBoolean(props.getProperty("json.prettyprint", "true"));
        if (prettyprint) {
            builder.setPrettyPrinting();
        }
        adapters.forEach((type, adapter) -> builder.registerTypeAdapter(type, adapter));

        this.gson = builder.create();
        return this;
    }

    @Override
    public RpcErrorResponse errorFromJson(String json) {
        return gson().fromJson(json, RpcErrorResponse.class);
    }


    @Override
    public <T> RpcResponse<T> responseFromJson(String json, Class<T> resultClass) {
        return gson().fromJson(json, TypeToken.getParameterized(RpcResponse.class, resultClass).getType());
    }

    @Override
    public <T> T fromJson(String json, Class<T> resultClass) {
        return gson().fromJson(json, resultClass);
    }

    @Override
    public String prettyPrint(String json) {
        // New builder since we will always pretty print. The parser is necesary to force pretty printing the sting input
        return builder().setLenient().setPrettyPrinting().create().toJson(new JsonParser().parse(json));
    }

    @Override
    public String toRpcJson(Object input) {
        return gson().toJson(input);
    }

    @Override
    public String toGenericJson(Object object, Type runtimeType) {
        return gson().toJson(object, runtimeType);
    }


    /**
     * add naming strategy to handle response with reserved keywords and dashes.
     * Examples are: TmpTransaction#Transaction tx-name and WalletBackupResponse wallet-seed
     *
     * @return custom FieldNamingStrategy
     * @see org.blockchain_innovation.factom.client.api.model.response.walletd.AddressResponse#_public public member of AddressResponse
     */
    private FieldNamingStrategy fieldNamingStrategy() {
//        return FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
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


    private static class RpcMethodSerializer implements JsonSerializer<RpcMethod> {
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


    @Override
    public String getName() {
        return NAME;
    }
}
