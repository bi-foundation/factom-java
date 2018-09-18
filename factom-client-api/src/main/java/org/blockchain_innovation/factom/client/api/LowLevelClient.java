package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.rpc.RpcRequest;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;

import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface LowLevelClient {
    RpcSettings getSettings();

    LowLevelClient setSettings(RpcSettings settings);

    URL getUrl();

    LowLevelClient setUrl(URL url);

    <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(FactomRequest factomRequest, Class<RpcResult> rpcResultClass);

    <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(RpcRequest.Builder rpcRequestBuilder, Class<RpcResult> rpcResultClass);

    <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(RpcRequest rpcRequest, Class<RpcResult> rpcResultClass);

    LowLevelClient setExecutorService(ExecutorService executorService);

    ExecutorService getExecutorService();
}
