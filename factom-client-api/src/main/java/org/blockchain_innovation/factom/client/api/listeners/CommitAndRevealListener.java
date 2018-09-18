package org.blockchain_innovation.factom.client.api.listeners;

import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;

// TODO: 19/09/2018 We need access to the RpcResponse class for ID correlation or add that as argument here
public interface CommitAndRevealListener {

    /**
     * Fired whenever a compose response is received.
     *
     * @param composeResponse The compose response
     */
    void onCompose(ComposeResponse composeResponse);

    /**
     * Fired whenever a commit entry response is received
     *
     * @param commitResponse The commit entry response
     */
    void onCommit(CommitEntryResponse commitResponse);

    /**
     * Fired whenever a commit chain response is received
     *
     * @param commitResponse The commit chain response
     */
    void onCommit(CommitChainResponse commitResponse);

    /**
     * Fired whenever a reveal response is received
     *
     * @param revealResponse The reveal response
     */
    void onReveal(RevealResponse revealResponse);

    /**
     * Fired whenever a transaction response is received on acknowledgement of the transaction
     *
     * @param transactionResponse The transaction response
     */
    void onTransactionAcknowledged(EntryTransactionResponse transactionResponse);

    /**
     * Fired whenever a transaction response is received on commit of the transaction
     *
     * @param transactionResponse The transaction response
     */
    void onCommitConfirmed(EntryTransactionResponse transactionResponse);

    /**
     * Fired upon an error response
     *
     * @param errorResponse The error response
     */
    void onError(RpcErrorResponse errorResponse);
}
