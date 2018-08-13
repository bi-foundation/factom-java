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

package org.blockchain_innovation.factom.client.data.model.rpc;

import java.util.HashMap;
import java.util.Map;

public class RpcErrorResponse {
    private Error error;
    private int id;
    private String jsonrpc;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "RpcErrorResponse{" +
                "error=" + error +
                ", id=" + id +
                ", jsonrpc='" + jsonrpc + '\'' +
                '}';
    }

    public static class Error {
        private int code;
        private String message;

        private Map<String, Object> data = new HashMap<>();

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
}
