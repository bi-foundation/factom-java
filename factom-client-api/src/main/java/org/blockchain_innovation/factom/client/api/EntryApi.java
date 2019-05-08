package org.blockchain_innovation.factom.client.api;


import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.listeners.CommitAndRevealListener;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryBlockResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EntryApi {

    EntryApi addListener(CommitAndRevealListener listener);

    EntryApi removeListener(CommitAndRevealListener listener);

    EntryApi clearListeners();

    EntryApi setFactomdClient(FactomdClient factomdClient);

    EntryApi setWalletdClient(WalletdClient walletdClient);

    int getTransactionAcknowledgeTimeout();

    EntryApi setTransactionAcknowledgeTimeout(int transactionAcknowledgeTimeout);

    int getCommitConfirmedTimeout();

    EntryApi setCommitConfirmedTimeout(int commitConfirmedTimeout);

    CompletableFuture<List<EntryBlockResponse>> allEntryBlocks(String chainId);

    /**
     * Compose, reveal and commit a chain.
     *
     * @param chain
     * @param address
     * @throws FactomException.ClientException
     */
    CompletableFuture<CommitAndRevealChainResponse> commitAndRevealChain(Chain chain, Address address);

    /**
     * Check whether a chain exists.
     *
     * @param chain
     */
    CompletableFuture<Boolean> chainExists(Chain chain);

    /**
     * Compose, reveal and commit a chain.
     *
     * @param chain
     * @param address
     * @return
     */
    CompletableFuture<CommitAndRevealChainResponse> commitAndRevealChain(Chain chain, Address address, boolean confirmCommit);

    /**
     * Compose, reveal and commit an entry.
     *
     * @param entry
     * @param address
     * @throws FactomException.ClientException
     */
    CompletableFuture<CommitAndRevealEntryResponse> commitAndRevealEntry(Entry entry, Address address);

    /**
     * Compose, reveal and commit an entry.
     *
     * @param entry
     * @param address
     * @throws FactomException.ClientException
     */
    CompletableFuture<CommitAndRevealEntryResponse> commitAndRevealEntry(Entry entry, Address address, boolean confirmCommit);
}
