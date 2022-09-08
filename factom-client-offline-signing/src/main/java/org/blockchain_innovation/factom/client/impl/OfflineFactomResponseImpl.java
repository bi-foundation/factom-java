package org.blockchain_innovation.factom.client.impl;

import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcResponse;

public class OfflineFactomResponseImpl<Result> implements FactomResponse<Result> {

    private final RpcResponse<Result> rpcResponse;

    public OfflineFactomResponseImpl(RpcResponse<Result> rpcResponse) {
        this.rpcResponse = rpcResponse;
    }

    @Override
    public RpcResponse<Result> getRpcResponse() {
        return rpcResponse;
    }

    @Override
    public Result getResult() {
        if (getRpcResponse() == null) {
            return null;
        }
        return getRpcResponse().getResult();
    }

    @Override
    public RpcErrorResponse getRpcErrorResponse() {
        return null;
    }

    @Override
    public int getHTTPResponseCode() {
        return 200; // assertValidResponse does not like 0
    }

    @Override
    public String getHTTPResponseMessage() {
        return null;
    }

    @Override
    public boolean hasErrors() {
        return false;
    }
}
