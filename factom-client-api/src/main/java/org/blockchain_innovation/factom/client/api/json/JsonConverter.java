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

package org.blockchain_innovation.factom.client.api.json;

import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcResponse;

import java.lang.reflect.Type;
import java.util.Properties;
import java.util.ServiceLoader;

public interface JsonConverter {

    void addAdapter(Type type, Object adapter);

    /**
     * Allows you to configure a JSON converter implementation. This is converter specific.
     *
     * @param properties Properties that the converter accepts.
     * @return The Json converter.
     */
    JsonConverter configure(Properties properties);

    /**
     * Deserializes and RPC error string in json format from a factomd or walletd RPC server.
     *
     * @param json The error json as String.
     * @return The RPC Error response.
     */
    RpcErrorResponse errorFromJson(String json);

    /**
     * Deserializes a response from factomd or walletd into a proper RCP Response object with an appropriate result
     * object as POJO.
     *
     * @param json        The json response as String from factomd or walletd.
     * @param resultClass The target result class for the response.
     * @param <Result>    The target result type.
     * @return The Rpc Response.
     */
    <Result> RpcResponse<Result> responseFromJson(String json, Class<Result> resultClass);

    /**
     * Deserialized a json
     * object as POJO.
     *
     * @param json        The json response as String from factomd or walletd
     * @param resultClass The target result class for the response
     * @param <T>    The target result type
     * @return The Target
     */
    <T> T fromJson(String json, Class<T> resultClass);

    /**
     * Pretty prints the input JSON string if the Json Converter implementation supports it.
     *
     * @param json The json string to prettyprint.
     * @return The prettyprinted json string.
     */
    String prettyPrint(String json);

    /**
     * Serializes the input object to a json string.
     *
     * @param source The source object.
     * @return A json string result.
     */
    String toRpcJson(Object source);

    String toGenericJson(Object object, Type runtimeType);

    /**
     * The name of the JSON converter implementation.
     *
     * @return The name of the implementation.
     */
    String getName();

    /**
     * SPI specific details are contained in this class.
     */
    class Provider {

        /**
         * Create a new JsonConverter instance.
         *
         * @return The Json Converter.
         */
        public static JsonConverter newInstance() {
            assertRegistered();
            JsonConverter converter = serviceLoader(false).iterator().next();
            return converter;
        }

        /**
         * Create a new JsonConverter instance by name.
         *
         * @param converterName The name of the converter.
         * @return The converter belonging to the supplied name.
         */
        public static JsonConverter newInstance(String converterName) {
            assertRegistered();
            for (JsonConverter jsonConverter : serviceLoader(false)) {
                if (jsonConverter.getName().equalsIgnoreCase(converterName)) {
                    return jsonConverter;
                }

            }
            throw new FactomRuntimeException(String.format("Could not find Json converter named %s. Please make sure a Factom json converter jar is on the classpath.", converterName));
        }

        /**
         * Checks whether there is at least one registered JSON converter.
         */
        private static void assertRegistered() {
            ServiceLoader<JsonConverter> jsonConverters = serviceLoader(false);
            if (!jsonConverters.iterator().hasNext()) {
                jsonConverters.reload();
                if (!jsonConverters.iterator().hasNext()) {
                    throw new FactomRuntimeException("No Factom Json converter class has been registered. Please make sure a Factom json converter jar is on the classpath.");
                }
            }
        }

        /**
         * Create JSON Converter service loader.
         *
         * @param reload Whether to reload the loader.
         * @return The JSON converter service loader.
         */
        private static ServiceLoader<JsonConverter> serviceLoader(boolean reload) {
            ServiceLoader<JsonConverter> loader = ServiceLoader.load(JsonConverter.class);
            if (reload) {
                loader.reload();
            }
            return loader;
        }
    }
}
