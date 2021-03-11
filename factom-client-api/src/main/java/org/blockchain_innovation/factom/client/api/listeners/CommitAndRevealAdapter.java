package org.blockchain_innovation.factom.client.api.listeners;

import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;

/**
 * An adapter class for the Commit And Reveal Listeners to provide easy implementations when only a small amount
 * of methods is needed
 */
public abstract class CommitAndRevealAdapter implements CommitAndRevealListener {

    @Override
    public void onCompose(ComposeResponse composeResponse) {
        // no-op as this is this is an adapter class where users can override one or more methods
    }

    @Override
    public void onCommit(CommitEntryResponse commitResponse) {
        // no-op as this is this is an adapter class where users can override one or more methods
    }

    @Override
    public void onCommit(CommitChainResponse commitResponse) {
        // no-op as this is this is an adapter class where users can override one or more methods
    }

    @Override
    public void onReveal(RevealResponse revealResponse) {
        // no-op as this is this is an adapter class where users can override one or more methods
    }

    @Override
    public void onTransactionAcknowledged(EntryTransactionResponse transactionResponse) {
        // no-op as this is this is an adapter class where users can override one or more methods
    }

    @Override
    public void onCommitConfirmed(EntryTransactionResponse transactionResponse) {
        // no-op as this is this is an adapter class where users can override one or more methods
    }

    @Override
    public void onError(RpcErrorResponse errorResponse) {
        // no-op as this is this is an adapter class where users can override one or more methods
    }
}
