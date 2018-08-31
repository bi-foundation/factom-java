package org.blockchain_innovation.factom.client.impl.listeners;

import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;

interface CommitAndRevealListener<T> {

    void onCompose(ComposeResponse composeResponse);

    void onCommit(T commitResponse);

    void onReveal(RevealResponse revealResponse);

    void onTransactionAcknowledged(EntryTransactionResponse transactionResponse);

    void onCommitConfirmed(EntryTransactionResponse transactionResponse);

    void onError(RpcErrorResponse e);
}
