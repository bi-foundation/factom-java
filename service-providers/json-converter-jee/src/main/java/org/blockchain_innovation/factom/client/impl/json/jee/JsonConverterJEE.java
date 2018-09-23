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

package org.blockchain_innovation.factom.client.impl.json.jee;

import org.blockchain_innovation.factom.client.api.json.JsonConverter;
import org.blockchain_innovation.factom.client.api.model.response.factomd.PendingTransactionsResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcMethod;
import org.blockchain_innovation.factom.client.api.rpc.RpcResponse;

import javax.inject.Named;
import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import java.lang.reflect.*;
import java.nio.CharBuffer;
import java.util.Objects;
import java.util.Properties;

import static javax.json.bind.config.PropertyOrderStrategy.LEXICOGRAPHICAL;


@SuppressWarnings("PMD.LawOfDemeter")
@Named
public class JsonConverterJEE implements JsonConverter {
    public static final String NAME = "JEE";
    protected static final String RPC_METHOD = "method";
    private Jsonb jsonb;

    @Override
    public JsonConverterJEE configure(Properties properties) {
        final JsonbConfig jsonbConfig = config();
        // Init new properties so we get default values
        Properties props = new Properties(properties);

        boolean prettyprint = Boolean.parseBoolean(props.getProperty("json.prettyprint", "true"));
        jsonbConfig.withFormatting(prettyprint);
        this.jsonb = JsonbBuilder.create(jsonbConfig);
        return this;
    }

    private JsonbConfig config() {
        JsonbConfig config = new JsonbConfig().
                withFormatting(true).
                withPropertyOrderStrategy(LEXICOGRAPHICAL).
                withSerializers(new RpcMethodSerializer()).
                withDeserializers(new PendingTransactionsResponseDeserializer()).
                withPropertyVisibilityStrategy(propertyVisibilityStrategy()).
                withPropertyNamingStrategy(propertyNamingStrategy());
        return config;
    }


    private Jsonb jsonb() {
        if (jsonb == null) {
            configure(null);
        }
        return jsonb;
    }

    @Override
    public RpcErrorResponse errorFromJson(String json) {
        return jsonb().fromJson(json, RpcErrorResponse.class);
    }


    @Override
    public <T> RpcResponse<T> fromJson(String json, Class<T> resultClass) {
        ParameterizedType parameterizedType = new ResolvedParameterizedType(RpcResponse.class, new Type[]{resultClass});
        return jsonb().fromJson(json, parameterizedType);
    }

    @Override
    public String prettyPrint(String json) {
        return toJson(json);
    }

    @Override
    public String toJson(Object input) {
        return jsonb().toJson(input);
    }

    @Override
    public String getName() {
        return NAME;
    }


    /**
     * add naming strategy to handle response with reserved keywords and dashes.
     * Examples are: TmpTransaction#Transaction tx-name and WalletBackupResponse wallet-seed
     *
     * @return custom PropertyNamingStrategy
     * @see org.blockchain_innovation.factom.client.api.model.response.walletd.AddressResponse#_public public member of AddressResponse
     */
    private PropertyNamingStrategy propertyNamingStrategy() {
        return propertyName -> {
            Objects.requireNonNull(propertyName);
            String translated = propertyName;
            if (propertyName.startsWith("_")) {
                translated = translated.replaceFirst("_", "");
            }

            CharBuffer charBuffer = CharBuffer.allocate(translated.length() * 2);
            char last = Character.MIN_VALUE;
            for (int i = 0; i < translated.length(); i++) {
                final char current = translated.charAt(i);
                if (i > 0 && Character.isUpperCase(current) && (Character.isAlphabetic(last) && Character.isLowerCase(last))) {
                    charBuffer.append('-');
                }
                charBuffer.append(Character.toLowerCase(current));
                last = current;
            }
            return new String(charBuffer.array(), 0, charBuffer.position());
        };
    }

    private PropertyVisibilityStrategy propertyVisibilityStrategy() {
        return new PropertyVisibilityStrategy() {
            @Override
            public boolean isVisible(Field field) {
                if (Modifier.isStatic(field.getModifiers())) {
                    return false;
                }
                return Modifier.isPrivate(field.getModifiers()) || Modifier.isProtected(field.getModifiers());
            }

            @Override
            public boolean isVisible(Method method) {

                String name = method.getName();
                if ("getJsonrpc".equals(name)) {
                    return false;
                }
                if ("getId".equals(name) || "getParams".equals(name) || "getMethod".equals(name) || "set".equals(name)) {
                    return Modifier.isProtected(method.getModifiers()) || Modifier.isPublic(method.getModifiers());
                }
                return false;
            }
        };
    }


    private static class RpcMethodSerializer implements JsonbSerializer<RpcMethod> {
        public void serialize(RpcMethod rpcMethod, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
            if (rpcMethod != null) {
                serializationContext.serialize(rpcMethod.toJsonValue(), jsonGenerator);
            }
        }
    }

    private class PendingTransactionsResponseDeserializer implements JsonbDeserializer<PendingTransactionsResponse> {

        public PendingTransactionsResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type type) {

            PendingTransactionsResponse response = new PendingTransactionsResponse();
            JsonArray transactionsArray = jsonParser.getArray();
            for (int i = 0; i < transactionsArray.size(); i++) {
                JsonValue transactionValue = transactionsArray.get(i);
                String json = transactionValue.toString();
                PendingTransactionsResponse.PendingTransaction pendingTransaction = jsonb.fromJson(json, PendingTransactionsResponse.PendingTransaction.class);
                response.add(pendingTransaction);
            }
            return response;
        }
    }

   /* private class RpcMethodDeserializer implements JsonbDeserializer<RpcMethod> {

        public RpcMethod deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type type) {
            RpcMethod rpcMethod = null;
            while (jsonParser.hasNext()) {
                JsonParser.Event event = jsonParser.next();
                if (event == JsonParser.Event.KEY_NAME) {
                    String key = jsonParser.getString();
                    if (RPC_METHOD.equalsIgnoreCase(key)) {
//                        jsonParser.next();

                        rpcMethod = RpcMethod.fromJsonValue(jsonParser.getValue().toString());
                    }
                }
            }
            return rpcMethod;
        }
    }*/

}
