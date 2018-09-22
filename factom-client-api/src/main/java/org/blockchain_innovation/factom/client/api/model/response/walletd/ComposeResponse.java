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

    public ComposeResponse() {
    }

    public ComposeResponse(Commit commit, Reveal reveal) {
        this.commit = commit;
        this.reveal = reveal;
    }

    public Commit getCommit() {
        return commit;
    }

    public Reveal getReveal() {
        return reveal;
    }


    public static class Commit implements Serializable {

        public Commit() {

        }

        public Commit(String jsonrpc, String method, int id, Params params) {
            this.jsonrpc = jsonrpc;
            this.method = method;
            this.id = id;
            this.params = params;
        }

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


        public static class Params implements Serializable {
            private String message;

            public Params() {
            }

            public Params(String message) {
                this.message = message;
            }

            public String getMessage() {
                return message;
            }

        }
    }

    public static class Reveal implements Serializable {
        private String jsonrpc;
        private int id;
        private Params params;
        private String method;

        public Reveal() {

        }

        public Reveal(String jsonrpc, String method, int id, Reveal.Params params) {
            this.jsonrpc = jsonrpc;
            this.method = method;
            this.id = id;
            this.params = params;
        }

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


        public static class Params implements Serializable {
            private String entry;

            public Params() {
            }

            public Params(String entry) {
                this.entry = entry;
            }

            public String getEntry() {
                return entry;
            }

        }
    }
}
