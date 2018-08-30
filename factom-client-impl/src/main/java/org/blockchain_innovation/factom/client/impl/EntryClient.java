package org.blockchain_innovation.factom.client.impl;

import org.blockchain_innovation.factom.client.api.FactomException;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class EntryClient {

    private final Logger logger = LoggerFactory.getLogger(EntryClient.class);
    private static final int ENTRY_REVEAL_WAIT = 2000;
    private int transactionConfirmationTimeout = 10000;

    private FactomdClient factomdClient;
    private WalletdClient walletdClient;

    private CompletableFuture<Void> waitFuture() {
        return CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(ENTRY_REVEAL_WAIT);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private CompletableFuture<Void> transactionConfirmation(EntryTransactionResponse.Status desiredStatus,  String entryHash, String chainId) {
        return CompletableFuture.runAsync(() -> {
            try {
                int timeout = 1000;
                int maxSeconds = transactionConfirmationTimeout / timeout;
                int seconds = 0;
                while (seconds < maxSeconds) {
                    logger.debug("Transaction verification of chain id={}, entry hash={} at {}", chainId, entryHash, seconds);
                    FactomResponse<EntryTransactionResponse> transactionsResponse = getFactomdClient().ackTransactions(entryHash, chainId, EntryTransactionResponse.class).join();

                    if (!transactionsResponse.hasErrors()) {
                        EntryTransactionResponse entryTransaction = transactionsResponse.getResult();
                        EntryTransactionResponse.Status status = entryTransaction.getCommitData().getStatus();
                        if (seconds > 12 && seconds % 6 == 0 && EntryTransactionResponse.Status.TransactionACK != status) {
                            logger.error("Transaction of chain id={}, entry hash={} still not in desired status after {}, state = {}. Probably will not succeed! ", chainId, entryHash, seconds, status);
                        } else if (desiredStatus == status) {
                            break;
                        }
                    }
                    seconds++;
                    Thread.sleep(timeout);
                }
            } catch (InterruptedException e) {
                throw new FactomException.ClientException("interrupted while waiting on confirmation", e);
            }
        });
    }

    private FactomdClient getFactomdClient() throws FactomException.ClientException {
        if (factomdClient == null) {
            throw new FactomException.ClientException("factomd client not provided");
        }
        return factomdClient;
    }

    private WalletdClient getWalletdClient() throws FactomException.ClientException {
        if (walletdClient == null) {
            throw new FactomException.ClientException("walletd client not provided");
        }
        return walletdClient;
    }

    public EntryClient setFactomdClient(FactomdClient factomdClient) {
        this.factomdClient = factomdClient;
        return this;
    }

    public EntryClient setWalletdClient(WalletdClient walletdClient) {
        this.walletdClient = walletdClient;
        return this;
    }

    public int getTransactionConfirmationTimeout() {
        return transactionConfirmationTimeout;
    }

    public EntryClient setTransactionConfirmationTimeout(int transactionConfirmationTimeout) {
        this.transactionConfirmationTimeout = transactionConfirmationTimeout;
        return this;
    }

    /**
     * Compose, reveal and commit a chain
     *
     * @param chain
     * @param entryCreditAddress
     * @throws FactomException.ClientException
     */
    public CompletableFuture<CommitAndRevealChainResponse> commitAndRevealChain(Chain chain, String entryCreditAddress) throws FactomException.ClientException {
        CompletableFuture<CommitAndRevealChainResponse> commitAndRevealChainFuture =
                // after compose chain combine commit and reveal chain
                composeChainFuture(chain, entryCreditAddress).thenCompose(_composeChainResponse ->
                        // commit chain
                        commitChainFuture(_composeChainResponse).thenCompose(_commitChainResponse ->
                                // wait to transaction is known
                                waitFuture().thenCompose(_void ->
                                        // reveal chain
                                        revealChainFuture(_composeChainResponse).thenApply(_revealChainResponse -> {
                                            // create response
                                            CommitAndRevealChainResponse response = new CommitAndRevealChainResponse();
                                            response.setCommitChainResponse(_commitChainResponse.getResult());
                                            response.setRevealResponse(_revealChainResponse.getResult());
                                            return response;
                                        }))));

        return commitAndRevealChainFuture;
    }

    /**
     * Compose, reveal and commit an entry
     *
     * @param entry
     * @param entryCreditAddress
     * @throws FactomException.ClientException
     */
    public CompletableFuture<CommitAndRevealEntryResponse> commitAndRevealEntry(Entry entry, String entryCreditAddress) throws FactomException.ClientException {
        CompletableFuture<CommitAndRevealEntryResponse> commitAndRevealEntryFuture =
                // after compose entry combine commit and reveal entry
                composeEntryFuture(entry, entryCreditAddress).thenCompose(_composeEntryResponse ->
                        // commit chain
                        commitEntryFuture(_composeEntryResponse).thenCompose(_commitEntryResponse ->
                                // wait to transaction is known
                                waitFuture().thenCompose(_void ->
                                        // reveal chain
                                        revealEntryFuture(_composeEntryResponse).thenApply(_revealEntryResponse -> {
                                            // create response
                                            CommitAndRevealEntryResponse response = new CommitAndRevealEntryResponse();
                                            response.setCommitEntryResponse(_commitEntryResponse.getResult());
                                            response.setRevealResponse(_revealEntryResponse.getResult());
                                            return response;
                                        }))));

        return commitAndRevealEntryFuture;
    }

    private CompletableFuture<FactomResponse<ComposeResponse>> composeChainFuture(Chain chain, String entryCreditAddress) {
        return getWalletdClient().composeChain(chain, entryCreditAddress);
    }

    private CompletableFuture<FactomResponse<CommitChainResponse>> commitChainFuture(FactomResponse<ComposeResponse> composeChain) {
        return getFactomdClient().commitChain(composeChain.getResult().getCommit().getParams().getMessage());
    }

    private CompletableFuture<FactomResponse<RevealResponse>> revealChainFuture(FactomResponse<ComposeResponse> composeChain) {
        return getFactomdClient().revealChain(composeChain.getResult().getReveal().getParams().getEntry());
    }

    private CompletableFuture<FactomResponse<ComposeResponse>> composeEntryFuture(Entry entry, String entryCreditAddress) {
        logger.info("commitEntryFuture");
        return getWalletdClient().composeEntry(entry, entryCreditAddress);
    }

    private CompletableFuture<FactomResponse<CommitEntryResponse>> commitEntryFuture(FactomResponse<ComposeResponse> composeEntry) {
        logger.info("commitEntryFuture");
        return getFactomdClient().commitEntry(composeEntry.getResult().getCommit().getParams().getMessage());
    }

    private CompletableFuture<FactomResponse<RevealResponse>> revealEntryFuture(FactomResponse<ComposeResponse> composeEntry) {
        logger.info("revealEntryFuture");
        return getFactomdClient().revealEntry(composeEntry.getResult().getReveal().getParams().getEntry());
    }
}
