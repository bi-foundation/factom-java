package org.blockchain_innovation.factom.client.impl.listeners;

import org.blockchain_innovation.factom.client.api.listeners.CommitAndRevealListener;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;

public abstract class CommitAndRevealAdapter implements CommitAndRevealListener {

    @Override
    public void onCompose(ComposeResponse composeResponse) {

    }

    @Override
    public void onCommit(CommitEntryResponse commitResponse) {

    }

    @Override
    public void onCommit(CommitChainResponse commitChainResponse) {

    }

    @Override
    public void onReveal(RevealResponse revealResponse) {

    }

    @Override
    public void onTransactionAcknowledged(EntryTransactionResponse transactionResponse) {

    }

    @Override
    public void onCommitConfirmed(EntryTransactionResponse transactionResponse) {

    }

    @Override
    public void onError(RpcErrorResponse errorResponse) {

    }
}
