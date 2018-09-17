package org.blockchain_innovation.factom.client.api.listeners;

import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;

public interface CommitAndRevealListener {

    void onCompose(ComposeResponse composeResponse);

    void onCommit(CommitEntryResponse commitResponse);

    void onCommit(CommitChainResponse commitResponse);

    void onReveal(RevealResponse revealResponse);

    void onTransactionAcknowledged(EntryTransactionResponse transactionResponse);

    void onCommitConfirmed(EntryTransactionResponse transactionResponse);

    void onError(RpcErrorResponse e);
}
