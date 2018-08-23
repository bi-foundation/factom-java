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

package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;

public class FactomException extends Exception {
    public FactomException(String message) {
        super(message);
    }

    public FactomException(String message, Throwable cause) {
        super(message, cause);
    }

    public FactomException(Throwable cause) {
        super(cause);
    }

    public static class ClientException extends FactomException {
        public ClientException(String message) {
            super(message);
        }

        public ClientException(String message, Throwable cause) {
            super(message, cause);
        }

        public ClientException(Throwable cause) {
            super(cause);
        }
    }

    public static class RpcErrorException extends ClientException {

        private final FactomResponse<?> factomErrorResponse;

        public RpcErrorException(Throwable cause, FactomResponse<?> factomResponse) {
            super(cause);
            this.factomErrorResponse = factomResponse;
        }

        public RpcErrorResponse getRpcErrorResponse() {
            if (getFactomResponse() == null) {
                return null;
            }
            return getFactomResponse().getRpcErrorResponse();
        }

        public FactomResponse getFactomResponse() {
            return factomErrorResponse;
        }
    }
}
