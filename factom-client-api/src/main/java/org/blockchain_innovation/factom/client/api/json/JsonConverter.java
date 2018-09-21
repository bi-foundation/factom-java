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

import java.util.Properties;
import java.util.ServiceLoader;

public interface JsonConverter {

    /**
     * Allows you to configure a JSON converter implementation. This is converter specific.
     *
     * @param properties Properties the converter accepts
     * @return
     */
    JsonConverter configure(Properties properties);

    /**
     * Deserializes and RPC error string in json format from a factomd or walletd RPC server.
     *
     * @param json The error json as String
     * @return
     */
    RpcErrorResponse errorFromJson(String json);

    /**
     * Deserialized a response from factomd or walletd into a proper RCP Response object with an appropriate result
     * object as POJO.
     *
     * @param json        The json response as String from factomd or walletd
     * @param resultClass The target result class for the response
     * @param <Result>    The target result type
     * @return The RpcResponse
     */
    <Result> RpcResponse<Result> fromJson(String json, Class<Result> resultClass);

    /**
     * Pretty prints the input JSON string if the Json Converter implementation supports it.
     *
     * @param json The json string to prettyprint
     * @return The prettyprinted json string
     */
    String prettyPrint(String json);

    /**
     * Serializes the input object to a json string.
     *
     * @param source The source object
     * @return A json string result
     */
    String toJson(Object source);

    String getName();


    class Provider {

        public static JsonConverter getInstance() {
            assertRegistered();
            JsonConverter converter = serviceLoader(false).iterator().next();
            return converter;
        }

        public static JsonConverter getInstance(String converterName) {
            assertRegistered();
            for (JsonConverter jsonConverter : serviceLoader(false)) {
                if (jsonConverter.getName().equalsIgnoreCase(converterName)) {
                    return jsonConverter;
                }

            }
            throw new FactomRuntimeException(String.format("Could not find Json converter named %s. Please make sure a Factom json converter jar is on the classpath.", converterName));
        }

        private static void assertRegistered() {
            ServiceLoader<JsonConverter> jsonConverters = serviceLoader(false);
            if (!jsonConverters.iterator().hasNext()) {
                jsonConverters.reload();
                if (!jsonConverters.iterator().hasNext()) {
                    throw new FactomRuntimeException("No Factom Json converter class has been registered. Please make sure a Factom json converter jar is on the classpath.");
                }
            }
        }

        private static ServiceLoader<JsonConverter> serviceLoader(boolean reload) {
            ServiceLoader<JsonConverter> loader = ServiceLoader.load(JsonConverter.class);
            if (reload) {
                loader.reload();
            }
            return loader;
        }


    }

}
