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

package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcResponse;

/**
 * A composition class around the RpcResponse to get access to different values from a server response,
 * like http codes, errors, results.
 *
 * @param <Result> type of result in responses
 */
public interface FactomResponse<Result> {
    /**
     * Get the RPC response object that represents the result of a API call.
     *
     * @return The Rpc Response
     */
    RpcResponse getRpcResponse();

    /**
     * The result object deserialized.
     *
     * @return The result in the RpcResponse
     */
    Result getResult();

    /**
     * The deserialized error response.
     *
     * @return The error response
     */
    RpcErrorResponse getRpcErrorResponse();

    /**
     * Gets the status code from an HTTP response message.
     *
     * @return the HTTP Response Status-Code, or -1 of the response is not from a HTTP request (rare)
     */
    int getHTTPResponseCode();

    /**
     * Gets the HTTP response message, if any, returned along with the response code from a server.
     * From responses like:
     * <PRE>
     * HTTP/1.0 200 OK
     * HTTP/1.0 404 Not Found
     * </PRE>
     * Extracts the Strings "OK" and "Not Found" respectively.
     * Returns null if none could be discerned from the responses (the result was not valid HTTP).
     *
     * @return the HTTP response message, or {@code null}
     */
    String getHTTPResponseMessage();

    /**
     * Checks if the Rpc response contains errors.
     *
     * @return if the Response contains errors
     */
    boolean hasErrors();
}
