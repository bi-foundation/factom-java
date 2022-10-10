package org.blockchain_innovation.accumulate.factombridge.impl;

import com.iwebpp.crypto.TweetNaclFast;
import io.accumulatenetwork.sdk.api.v2.AccumulateAsyncApi;
import io.accumulatenetwork.sdk.commons.codec.DecoderException;
import io.accumulatenetwork.sdk.commons.codec.binary.Hex;
import io.accumulatenetwork.sdk.generated.apiv2.DataEntryQuery;
import io.accumulatenetwork.sdk.generated.apiv2.DataEntrySetQuery;
import io.accumulatenetwork.sdk.generated.apiv2.TransactionQueryResponse;
import io.accumulatenetwork.sdk.generated.apiv2.TxnQuery;
import io.accumulatenetwork.sdk.generated.protocol.FactomDataEntry;
import io.accumulatenetwork.sdk.generated.protocol.SignatureType;
import io.accumulatenetwork.sdk.generated.query.ResponseDataEntry;
import io.accumulatenetwork.sdk.protocol.FactomEntry;
import io.accumulatenetwork.sdk.protocol.SignatureKeyPair;
import io.accumulatenetwork.sdk.protocol.TxID;
import net.jodah.expiringmap.ExpiringMap;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FactomToAccumulateBridge {
    private static final String MESSAGE_CHAIN_COMMIT_SUCCESS = "Chain Commit Success";
    private static final String MESSAGE_CHAIN_REVEAL_SUCCESS = "Chain Reveal Success";
    private static final String MESSAGE_ENTRY_COMMIT_SUCCESS = "Entry Commit Success";
    private static final String MESSAGE_ENTRY_REVEAL_SUCCESS = "Entry Reveal Success";
    private static final Logger logger = LogFactory.getLogger(FactomToAccumulateBridge.class);
    private static final Map<Key, LiteAccount> liteAccountCache =  ExpiringMap.builder()
            .maxSize(150000)
            .expiration(24, TimeUnit.HOURS)
            .build();
    private static final Map<Key, TxID> txIdCache =  ExpiringMap.builder()
            .maxSize(150000)
            .expiration(24, TimeUnit.HOURS)
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
                                        .count(1))
                                .thenApply(response -> {
                                    if (response.getItems() != null && !response.getItems().isEmpty()) {
                                        ChainHeadResponse chainHeadResponse = null;
                                        final ResponseDataEntry firstEntry = response.getItems().get(0);
                                        if (firstEntry != null) {
                                            chainHeadResponse = new ChainHeadResponse(
                                                    chainId + '/' + Hex.encodeHexString(firstEntry.getEntryHash()), false);
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

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> queryEntriesByChainId(final String chainId, final boolean expand, final boolean logErrors) {
        final List<EntryBlockResponse.Entry> entryList = new ArrayList<>();
        final EntryBlockResponse.Header header = new EntryBlockResponse.Header();
        header.setChainid(chainId);
        final EntryBlockResponse entryBlockResponse = new EntryBlockResponse(header, entryList);
        final RpcResult rpcResult = (RpcResult) entryBlockResponse;

        final CompletableFuture<FactomResponse<RpcResult>> futureCountResponse = accumulateApi.queryData(new DataEntrySetQuery()
                        .url(chainId)
                        .start(0)
                        .count(1))
                .thenCompose(countResponse -> {
                    if (countResponse.getTotal() > 0) {
                        final CompletableFuture<FactomResponse<RpcResult>> futureResponse = accumulateApi.queryData(new DataEntrySetQuery()
                                        .url(chainId)
                                        .start(0)
                                        .count(countResponse.getTotal())
                                        .expand(expand))
                                .thenApply(response -> {
                                    if (response.getItems() != null && !response.getItems().isEmpty()) {
                                        Collections.reverse(response.getItems());
                                        response.getItems().forEach(entry -> {
                                            EntryResponse entryResponse = null;
                                            final FactomDataEntry factomDataEntry = (FactomDataEntry) entry.getEntry();
                                            if (expand) {
                                                final List<String> extIds = Arrays.stream(factomDataEntry.getExtIds())
                                                        .map(extId -> Hex.encodeHexString(extId))
                                                        .collect(Collectors.toList());
                                                entryResponse = new EntryResponse(chainId, extIds, Hex.encodeHexString(factomDataEntry.getData()));
                                            } else {
                                                entryResponse = new EntryResponse(chainId, null, null);
                                            }
                                            // Prepend chain before entryHash, we can't query with only entryHash
                                            entryList.add(new EntryBlockResponse.Entry(chainId + '/' + Hex.encodeHexString(entry.getEntryHash()), entryResponse));
                                        });
                                    }
                                    return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, MESSAGE_ENTRY_REVEAL_SUCCESS);
                                });
                        return futureResponse;
                    }
                    return CompletableFuture.completedFuture(new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, MESSAGE_ENTRY_REVEAL_SUCCESS));
                });
        if (logErrors) {
            futureCountResponse.exceptionally(throwable -> {
                logger.error(String.format("queryEntriesByChainId failed for chain ID %s", chainId), throwable);
                return null;
            });
        }
        return futureCountResponse;
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> getEntry(final String queryUrl, final boolean logErrors) {
        final int split = queryUrl.indexOf('/');
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
                logger.error(String.format("chainHead failed for chain ID %s", chainId), throwable);
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
