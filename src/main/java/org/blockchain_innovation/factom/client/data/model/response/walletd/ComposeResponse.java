package org.blockchain_innovation.factom.client.data.model.response.walletd;

public class ComposeResponse {

    private Commit commit;
    private Reveal reveal;

    public Commit getCommit() {
        return commit;
    }

    public Reveal getReveal() {
        return reveal;
    }

    public class Commit {
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

        public class Params {
            private String message;

            public String getMessage() {
                return message;
            }
        }
    }

    public class Reveal {
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

        public class Params {
            private String entry;

            public String getEntry() {
                return entry;
            }
        }
    }
}
