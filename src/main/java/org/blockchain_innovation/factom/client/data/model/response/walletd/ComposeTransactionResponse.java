package org.blockchain_innovation.factom.client.data.model.response.walletd;

public class ComposeTransactionResponse {

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
        private String transaction;

        public String getTransaction() {
            return transaction;
        }
    }
}
