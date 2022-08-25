package org.blockchain_innovation.accumulate.factombridge.impl;

import org.blockchain_innovation.factom.client.api.rpc.RpcMethod;

public class NotSupportedInAccumulateException extends RuntimeException {
    public NotSupportedInAccumulateException(RpcMethod method) {
        super(String.format("Method \"%s\" is not supported in Accumulate", method.name()));
    }
}
