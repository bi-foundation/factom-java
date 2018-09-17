package org.blockchain_innovation.factom.client.impl;

import org.blockchain_innovation.factom.client.api.FactomException;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.FactomdClient;
import org.blockchain_innovation.factom.client.api.listeners.CommitAndRevealListener;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class EntryApiImpl {

    private static final int ENTRY_REVEAL_WAIT = 2000;
    private final Logger logger = LoggerFactory.getLogger(EntryApiImpl.class);
    private int transactionAcknowledgeTimeout = 10000; // 10 sec
    private int commitConfirmedTimeout = 15 * 60000; // 15 min

    private FactomdClient factomdClient;
    private WalletdClientImpl walletdClient;

    private List<CommitAndRevealListener> listeners = new ArrayList<>();


    public EntryApiImpl addListener(CommitAndRevealListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
        return this;
    }

    public EntryApiImpl removeListener(CommitAndRevealListener listener) {
        listeners.remove(listener);
        return this;
    }

    public EntryApiImpl clearListeners() {
        listeners.clear();
        return this;
    }

    private FactomdClient getFactomdClient() throws FactomException.ClientException {
        if (factomdClient == null) {
            throw new FactomException.ClientException("factomd client not provided");
        }
        return factomdClient;
    }

    public EntryApiImpl setFactomdClient(FactomdClient factomdClient) {
        this.factomdClient = factomdClient;
        return this;
    }

    private WalletdClientImpl getWalletdClient() throws FactomException.ClientException {
        if (walletdClient == null) {
            throw new FactomException.ClientException("walletd client not provided");
        }
        return walletdClient;
    }

    public EntryApiImpl setWalletdClient(WalletdClientImpl walletdClient) {
        this.walletdClient = walletdClient;
        return this;
    }

    public int getTransactionAcknowledgeTimeout() {
        return transactionAcknowledgeTimeout;
    }

    public EntryApiImpl setTransactionAcknowledgeTimeout(int transactionAcknowledgeTimeout) {
        this.transactionAcknowledgeTimeout = transactionAcknowledgeTimeout;
        return this;
    }

    public int getCommitConfirmedTimeout() {
        return commitConfirmedTimeout;
    }

    public EntryApiImpl setCommitConfirmedTimeout(int commitConfirmedTimeout) {
        this.commitConfirmedTimeout = commitConfirmedTimeout;
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
        return commitAndRevealChain(chain, entryCreditAddress, false);
    }

    /**
     * Compose, reveal and commit a chain
     *
     * @param chain
     * @param entryCreditAddress
     * @return
     */
    public CompletableFuture<CommitAndRevealChainResponse> commitAndRevealChain(Chain chain, String entryCreditAddress, boolean confirmCommit) {
        // after compose chain combine commit and reveal chain
        CompletableFuture<CommitAndRevealChainResponse> commitAndRevealChainFuture = composeChainFuture(chain, entryCreditAddress)
                .thenApply(_composeChainResponse -> notifyCompose(_composeChainResponse))
                // commit chain
                .thenCompose(_composeChainResponse -> commitChainFuture(_composeChainResponse)
                        .thenApply(_commitChainResponse -> notifyChainCommit(_commitChainResponse))
                        // wait to transaction is known
                        .thenCompose(_commitChainResponse -> waitFuture()
                                // reveal chain
                                .thenCompose(_void -> revealChainFuture(_composeChainResponse)
                                        .thenApply(_revealChainResponse -> notifyReveal(_revealChainResponse))
                                        // wait for transaction acknowledgement
                                        .thenCompose(_revealChainResponse -> transactionAcknowledgeConfirmation(_revealChainResponse)
                                                .thenApply(_transactionAcknowledgeResponse -> notifyEntryTransaction(_transactionAcknowledgeResponse))
                                                .thenCompose(_transactionAcknowledgeResponse -> transactionCommitConfirmation(confirmCommit, _revealChainResponse)
                                                        .thenApply(_commitConfirmedResponse -> {
                                                            notifyCommitConfirmed(_commitConfirmedResponse);
                                                            // create response
                                                            CommitAndRevealChainResponse response = new CommitAndRevealChainResponse();
                                                            response.setCommitChainResponse(_commitChainResponse.getResult());
                                                            response.setRevealResponse(_revealChainResponse.getResult());
                                                            return response;
                                                        }))))));
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
        return commitAndRevealEntry(entry, entryCreditAddress, false);
    }

    /**
     * Compose, reveal and commit an entry
     *
     * @param entry
     * @param entryCreditAddress
     * @throws FactomException.ClientException
     */
    public CompletableFuture<CommitAndRevealEntryResponse> commitAndRevealEntry(Entry entry, String entryCreditAddress, boolean confirmCommit) throws FactomException.ClientException {
        // after compose entry combine commit and reveal entry
        CompletableFuture<CommitAndRevealEntryResponse> commitAndRevealEntryFuture = composeEntryFuture(entry, entryCreditAddress)
                .thenApply(_composeEntryResponse -> notifyCompose(_composeEntryResponse))
                // commit chain
                .thenCompose(_composeEntryResponse -> commitEntryFuture(_composeEntryResponse)
                        .thenApply(_commitEntryResponse -> notifyEntryCommit(_commitEntryResponse))
                        // wait to transaction is known
                        .thenCompose(_commitEntryResponse -> waitFuture()
                                // reveal chain
                                .thenCompose(_void -> revealEntryFuture(_composeEntryResponse)
                                        .thenApply(_revealEntryResponse -> notifyReveal(_revealEntryResponse))
                                        // wait for transaction acknowledgement
                                        .thenCompose(_revealEntryResponse -> transactionAcknowledgeConfirmation(_revealEntryResponse)
                                                .thenApply(_transactionAcknowledgeResponse -> notifyEntryTransaction(_transactionAcknowledgeResponse))
                                                // wait for block confirmed
                                                .thenCompose(_transactionAcknowledgeResponse -> transactionCommitConfirmation(confirmCommit, _revealEntryResponse)
                                                        .thenApply(_commitConfirmedResponse -> {
                                                            notifyCommitConfirmed(_commitConfirmedResponse);
                                                            // create response
                                                            CommitAndRevealEntryResponse response = new CommitAndRevealEntryResponse();
                                                            response.setCommitEntryResponse(_commitEntryResponse.getResult());
                                                            response.setRevealResponse(_revealEntryResponse.getResult());
                                                            return response;
                                                        }))))));

        return commitAndRevealEntryFuture;
    }

    private <T> FactomResponse<T> handleResponse(CommitAndRevealListener listener, Consumer<T> listenerCall, FactomResponse<T> response) {
        if (response.hasErrors()) {
            listener.onError(response.getRpcErrorResponse());
        } else {
            listenerCall.accept(response.getResult());
        }
        return response;
    }

    private FactomResponse<ComposeResponse> notifyCompose(FactomResponse<ComposeResponse> response) {
        listeners.forEach(listener -> handleResponse(listener, listener::onCompose, response));
        return response;
    }

    private FactomResponse<CommitEntryResponse> notifyEntryCommit(FactomResponse<CommitEntryResponse> response) {
        listeners.forEach(listener -> handleResponse(listener, listener::onCommit, response));
        return response;
    }

    private FactomResponse<CommitChainResponse> notifyChainCommit(FactomResponse<CommitChainResponse> response) {
        listeners.forEach(listener -> handleResponse(listener, listener::onCommit, response));
        return response;
    }

    private FactomResponse<RevealResponse> notifyReveal(FactomResponse<RevealResponse> response) {
        listeners.forEach(listener -> handleResponse(listener, listener::onReveal, response));
        return response;
    }

    private FactomResponse<EntryTransactionResponse> notifyEntryTransaction(FactomResponse<EntryTransactionResponse> response) {
        listeners.forEach(listener -> handleResponse(listener, listener::onTransactionAcknowledged, response));
        return response;
    }

    private FactomResponse<EntryTransactionResponse> notifyCommitConfirmed(FactomResponse<EntryTransactionResponse> response) {
        listeners.forEach(listener -> handleResponse(listener, listener::onCommitConfirmed, response));
        return response;
    }

    private CompletableFuture<Void> waitFuture() {
        return CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(ENTRY_REVEAL_WAIT);
            } catch (InterruptedException e) {
                throw new FactomException.ClientException("interrupted while waiting on confirmation", e);
            }
        });
    }

    private CompletionStage<FactomResponse<EntryTransactionResponse>> transactionAcknowledgeConfirmation(FactomResponse<RevealResponse> revealChainResponse) {
        String entryHash = revealChainResponse.getResult().getEntryHash();
        String chainId = revealChainResponse.getResult().getChainId();
        return transactionConfirmation(entryHash, chainId, Arrays.asList(EntryTransactionResponse.Status.TransactionACK, EntryTransactionResponse.Status.DBlockConfirmed), transactionAcknowledgeTimeout, 1000);
    }

    private CompletableFuture<FactomResponse<EntryTransactionResponse>> transactionCommitConfirmation(boolean waitForConfirmation, FactomResponse<RevealResponse> revealChainResponse) {
        String entryHash = revealChainResponse.getResult().getEntryHash();
        String chainId = revealChainResponse.getResult().getChainId();
        if (waitForConfirmation) {
            return transactionConfirmation(entryHash, chainId, Arrays.asList(EntryTransactionResponse.Status.DBlockConfirmed), commitConfirmedTimeout, 60000);
        } else {
            return getFactomdClient().ackTransactions(entryHash, chainId, EntryTransactionResponse.class);
        }
    }

    private CompletableFuture<FactomResponse<EntryTransactionResponse>> transactionConfirmation(String entryHash, String chainId, List<EntryTransactionResponse.Status> desiredStatus, int timeout, int sleepTime) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FactomResponse<EntryTransactionResponse> transactionsResponse = null;
                boolean confirmed = false;
                int maxSeconds = timeout / sleepTime;
                int seconds = 0;
                while (!confirmed && seconds < maxSeconds) {
                    logger.debug("Transaction verification of chain id={}, entry hash={} at {}", chainId, entryHash, seconds);
                    transactionsResponse = getFactomdClient().ackTransactions(entryHash, chainId, EntryTransactionResponse.class).join();

                    if (!transactionsResponse.hasErrors()) {
                        confirmed = desiredStatus.contains(transactionsResponse.getResult().getCommitData().getStatus());
                    }
                    Thread.sleep(sleepTime);
                    seconds++;
                }

                if (transactionsResponse == null) {
                    throw new FactomException.ClientException(String.format("Transaction of chain id=%s, entry hash=%s didn't return a response after %s. Probably will not succeed! ", chainId, entryHash, seconds));
                } else if (transactionsResponse.hasErrors()) {
                    logger.error("Transaction of chain id={}, entry hash={} received error after {}, errors={}. Probably will not succeed! ", chainId, entryHash, seconds, transactionsResponse.getRpcErrorResponse());
                } else if (!confirmed) {
                    EntryTransactionResponse.Status status = transactionsResponse.getResult().getCommitData().getStatus();
                    logger.error("Transaction of chain id={}, entry hash={} still not in desired status after {}, state = {}. Probably will not succeed! ", chainId, entryHash, seconds, status);
                }
                return transactionsResponse;
            } catch (InterruptedException e) {
                throw new FactomException.ClientException("interrupted while waiting on confirmation", e);
            }
        });
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
