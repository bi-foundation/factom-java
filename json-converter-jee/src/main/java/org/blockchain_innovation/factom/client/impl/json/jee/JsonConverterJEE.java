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
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcMethod;
import org.blockchain_innovation.factom.client.api.rpc.RpcResponse;

import javax.json.Json;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Properties;

public class JsonConverterJEE implements JsonConverter {

    private Reader reader;
    private Writer writer;

    static {
        Registry.register(JsonConverterJEE.class);
    }

    //// TODO: 06/08/2018 Implement readers/writers

    @Override
    public JsonConverterJEE configure(Properties properties) {
//        this.gson = new GsonBuilder().setPrettyPrinting().setFieldNamingStrategy(propertyNamingStrategy()).create();
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

    private Jsonb jsonb() {
        JsonbConfig config = new JsonbConfig().
                withFormatting(true).
                withSerializers(new RpcMethodSerializer()).
                withDeserializers(new RpcMethodDeserializer()).withPropertyNamingStrategy(propertyNamingStrategy());
        return JsonbBuilder.create(config);
    }

    @Override
    public Writer getJsonWriter() {
        return writer;
    }

    @Override
    public RpcErrorResponse errorFromJson(Reader reader) {
        return jsonb().fromJson(reader, RpcErrorResponse.class);
    }

    @Override
    public RpcErrorResponse errorFromJson(String json) {
        return jsonb().fromJson(json, RpcErrorResponse.class);
    }


    @Override
    public <T> RpcResponse<T> fromJson(Reader reader, Class<T> resultClass) {
        Class<RpcResponse<T>> responseClass = (Class<RpcResponse<T>>) new RpcResponse().getClass();
        return jsonb().fromJson(reader, responseClass);
    }

    @Override
    public <T> RpcResponse<T> fromJson(String json, Class<T> resultClass) {
        Class<RpcResponse<T>> responseClass = (Class<RpcResponse<T>>) new RpcResponse().getClass();
        return jsonb().fromJson(json, responseClass);
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
    public JsonConverterJEE toJson(Object source, Writer writer) {
        jsonb().toJson(source, writer);
        return this;
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
                translated.replaceFirst("_", "");
            }
            return translated.toLowerCase();
        };
    }


    private class RpcMethodSerializer implements JsonbSerializer<RpcMethod> {
        public void serialize(RpcMethod rpcMethod, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
            if (rpcMethod != null) {
                serializationContext.serialize(rpcMethod.toJsonValue(), rpcMethod, jsonGenerator);
            } else {
                serializationContext.serialize(null, jsonGenerator);
            }
        }
    }

    private class RpcMethodDeserializer implements JsonbDeserializer<RpcMethod> {
        public RpcMethod deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type type) {
            RpcMethod rpcMethod = null;
            while (jsonParser.hasNext()) {
                JsonParser.Event event = jsonParser.next();
                if (event == JsonParser.Event.KEY_NAME) {
                    String key = jsonParser.getString();
                    if ("rpcmethod".equalsIgnoreCase(key)) {
//                        jsonParser.next();

                        rpcMethod = RpcMethod.fromJsonValue(jsonParser.getValue().toString());
                    }
                }
            }
            return rpcMethod;
        }
    }


}
