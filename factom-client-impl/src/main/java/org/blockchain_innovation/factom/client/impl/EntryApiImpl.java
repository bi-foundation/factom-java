package org.blockchain_innovation.factom.client.impl;

import org.blockchain_innovation.factom.client.api.*;
import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.listeners.CommitAndRevealListener;
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.*;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;
import org.blockchain_innovation.factom.client.api.ops.StringUtils;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Named
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods"})
public class EntryApiImpl extends AbstractClient implements EntryApi {

    public static final String NO_PREVIOUS_KEY_MERKLE_ROOT = "0000000000000000000000000000000000000000000000000000000000000000";
    private static final int ENTRY_REVEAL_WAIT = 2000;
    private static Logger logger = LogFactory.getLogger(EntryApiImpl.class);
    private int transactionAcknowledgeTimeout = 10000; // 10 sec
    private int commitConfirmedTimeout = 15 * 60000; // 15 min

    private FactomdClient factomdClient;
    private WalletdClient walletdClient;
    private final EntryOperations entryOperations = new EntryOperations();

    private final List<CommitAndRevealListener> listeners = new ArrayList<>();

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

    @Override
    public FactomdClient getFactomdClient() throws FactomException.ClientException {
        if (factomdClient == null) {
            throw new FactomException.ClientException("factomd client not provided");
        }
        return factomdClient;
    }

    @Override
    public EntryApiImpl setFactomdClient(FactomdClient factomdClient) {
        this.factomdClient = factomdClient;
        return this;
    }

    @Override
    public WalletdClient getWalletdClient() throws FactomException.ClientException {
        if (walletdClient == null) {
            throw new FactomException.ClientException("walletd client not provided");
        }
        return walletdClient;
    }

    @Override
    public EntryApiImpl setWalletdClient(WalletdClient walletdClient) {
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
     * Compose, reveal and commit a chain.
     *
     * @param chain
     * @param address
     * @throws FactomException.ClientException
     */
    public CompletableFuture<CommitAndRevealChainResponse> commitAndRevealChain(Chain chain, Address address) throws FactomException.ClientException {
        return commitAndRevealChain(chain, address, false);
    }

    public CompletableFuture<Boolean> chainExists(Chain chain) {
        String chainId = Encoding.HEX.encode(entryOperations.calculateChainId(chain.getFirstEntry().getExternalIds()));
        return factomdClient.chainHead(chainId)
                .thenApplyAsync(response -> response.getResult() != null &&
                        StringUtils.isNotEmpty(response.getResult().getChainHead()));
    }

    /**
     * @param chainId
     * @return list of all EntryBlocks within a certain chain up till genesis block
     */
    @Override
    public CompletableFuture<List<EntryBlockResponse>> allEntryBlocks(String chainId) {
        return entryBlocksUpTilKeyMR(factomdClient.chainHead(chainId).join().getResult().getChainHead());
    }

    /**
     * All entry blocks of a chain
     *
     * @param chainId
     * @return
     */
    @Override
    public CompletableFuture<List<EntryBlockResponse.Entry>> allEntryBlocksEntries(String chainId) {
        return entryBlocksEntriesUpTilKeyMR(factomdClient.chainHead(chainId).join().getResult().getChainHead());
    }

    @Override
    public CompletableFuture<List<EntryResponse>> allEntries(String chainId) {
        return entriesUpTilKeyMR(factomdClient.chainHead(chainId).join().getResult().getChainHead());
    }

    @Override
    public CompletableFuture<List<EntryResponse>> entriesUpTilKeyMR(String keyMR) {
        List<EntryBlockResponse.Entry> entriesUpTilKeyMR = entryBlocksEntriesUpTilKeyMR(keyMR).join();
        List<EntryResponse> entries = entriesUpTilKeyMR.stream().map(entryInfo -> factomdClient.entry(entryInfo.getEntryHash()).join().getResult()).collect(Collectors.toList());
        CompletableFuture<List<EntryResponse>> completableFuture = new CompletableFuture<>();
        completableFuture.complete(entries);
        return completableFuture;
    }


    @Override
    public CompletableFuture<List<EntryBlockResponse>> entryBlocksUpTilKeyMR(String keyMR) {
        List<EntryBlockResponse> entryBlockResponseList = new ArrayList<>();

        String currentKeyMR = keyMR;

        while (!currentKeyMR.equals(NO_PREVIOUS_KEY_MERKLE_ROOT)) {
            EntryBlockResponse currentBlock = factomdClient.entryBlockByKeyMerkleRoot(currentKeyMR).join().getResult();
            currentKeyMR = currentBlock.getHeader().getPreviousKeyMR();
            entryBlockResponseList.add(currentBlock);
        }
        CompletableFuture<List<EntryBlockResponse>> completableFuture = new CompletableFuture<>();
        completableFuture.complete(entryBlockResponseList);
        return completableFuture;
    }


    /**
     * List of all entry hashes and timestamps uptil a certain key merkle root.
     *
     * @param keyMR
     * @return
     */
    @Override
    public CompletableFuture<List<EntryBlockResponse.Entry>> entryBlocksEntriesUpTilKeyMR(String keyMR) {
        List<EntryBlockResponse> entryBlocksUpTilKeyMR = entryBlocksUpTilKeyMR(keyMR).join();
        List<EntryBlockResponse.Entry> entries = new ArrayList<>();
        entryBlocksUpTilKeyMR.stream().map(EntryBlockResponse::getEntryList).forEach(entries::addAll);
        CompletableFuture<List<EntryBlockResponse.Entry>> completableFuture = new CompletableFuture<>();
        completableFuture.complete(entries);
        return completableFuture;
    }

    /**
     * Compose, reveal and commit a chain.
     *
     * @param chain
     * @param address
     * @return
     */
    public CompletableFuture<CommitAndRevealChainResponse> commitAndRevealChain(Chain chain, Address address, boolean confirmCommit) {
        // after compose chain combine commit and reveal chain
        CompletableFuture<CommitAndRevealChainResponse> commitAndRevealChainFuture = composeChainFuture(chain, address)
                .thenApplyAsync(_composeChainResponse -> notifyCompose(_composeChainResponse), executorService())
                // commit chain
                .thenComposeAsync(_composeChainResponse -> commitChainFuture(_composeChainResponse)
                        .thenApplyAsync(_commitChainResponse -> notifyChainCommit(_commitChainResponse), executorService())
                        // wait to transaction is known
                        .thenComposeAsync(_commitChainResponse -> waitFuture()
                                // reveal chain
                                .thenComposeAsync(_void -> revealChainFuture(_composeChainResponse)
                                        .thenApplyAsync(_revealChainResponse -> notifyReveal(_revealChainResponse), executorService())
                                        // wait for transaction acknowledgement
                                        .thenComposeAsync(_revealChainResponse -> transactionAcknowledgeConfirmation(_revealChainResponse)
                                                .thenApplyAsync(_transactionAcknowledgeResponse -> notifyEntryTransaction(_transactionAcknowledgeResponse), executorService())
                                                .thenComposeAsync(_transactionAcknowledgeResponse -> transactionCommitConfirmation(confirmCommit, _revealChainResponse)
                                                        .thenApplyAsync(_commitConfirmedResponse -> {
                                                            notifyCommitConfirmed(_commitConfirmedResponse);
                                                            // create response
                                                            CommitAndRevealChainResponse response = new CommitAndRevealChainResponse();
                                                            response.setCommitChainResponse(_commitChainResponse.getResult());
                                                            response.setRevealResponse(_revealChainResponse.getResult());
                                                            return response;
                                                        }, executorService()), executorService()), executorService()), executorService()), executorService()), executorService());
        return commitAndRevealChainFuture;
    }

    /**
     * Compose, reveal and commit an entry.
     *
     * @param entry
     * @param address
     * @throws FactomException.ClientException
     */
    public CompletableFuture<CommitAndRevealEntryResponse> commitAndRevealEntry(Entry entry, Address address) throws FactomException.ClientException {
        return commitAndRevealEntry(entry, address, false);
    }

    /**
     * Compose, reveal and commit an entry.
     *
     * @param entry
     * @param address
     * @throws FactomException.ClientException
     */
    public CompletableFuture<CommitAndRevealEntryResponse> commitAndRevealEntry(Entry entry, Address address, boolean confirmCommit) throws FactomException.ClientException {
        // after compose entry combine commit and reveal entry
        CompletableFuture<CommitAndRevealEntryResponse> commitAndRevealEntryFuture = composeEntryFuture(entry, address)
                .thenApplyAsync(_composeEntryResponse -> notifyCompose(_composeEntryResponse), executorService())
                // commit chain
                .thenComposeAsync(_composeEntryResponse -> commitEntryFuture(_composeEntryResponse)
                                .thenApplyAsync(_commitEntryResponse -> notifyEntryCommit(_commitEntryResponse), executorService())
                                // wait to transaction is known
                                .thenComposeAsync(_commitEntryResponse -> waitFuture()
                                                // reveal chain
                                                .thenComposeAsync(_void -> revealEntryFuture(_composeEntryResponse)
                                                                .thenApplyAsync(_revealEntryResponse -> notifyReveal(_revealEntryResponse), executorService())
                                                                // wait for transaction acknowledgement
                                                                .thenComposeAsync(_revealEntryResponse -> transactionAcknowledgeConfirmation(_revealEntryResponse)
                                                                                .thenApplyAsync(_transactionAcknowledgeResponse ->
                                                                                        notifyEntryTransaction(_transactionAcknowledgeResponse), executorService())
                                                                                // wait for block confirmed
                                                                                .thenComposeAsync(_transactionAcknowledgeResponse ->
                                                                                                transactionCommitConfirmation(confirmCommit, _revealEntryResponse)
                                                                                                        .thenApplyAsync(_commitConfirmedResponse -> {
                                                                                                            notifyCommitConfirmed(_commitConfirmedResponse);
                                                                                                            // create response
                                                                                                            CommitAndRevealEntryResponse response = new CommitAndRevealEntryResponse();
                                                                                                            response.setCommitEntryResponse(_commitEntryResponse.getResult());
                                                                                                            response.setRevealResponse(_revealEntryResponse.getResult());
                                                                                                            return response;
                                                                                                        }, executorService()),
                                                                                        executorService()),
                                                                        executorService()),
                                                        executorService()),
                                        executorService()),
                        executorService()
                );

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
                Thread.currentThread().interrupt();
                throw new FactomException.ClientException("interrupted while waiting on confirmation", e);
            }
        }, executorService());
    }

    private ExecutorService executorService() {
        return client().getExecutorService();
    }

    private LowLevelClient client() {
        return (LowLevelClient) factomdClient;
    }

    private CompletionStage<FactomResponse<EntryTransactionResponse>> transactionAcknowledgeConfirmation(FactomResponse<RevealResponse> revealChainResponse) {
        String entryHash = revealChainResponse.getResult().getEntryHash();
        String chainId = revealChainResponse.getResult().getChainId();
        List<EntryTransactionResponse.Status> desiredStatus = Arrays.asList(EntryTransactionResponse.Status.TransactionACK, EntryTransactionResponse.Status.DBlockConfirmed);
        return transactionConfirmation(entryHash, chainId, desiredStatus, transactionAcknowledgeTimeout, 1000);
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

    private CompletableFuture<FactomResponse<EntryTransactionResponse>> transactionConfirmation(String entryHash, String chainId,
                                                                                                List<EntryTransactionResponse.Status> desiredStatus,
                                                                                                int timeout, int sleepTime) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FactomResponse<EntryTransactionResponse> transactionsResponse = null;
                boolean confirmed = false;
                int maxSeconds = timeout / sleepTime;
                int seconds = 0;
                while (!confirmed && seconds < maxSeconds) {
                    logger.debug("Transaction verification of chain id=%s, entry hash=%s at %d", chainId, entryHash, seconds);
                    transactionsResponse = getFactomdClient().ackTransactions(entryHash, chainId, EntryTransactionResponse.class).join();

                    if (!transactionsResponse.hasErrors()) {
                        confirmed = desiredStatus.contains(transactionsResponse.getResult().getCommitData().getStatus());
                    }
                    Thread.sleep(sleepTime);
                    seconds++;
                }

                if (transactionsResponse == null) {
                    throw new FactomException.ClientException(String.format("Transaction of chain id=%s, entry hash=%s didn't return a response after %s. " +
                            "Probably will not succeed! ", chainId, entryHash, seconds));
                } else if (transactionsResponse.hasErrors()) {
                    logger.error("Transaction of chain id=%s, entry hash=%s received error after %d, errors=%s. Probably will not succeed! ",
                            chainId, entryHash, seconds, transactionsResponse.getRpcErrorResponse());
                } else if (!confirmed) {
                    EntryTransactionResponse.Status status = transactionsResponse.getResult().getCommitData().getStatus();
                    logger.error("Transaction of chain id=%s, entry hash=%s still not in desired status after %d, state = %s. Probably will not succeed! ",
                            chainId, entryHash, seconds, status);
                }
                return transactionsResponse;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new FactomException.ClientException("interrupted while waiting on confirmation", e);
            }
        }, executorService());
    }

    private CompletableFuture<FactomResponse<ComposeResponse>> composeChainFuture(Chain chain, Address address) {
        return getWalletdClient().composeChain(chain, address);
    }

    private CompletableFuture<FactomResponse<CommitChainResponse>> commitChainFuture(FactomResponse<ComposeResponse> composeChain) {
        return getFactomdClient().commitChain(composeChain.getResult().getCommit().getParams().getMessage());
    }

    private CompletableFuture<FactomResponse<RevealResponse>> revealChainFuture(FactomResponse<ComposeResponse> composeChain) {
        return getFactomdClient().revealChain(composeChain.getResult().getReveal().getParams().getEntry());
    }

    private CompletableFuture<FactomResponse<ComposeResponse>> composeEntryFuture(Entry entry, Address address) {
        logger.info("commitEntryFuture");
        return getWalletdClient().composeEntry(entry, address);
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
