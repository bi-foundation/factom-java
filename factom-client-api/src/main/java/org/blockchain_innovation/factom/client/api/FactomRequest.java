package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.rpc.RpcRequest;

/**
 * A composition class around the RpcRequest to make the request and response structure symmetric and easily extendable.
 */
public interface FactomRequest {
    /**
     * The Rpc Request object.
     *
     * @return The Rpc request
     */
    RpcRequest getRpcRequest();
}
