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

package org.blockchain_innovation.factom.client.api.model.response.walletd;

import java.io.Serializable;

public class ComposeResponse implements Serializable {

    private Commit commit;
    private Reveal reveal;

    public Commit getCommit() {
        return commit;
    }

    public Reveal getReveal() {
        return reveal;
    }

    public ComposeResponse setCommit(Commit commit) {
        this.commit = commit;
        return this;
    }

    public ComposeResponse setReveal(Reveal reveal) {
        this.reveal = reveal;
        return this;
    }

    public static class Commit implements Serializable {
        private String jsonrpc;
        private int id;
        private Params params;
        private String method;

        public String getJsonRpc() {
            return jsonrpc;
        }

        public int getId() {
            return id;
        }

        public Params getParams() {
            return params;
        }

        public String getMethod() {
            return method;
        }

        public Commit setJsonrpc(String jsonrpc) {
            this.jsonrpc = jsonrpc;
            return this;
        }

        public Commit setId(int id) {
            this.id = id;
            return this;
        }

        public Commit setParams(Params params) {
            this.params = params;
            return this;
        }

        public Commit setMethod(String method) {
            this.method = method;
            return this;
        }

        public static class Params implements Serializable {
            private String message;

            public String getMessage() {
                return message;
            }

            public Params setMessage(String message) {
                this.message = message;
                return this;
            }
        }
    }

    public static class Reveal implements Serializable {
        private String jsonrpc;
        private int id;
        private Params params;
        private String method;

        public String getJsonRpc() {
            return jsonrpc;
        }

        public int getId() {
            return id;
        }

        public Params getParams() {
            return params;
        }

        public String getMethod() {
            return method;
        }

        public Reveal setJsonrpc(String jsonrpc) {
            this.jsonrpc = jsonrpc;
            return this;
        }

        public Reveal setId(int id) {
            this.id = id;
            return this;
        }

        public Reveal setParams(Params params) {
            this.params = params;
            return this;
        }

        public Reveal setMethod(String method) {
            this.method = method;
            return this;
        }

        public static class Params implements Serializable {
            private String entry;

            public String getEntry() {
                return entry;
            }

            public Params setEntry(String entry) {
                this.entry = entry;
                return this;
            }
        }
    }
}
