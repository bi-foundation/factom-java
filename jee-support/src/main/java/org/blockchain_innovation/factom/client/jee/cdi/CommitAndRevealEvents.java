package org.blockchain_innovation.factom.client.jee.cdi;

import org.blockchain_innovation.factom.client.api.listeners.CommitAndRevealListener;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.Serializable;

public class CommitAndRevealEvents implements CommitAndRevealListener, Serializable {

    @Inject
    private Event<ComposeResponse> composeResponseEvent;

    @Inject
    private Event<CommitEntryResponse> commitEntryResponseEvent;

    @Inject
    private Event<CommitChainResponse> commitChainResponseEvent;

    @Inject
    private Event<EntryTransactionResponse> entryTransactionResponseEvent;

    @Inject
    private Event<RevealResponse> revealResponseEvent;

    @Inject
    private Event<RpcErrorResponse> errorResponseEvent;

    @Override
    public void onCompose(ComposeResponse composeResponse) {
        composeResponseEvent.fire(composeResponse);
    }

    @Override
    public void onCommit(CommitEntryResponse commitResponse) {
        commitEntryResponseEvent.fire(commitResponse);
    }

    @Override
    public void onCommit(CommitChainResponse commitResponse) {
        commitChainResponseEvent.fire(commitResponse);
    }

    @Override
    public void onReveal(RevealResponse revealResponse) {
        revealResponseEvent.fire(revealResponse);
    }

    @Override
    public void onTransactionAcknowledged(EntryTransactionResponse transactionResponse) {
        entryTransactionResponseEvent.fire(transactionResponse);
    }

    @Override
    public void onCommitConfirmed(EntryTransactionResponse transactionResponse) {
        // TODO: 18/09/2018 Add qualifiers
        entryTransactionResponseEvent.fire(transactionResponse);
    }

    @Override
    public void onError(RpcErrorResponse e) {
        errorResponseEvent.fire(e);
    }
}
