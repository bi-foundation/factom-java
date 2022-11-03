package org.blockchain_innovation.accumulate.factombridge.impl;

import com.iwebpp.crypto.TweetNaclFast;
import io.accumulatenetwork.sdk.api.v2.AccumulateAsyncApi;
import io.accumulatenetwork.sdk.api.v2.TransactionQueryResult;
import io.accumulatenetwork.sdk.commons.codec.DecoderException;
import io.accumulatenetwork.sdk.commons.codec.binary.Hex;
import io.accumulatenetwork.sdk.generated.apiv2.DataEntryQuery;
import io.accumulatenetwork.sdk.generated.apiv2.DataEntrySetQuery;
import io.accumulatenetwork.sdk.generated.apiv2.TransactionQueryResponse;
import io.accumulatenetwork.sdk.generated.apiv2.TxnQuery;
import io.accumulatenetwork.sdk.generated.protocol.FactomDataEntry;
import io.accumulatenetwork.sdk.generated.protocol.SignatureType;
import io.accumulatenetwork.sdk.generated.protocol.WriteDataTo;
import io.accumulatenetwork.sdk.generated.query.ResponseDataEntry;
import io.accumulatenetwork.sdk.protocol.*;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.blockchain_innovation.accumulate.factombridge.model.LiteAccount;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.Key;
import org.blockchain_innovation.factom.client.api.model.response.factomd.*;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;
import org.blockchain_innovation.factom.client.api.rpc.RpcResponse;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FactomToAccumulateBridge {
    private static final String MESSAGE_CHAIN_COMMIT_SUCCESS = "Chain Commit Success";
    private static final String MESSAGE_CHAIN_REVEAL_SUCCESS = "Chain Reveal Success";
    private static final String MESSAGE_ENTRY_COMMIT_SUCCESS = "Entry Commit Success";
    private static final String MESSAGE_ENTRY_REVEAL_SUCCESS = "Entry Reveal Success";
    private static final String MESSAGE_GET_ENTRY_SUCCESS = "Get Entry Success";
    private static final String MESSAGE_QUERY_CHAIN_SUCCESS = "Query Chain Success";
    private static final Logger logger = LogFactory.getLogger(FactomToAccumulateBridge.class);
    private static final TxID NULL_TX_ID = new TxID(Url.toAccURL("acc://0@0"));
    private static final Map<Key, LiteAccount> liteAccountCache = ExpiringMap.builder()
            .maxSize(150000)
            .expiration(24, TimeUnit.HOURS)
            .build();
    private static final Map<Key, TxID> txIdCache = ExpiringMap.builder()
            .maxSize(150000)
            .expiration(20, TimeUnit.MINUTES)
            .build();

    private static final Map<Url, Map<TxID, TxID>> previousTxIdCache = ExpiringMap.builder()
            .maxSize(150000)
            .expiration(20, TimeUnit.MINUTES)
            .build();
    private final EntryOperations entryOperations = new EntryOperations();

    private AccumulateAsyncApi accumulateApi;

    FactomToAccumulateBridge() {
    }

    void configure(final RpcSettings settings) {
        switch (settings.getSubSystem()) {
            case FACTOMD:
                try {
                    accumulateApi = new AccumulateAsyncApi(settings.getServer().getURL().toURI());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                break;
            case WALLETD:
                break;
        }
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> commitChain(final String message) {
        /**
         *

         OUT
         rpcResponse = {RpcResponse@2754}
         id = 0
         jsonrpc = "2.0"
         result = {CommitChainResponse@2757}
         chainidhash = "f59265cbbc65416e31324f78179691700d0dd56d72c8ee3f33f6935cd2f38cbb"
         message = "Chain Commit Success"
         txid = "616826fbd10385f709aaef8919c1abbb77e1aae81294b11e2b112568fb8b960e"
         entryhash = "4caa1c0b0443df2345428e7f0763a155afad1dc3dc7687add78883ed153d542c"
         */

        return CompletableFuture.supplyAsync(() -> {
            try {
                final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(Hex.decodeHex(message)));
                final byte version = inputStream.readByte(); // TODO cleanup unused stuff here
                final byte[] timeStamp = new byte[6];
                inputStream.read(timeStamp);
                final byte[] chainIdHash = new byte[32];
                inputStream.read(chainIdHash);
                final byte[] chainWeld = new byte[32];
                inputStream.read(chainWeld);
                final byte[] entryHash = new byte[32];
                inputStream.read(entryHash);

                final byte[] privateKey = new byte[64];
                inputStream.read(privateKey);
                final LiteAccount liteAccount = liteAccountFromPrivateKey(privateKey);
                final CommitChainResponse commitChainResponse = new CommitChainResponse(
                        MESSAGE_CHAIN_COMMIT_SUCCESS,
                        "NA",
                        Hex.encodeHexString(entryHash),
                        Hex.encodeHexString(chainIdHash));

                final Key entryHashKey = new Key(entryHash);
                liteAccountCache.put(entryHashKey, liteAccount);
                final RpcResult rpcResult = (RpcResult) commitChainResponse;
                return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, MESSAGE_CHAIN_COMMIT_SUCCESS);
            } catch (DecoderException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> revealChain(String revealMessage, boolean logErrors) {
        final Entry firstEntry = decodeEntry(revealMessage);
        final byte[] entryHash = entryOperations.calculateEntryHash(firstEntry.getExternalIds(), firstEntry.getContent(), firstEntry.getChainId());
        final Key entryHashKey = new Key(entryHash);
        final LiteAccount liteAccount = liteAccountCache.remove(entryHashKey);
        if (liteAccount == null) {
            throw new RuntimeException(String.format("LiteAccount could not be found for entryHash %s, call commitChain first", Hex.encodeHexString(entryHash)));
        }

        final FactomEntry factomEntry = new FactomEntry(firstEntry.getContent());
        firstEntry.getExternalIds().forEach(extId -> {
            factomEntry.addExtRef(extId);
        });

        final CompletableFuture<FactomResponse<RpcResult>> futureResponse = accumulateApi.createLiteDataAccount(liteAccount, factomEntry)
                .thenApply(writeDataResult -> {
                    txIdCache.put(entryHashKey, writeDataResult.getTxID());
                    final RevealResponse revealChainResponse = new RevealResponse(MESSAGE_ENTRY_REVEAL_SUCCESS,
                            Hex.encodeHexString(entryHash), firstEntry.getChainId());
                    final RpcResult rpcResult = (RpcResult) revealChainResponse;
                    return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, MESSAGE_CHAIN_REVEAL_SUCCESS);
                });
        //   if (logErrors) {
        futureResponse.exceptionally(throwable -> {
            logger.error(String.format("revealChain failed for chain ID %s", firstEntry.getChainId()), throwable);
            return null;
        });
        //   }
        return futureResponse;
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> ackTransaction(final String chainId, final String hash, final boolean logErrors) {
        // We need to forge a FactoidTransactionsResponse out of this
        try {
            final Key entryHashKey = new Key(Hex.decodeHex(hash));
            final TxID txId = txIdCache.get(entryHashKey);
            if (txId == null) {
                throw new RuntimeException("Could not lookup the TxID for entry hash " + hash);
            }

            final CompletableFuture<FactomResponse<RpcResult>> futureResponse = accumulateApi.getTx(new TxnQuery()
                            .txIdUrl(txId)
                            .wait(Duration.ofSeconds(2))) // Configurable?
                    .thenApply(txResult -> {
                        final TransactionQueryResponse queryResponse = txResult.getQueryResponse();

                        EntryTransactionResponse.Status status;
                        switch (queryResponse.getStatus().getCode()) {
                            case OK:
                            case DELIVERED:
                                if (allDelivered(txResult.getQueryResponse().getProduced())) {
                                    status = EntryTransactionResponse.Status.DBlockConfirmed;
                                } else {
                                    // Accumulate's pending state is short, but the entry is not queryable until the produced synth txns have been synced
                                    status = EntryTransactionResponse.Status.NotConfirmed;
                                }
                                break;
/*
                            case PENDING:
                                status = EntryTransactionResponse.Status.TransactionACK;
                                break;
*/
                            case REMOTE:
                                status = EntryTransactionResponse.Status.Unknown;
                                break;
                            default:
                                status = EntryTransactionResponse.Status.NotConfirmed;
                                break;
                        }
                        final String entryHash = Hex.encodeHexString(queryResponse.getTransactionHash());
                        final EntryTransactionResponse.EntryData entryData = new EntryTransactionResponse.EntryData(status.name());
                        final EntryTransactionResponse entryTransactionResponse = new EntryTransactionResponse(
                                txId.toString(), entryHash, new EntryTransactionResponse.CommitData(status), entryData);

                        final RpcResult rpcResult = (RpcResult) entryTransactionResponse;
                        return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, MESSAGE_ENTRY_REVEAL_SUCCESS);

                    });
            if (logErrors) {
                futureResponse.exceptionally(throwable -> {
                    logger.error(String.format("ackTransaction failed for chain ID %s and entry hash %s", chainId, hash), throwable);
                    return null;
                });
            }
            return futureResponse;
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean allDelivered(final TxID[] produced) {
        if (produced == null) {
            return true;
        }

        boolean allDelivered = true;
        for (final TxID txID : produced) {
            try {
                final TransactionQueryResponse queryResponse = accumulateApi.getTx(txID).get().getQueryResponse();
                switch (queryResponse.getStatus().getCode()) {
                    case OK:
                    case DELIVERED:
                        if (!allDelivered(queryResponse.getProduced())) {
                            allDelivered = false;
                        }
                        break;
                    default:
                        allDelivered = false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return allDelivered;
    }


    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> commitEntry(final String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(Hex.decodeHex(message)));
                final byte version = inputStream.readByte(); // TODO cleanup unused stuff here
                final byte[] timeStamp = new byte[6];
                inputStream.read(timeStamp);
                final byte[] entryHash = new byte[32];
                inputStream.read(entryHash);

                final byte[] privateKey = new byte[64];
                inputStream.read(privateKey);
                final LiteAccount liteAccount = liteAccountFromPrivateKey(privateKey);

                final CommitEntryResponse commitEntryResponse = new CommitEntryResponse(
                        MESSAGE_ENTRY_COMMIT_SUCCESS,
                        "na", // We don't get txid until we do reveal-entry
                        Hex.encodeHexString(entryHash));

                final Key entryHashKey = new Key(entryHash);
                liteAccountCache.put(entryHashKey, liteAccount);
                final RpcResult rpcResult = (RpcResult) commitEntryResponse;
                return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, MESSAGE_CHAIN_COMMIT_SUCCESS);
            } catch (DecoderException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static LiteAccount liteAccountFromPrivateKey(final byte[] privateKey) {
        final TweetNaclFast.Signature.KeyPair keyPair = TweetNaclFast.Signature.keyPair_fromSecretKey(privateKey);
        final LiteAccount liteAccount = new LiteAccount(new SignatureKeyPair(keyPair, SignatureType.ED25519)); // FIXME ED25519 is hardcoded
        return liteAccount;
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> revealEntry(String revealMessage, boolean logErrors) {
        final Entry firstEntry = decodeEntry(revealMessage);
        final byte[] entryHash = entryOperations.calculateEntryHash(firstEntry.getExternalIds(), firstEntry.getContent(), firstEntry.getChainId());
        final Key entryHashKey = new Key(entryHash);
        final LiteAccount liteAccount = liteAccountCache.remove(entryHashKey);

        final FactomEntry factomEntry = new FactomEntry(firstEntry.getContent());
        firstEntry.getExternalIds().forEach(extId -> {
            factomEntry.addExtRef(extId);
        });

        final CompletableFuture<FactomResponse<RpcResult>> futureResponse = accumulateApi.writeFactomData(liteAccount, firstEntry.getChainId(), factomEntry)
                .thenApply(writeDataResult -> {
                    txIdCache.put(entryHashKey, writeDataResult.getTxID());
                    final RevealResponse revealEntryResponse = new RevealResponse(MESSAGE_ENTRY_REVEAL_SUCCESS,
                            Hex.encodeHexString(entryHash), firstEntry.getChainId());
                    final RpcResult rpcResult = (RpcResult) revealEntryResponse;
                    return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, MESSAGE_ENTRY_REVEAL_SUCCESS);
                });
        if (logErrors) {
            futureResponse.exceptionally(throwable -> {
                logger.error(String.format("revealChain failed for chain ID %s", firstEntry.getChainId()), throwable);
                return null;
            });
        }
        return futureResponse;
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> chainHead(final String chainId, final boolean logErrors) {
        final CompletableFuture<FactomResponse<RpcResult>> futureCountResponse = accumulateApi.queryData(new DataEntrySetQuery()
                        .url(chainId)
                        .start(0)
                        .count(1))
                .thenCompose(countResponse -> {
                    if (countResponse.getTotal() > 0) {
                        final CompletableFuture<FactomResponse<RpcResult>> futureResponse = accumulateApi.queryData(new DataEntrySetQuery()
                                        .url(chainId)
                                        .start(countResponse.getTotal() - 1)
                                        .expand(true)
                                        .count(1))
                                .thenApply(response -> {
                                    if (response.getItems() != null && !response.getItems().isEmpty()) {
                                        ChainHeadResponse chainHeadResponse = null;
                                        final ResponseDataEntry lastEntry = response.getItems().get(0);
                                        if (lastEntry != null) {
                                            final TxID txId = ObjectUtils.firstNonNull(lastEntry.getCauseTxId(), lastEntry.getTxId());
                                            chainHeadResponse = new ChainHeadResponse(txId.getUrl().string(), false);
                                        }
                                        final RpcResult rpcResult = (RpcResult) chainHeadResponse;
                                        return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, MESSAGE_ENTRY_REVEAL_SUCCESS);
                                    }
                                    return null;
                                });
                        return futureResponse;
                    }
                    return null;
                });
        if (logErrors) {
            futureCountResponse.exceptionally(throwable -> {
                logger.error(String.format("chainHead failed for chain ID %s", chainId), throwable);
                return null;
            });
        }
        return futureCountResponse;
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> queryChain(final String chainId, final boolean expand, final boolean logErrors) {
        final QueryChainResponse queryChainResponse = new QueryChainResponse();
        final List<ChainEntry> chainEntries = queryChainResponse.getChainEntries();
        final RpcResult rpcResult = (RpcResult) queryChainResponse;

        final CompletableFuture<FactomResponse<RpcResult>> futureFinalResponse = accumulateApi.queryData(new DataEntrySetQuery()
                        .url(chainId)
                        .start(0)
                        .count(1))
                .thenCompose(countResponse -> {
                    if (countResponse.getTotal() > 0) {
                        final CompletableFuture<FactomResponse<RpcResult>> futureResponse = accumulateApi.queryData(new DataEntrySetQuery()
                                        .url(chainId)
                                        .start(0)
                                        .count(countResponse.getTotal())  // FIXME blocks of max 1000
                                        .expand(expand))
                                .thenApply(response -> {
                                    if (response.getItems() != null && !response.getItems().isEmpty()) {
                                        Collections.reverse(response.getItems());
                                        response.getItems().forEach(responseDataEntry -> {
                                            ChainEntry chainEntry = new ChainEntry(chainId);
                                            final FactomDataEntry factomDataEntry = (FactomDataEntry) responseDataEntry.getEntry();
                                            if (expand) {
                                                final TxID txId = ObjectUtils.firstNonNull(responseDataEntry.getCauseTxId(), responseDataEntry.getTxId());
                                                try {
                                                    final TransactionQueryResult transactionQueryResult = accumulateApi.getTx(new TxnQuery()
                                                                    .txIdUrl(txId)
                                                                    .prove(true)
                                                                    .expand(false))
                                                            .get();
                                                    final TransactionQueryResponse queryResponse = transactionQueryResult.getQueryResponse();
                                                    final List<String> extIds = Arrays.stream(factomDataEntry.getExtIds())
                                                            .map(extId -> Hex.encodeHexString(extId))
                                                            .collect(Collectors.toList());
                                                    chainEntry.setExtIds(extIds);
                                                    chainEntry.setContent(Hex.encodeHexString(factomDataEntry.getData()));
                                                    chainEntry.setStatus(queryResponse.getStatus().getCode().getApiName());
                                                    Arrays.stream(queryResponse.getReceipts())
                                                            .findFirst().ifPresent(txReceipt -> {
                                                                chainEntry.setBlockTime(txReceipt.getLocalBlockTime());
                                                                chainEntry.setBlock(txReceipt.getLocalBlock());
                                                            });
                                                } catch (InterruptedException | ExecutionException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                            chainEntry.setEntryHash(responseDataEntry.getEntryHash());
                                            chainEntries.add(chainEntry);
                                        });
                                    }
                                    return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, MESSAGE_QUERY_CHAIN_SUCCESS);
                                });
                        return futureResponse;
                    }
                    return CompletableFuture.completedFuture(new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, MESSAGE_QUERY_CHAIN_SUCCESS));
                });
        if (logErrors) {
            futureFinalResponse.exceptionally(throwable -> {
                logger.error(String.format("queryEntriesByChainId failed for chain ID %s", chainId), throwable);
                return null;
            });
        }
        return futureFinalResponse;
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> entryBlockByTxId(final String txId, final boolean logErrors) {
        final TxID txID = new TxID(Url.toAccURL(txId));
        final List<EntryBlockResponse.Entry> entryList = new ArrayList<>();
        final EntryBlockResponse.Header header = new EntryBlockResponse.Header();
        final EntryBlockResponse entryBlockResponse = new EntryBlockResponse(header, entryList);
        final RpcResult rpcResult = (RpcResult) entryBlockResponse;

        final CompletableFuture<FactomResponse<RpcResult>> futureFinalResponse = accumulateApi.getTx(new TxnQuery()
                        .txIdUrl(txID)
                        .prove(true)
                        .expand(false))
                .thenApply(transactionQueryResult -> {
                    final TransactionQueryResponse queryResponse = transactionQueryResult.getQueryResponse();
                    final WriteDataTo writeDataTo = (WriteDataTo) queryResponse.getTransaction().getBody();
                    final FactomDataEntry dataEntry = (FactomDataEntry) writeDataTo.getEntry();
                    final Url chainIdUrl = Url.toAccURL(Hex.encodeHexString(dataEntry.getAccountId()));
                    final String chainId = chainIdUrl.authority();
                    header.setChainid(chainId);
                    Arrays.stream(queryResponse.getReceipts()).findFirst().ifPresent(txReceipt -> {
                        header.setPrevkeymr(getPreviousTxId(chainIdUrl, txID));
                        header.setBlocksequencenumber(txReceipt.getLocalBlock());
                        header.setTimestamp(txReceipt.getLocalBlockTime().toInstant().toEpochMilli());
                    });

                    final EntryResponse entryResponse = buildEntryResponse(dataEntry, chainId);
                    final byte[] entryHash = reconstructEntryHash(dataEntry, chainIdUrl);
                    final EntryBlockResponse.Entry entry = new EntryBlockResponse.Entry(chainId + '/' + Hex.encodeHexString(entryHash), entryResponse);
                    entry.setTimestamp(header.getTimestamp());
                    entryList.add(entry);
                    return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, MESSAGE_GET_ENTRY_SUCCESS);
                });

        if (logErrors) {
            futureFinalResponse.exceptionally(throwable -> {
                logger.error(String.format("queryEntriesByChainId failed for txId %s", txId), throwable);
                return null;
            });
        }
        return futureFinalResponse;
    }

    private static EntryResponse buildEntryResponse(final FactomDataEntry dataEntry, final String chainId) {
        final List<String> hexExtIds = Arrays.stream(dataEntry.getExtIds())
                .map(extId -> Hex.encodeHexString(extId))
                .collect(Collectors.toList());
        final String hexContent = new String(dataEntry.getData(), StandardCharsets.UTF_8);
        final EntryResponse entryResponse = new EntryResponse(chainId, hexExtIds, hexContent);
        return entryResponse;
    }

    private byte[] reconstructEntryHash(final FactomDataEntry dataEntry, final Url chainIdUrl) {
        final List<String> strExtIds = Arrays.stream(dataEntry.getExtIds())
                .map(extId -> new String(extId, StandardCharsets.UTF_8))
                .collect(Collectors.toList());
        final String stringContent = new String(dataEntry.getData(), StandardCharsets.UTF_8);
        final byte[] entryHash = entryOperations.calculateEntryHash(strExtIds, stringContent, chainIdUrl.authority());
        return entryHash;
    }

    private String getPreviousTxId(final Url chainId, final TxID txId) {
        final Map<TxID, TxID> prevTxIdMap = previousTxIdCache.computeIfAbsent(chainId, k -> new HashMap<>());
        TxID prevTxId = prevTxIdMap.get(txId);
        if (prevTxId == null) {
            loadTxIdMap(chainId, prevTxIdMap);
        }
        prevTxId = prevTxIdMap.get(txId);
        if (prevTxId == null) {
            throw new RuntimeException("TxID " + txId.getUrl().string() + " could not be found");
        }
        return prevTxId.getUrl().string();
    }

    private void loadTxIdMap(final Url chainId, final Map<TxID, TxID> prevTxIdMap) {
        try {
            prevTxIdMap.clear();
            final MultiResponse<ResponseDataEntry> countResponse = accumulateApi.queryData(new DataEntrySetQuery()
                    .url(chainId)
                    .count(1)
                    .expand(false)).get();
            long start = 0;
            while (start < countResponse.getTotal()) {
                final TxID[] prevTxId = {NULL_TX_ID};
                final MultiResponse<ResponseDataEntry> response = accumulateApi.queryData(new DataEntrySetQuery()
                        .url(chainId)
                        .start(start)
                        .count(1024)
                        .expand(true)).get();
                response.getItems().stream()
                        .map(ResponseDataEntry::getCauseTxId)
                        .forEach(txId -> {
                            if (!prevTxIdMap.containsKey(txId) && !prevTxIdMap.containsValue(prevTxId[0]) && !txId.equals(prevTxId[0])) {
                                prevTxIdMap.put(txId, prevTxId[0]);
                                prevTxId[0] = txId;
                            }
                        });
                start += 1024;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> getEntry(final String queryUrl, final boolean logErrors) {
        if (StringUtils.length(queryUrl) < 64) {
            throw new IllegalArgumentException("Invalid query URL, the minimum length of an entry is 64");
        }
        final int protocolIndex = Math.max(queryUrl.indexOf("//"), 0) + 2;
        final int split = queryUrl.indexOf('/', protocolIndex);
        if (split < 0) {
            throw new IllegalArgumentException("Accumulate entry has to be fetched in acc://<chain id>/<entry hash> format.");
        }
        final String chainId = queryUrl.substring(0, split);
        final String entryHash = queryUrl.substring(split + 1);
        final CompletableFuture<FactomResponse<RpcResult>> futureResponse = accumulateApi.queryData(new DataEntryQuery()
                        .url(chainId)
                        .entryHash(entryHash))
                .thenApply(response -> {
                    if (response.getEntry() != null) {
                        final FactomDataEntry factomDataEntry = (FactomDataEntry) response.getEntry();
                        final List<String> extIds = Arrays.stream(factomDataEntry.getExtIds())
                                .map(extId -> Hex.encodeHexString(extId))
                                .collect(Collectors.toList());
                        final EntryResponse entryResponse = new EntryResponse(chainId, extIds, Hex.encodeHexString(factomDataEntry.getData()));
                        final RpcResult rpcResult = (RpcResult) entryResponse;
                        return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, MESSAGE_ENTRY_REVEAL_SUCCESS);
                    }
                    return null;
                });
        if (logErrors) {
            futureResponse.exceptionally(throwable -> {
                logger.error(String.format("getEntry failed for chain ID %s", chainId), throwable);
                return null;
            });
        }
        return futureResponse;
    }

    public Entry decodeEntry(final String message) {
        final Entry entry = new Entry();
        try {
            final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(Hex.decodeHex(message)));
            final byte version = inputStream.readByte(); // TODO cleanup unused stuff here
            final byte[] chainId = new byte[32];
            inputStream.read(chainId);
            entry.setChainId(Hex.encodeHexString(chainId));

            short externalIdBufferSize = inputStream.readShort();
            while (externalIdBufferSize > 0) {
                final short externalIdLength = inputStream.readShort();
                final byte[] extId = new byte[externalIdLength];
                inputStream.read(extId);
                entry.getExternalIds().add(new String(extId, StandardCharsets.UTF_8));
                externalIdBufferSize -= externalIdLength + 2;
            }
            final int contentLength = inputStream.available();
            if (contentLength > 0) {
                final byte[] content = new byte[contentLength];
                inputStream.read(content);
                entry.setContent(new String(content, StandardCharsets.UTF_8));
            }
            return entry;
        } catch (DecoderException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
