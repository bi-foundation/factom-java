package org.blockchain_innovation.factom.client.api;


import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.listeners.CommitAndRevealListener;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryBlockResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EntryApi {

     EntryApi addListener(CommitAndRevealListener listener);

     EntryApi removeListener(CommitAndRevealListener listener);

     EntryApi clearListeners();

    FactomdClient getFactomdClient() throws FactomException.ClientException;

    EntryApi setFactomdClient(FactomdClient factomdClient);

    WalletdClient getWalletdClient() throws FactomException.ClientException;

    EntryApi setWalletdClient(WalletdClient walletdClient);

     int getTransactionAcknowledgeTimeout();

     EntryApi setTransactionAcknowledgeTimeout(int transactionAcknowledgeTimeout);

     int getCommitConfirmedTimeout();

     EntryApi setCommitConfirmedTimeout(int commitConfirmedTimeout);

    CompletableFuture<List<EntryBlockResponse>> allEntryBlocks (String chainId);

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

    CompletableFuture<List<EntryBlockResponse.Entry>> allEntryBlocksEntries(String chainId);

    CompletableFuture<List<EntryResponse>> allEntries(String chainId);

    CompletableFuture<List<EntryResponse>> entriesUpTilKeyMR(String keyMR);

    CompletableFuture<List<EntryBlockResponse>> entryBlocksUpTilKeyMR(String keyMR);

    CompletableFuture<List<EntryBlockResponse.Entry>> entryBlocksEntriesUpTilKeyMR(String keyMR);

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
