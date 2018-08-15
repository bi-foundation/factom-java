package org.blockchain_innovation.factom.client.data.model.rpc;

import org.blockchain_innovation.factom.client.FactomResponse;
import org.blockchain_innovation.factom.client.data.FactomException;

import java.util.List;
import java.util.Map;
/**
 * Callback for asynchronous API call.
 *
 * @param <Result> The return type
 */
public interface Callback<Result> {
    /**
     * This is called when the API call fails.
     *
     * @param e The exception causing the failure
     */
    void onFailure(FactomException e);

    /**
     * This is called when the API call succeeded.
     *
     * @param factomResponse The result deserialized from response
     */
    void onSuccess(FactomResponse<Result> factomResponse);
}