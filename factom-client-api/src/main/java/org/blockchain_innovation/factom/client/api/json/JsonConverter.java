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

public interface JsonConverter {

    JsonConverter configure(Properties properties);

    RpcErrorResponse errorFromJson(String json);

    <Result> RpcResponse<Result> fromJson(String json, Class<Result> resultClass);

    String prettyPrint(String json);

    String toJson(Object source);

    class Registry {

        private static JsonConverter instance;
        private static Class<? extends JsonConverter> converterClass;

        public static void register(Class<? extends JsonConverter> converterClass) {
            if (Registry.converterClass == null || Registry.converterClass.equals(converterClass)) {
                Registry.converterClass = converterClass;
            } else {
                throw new FactomRuntimeException("Only one Factom Json converter class is allowed on the classpath. Please make sure you configured it correctly");
            }
        }

        public static JsonConverter newInstance() {
            try {
                assertRegistered();
                return converterClass.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                throw new FactomRuntimeException(e);
            }
        }

        public static JsonConverter sharedInstance() {
            if (instance == null) {
                instance = newInstance();
            }
            return instance;
        }


        private static void assertRegistered() {
            if (converterClass == null) {
                throw new FactomRuntimeException("No Factom Json converter class has been registered. Please make sure a Factom json converter jar is on the classpath.");
            }
        }
    }

}
