package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.rpc.RpcRequest;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;

import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * The lowlevel client allows you to do direct exchanges (request, responses). This allows you to add additional request/response pairs. It also gives you access to settings and executor services.
 */
public interface LowLevelClient {
    /**
     * Gets the Rpc setting for this client.
     *
     * @return Rpc Settings.
     */
    RpcSettings getSettings();

    /**
     * Sets the Rpc settings for this client. The settings allow you to set urls, usernames, passwords etc.
     *
     * @param settings The Rpc settings
     * @return This client
     */
    LowLevelClient setSettings(RpcSettings settings);

    /**
     * Gets the URL this client connects to.
     *
     * @return The url of factomd/walletd.
     */
    URL getUrl();

    /**
     * Sets the URL this client connects to (factomd/walletd). Normally this is configured from settings.
     *
     * @param url The url of factomd/walletd.
     * @return This client.
     */
    LowLevelClient setUrl(URL url);

    /**
     * Performs the exchange that happens when submitting a request to factomd/walletd. This returns a promise.
     *
     * @param factomRequest  The request to send
     * @param rpcResultClass The result class of the response.
     * @param <RpcResult>    The type of result to expect (response).
     * @return The promise for the response
     */
    <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(FactomRequest factomRequest, Class<RpcResult> rpcResultClass);

    /**
     * Performs the exchange that happens when submitting a request to factomd/walletd. This returns a promise.
     *
     * @param rpcRequestBuilder The request builder that will be build into a request to send
     * @param rpcResultClass    The result class of the response.
     * @param <RpcResult>       The type of result to expect (response).
     * @return The promise for the response
     */
    <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(RpcRequest.Builder rpcRequestBuilder, Class<RpcResult> rpcResultClass);

    /**
     * Performs the exchange that happens when submitting a request to factomd/walletd. This returns a promise. This overload leaves out the FactomRequest wrapper object, since it only has one delegate currently.
     *
     * @param rpcRequest     The Rpc request to send
     * @param rpcResultClass The result class of the response.
     * @param <RpcResult>    The type of result to expect (response).
     * @return The promise for the response
     */
    <RpcResult> CompletableFuture<FactomResponse<RpcResult>> exchange(RpcRequest rpcRequest, Class<RpcResult> rpcResultClass);

    /**
     * Set the executor service for the async parts. Allows you to use your own executor services, or use managed executor services in a JEE environment. If not provided a default executor service will be provided.
     *
     * @param executorService The executor service.
     * @return This client/
     */
    LowLevelClient setExecutorService(ExecutorService executorService);

    /**
     * Get the executor service for the async parts. Allows you to use your own executor services, or use managed executor services in a JEE environment. If not provided a default executor service will be provided.
     *
     * @return The executor service being used.
     */
    ExecutorService getExecutorService();
}
