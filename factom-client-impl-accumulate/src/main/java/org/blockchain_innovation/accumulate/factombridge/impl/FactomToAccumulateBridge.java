package org.blockchain_innovation.accumulate.factombridge.impl;

import com.iwebpp.crypto.TweetNaclFast;
import io.accumulatenetwork.sdk.api.v2.AccumulateAsyncApi;
import io.accumulatenetwork.sdk.api.v2.TransactionQueryResult;
import io.accumulatenetwork.sdk.commons.codec.DecoderException;
import io.accumulatenetwork.sdk.commons.codec.binary.Hex;
import io.accumulatenetwork.sdk.generated.apiv2.TransactionQueryResponse;
import io.accumulatenetwork.sdk.generated.apiv2.TxnQuery;
import io.accumulatenetwork.sdk.generated.protocol.SignatureType;
import io.accumulatenetwork.sdk.protocol.FactomEntry;
import io.accumulatenetwork.sdk.protocol.SignatureKeyPair;
import io.accumulatenetwork.sdk.protocol.TxID;
import org.blockchain_innovation.accumulate.factombridge.model.LiteAccount;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.Key;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;
import org.blockchain_innovation.factom.client.api.rpc.RpcResponse;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FactomToAccumulateBridge {
    private static final String MESSAGE_CHAIN_COMMIT_SUCCESS = "Chain Commit Success";
    private static final String MESSAGE_ENTRY_COMMIT_SUCCESS = "Entry Commit Success";
    private static final String MESSAGE_ENTRY_REVEAL_SUCCESS = "Entry Reveal Success";
    private static final Logger logger = LogFactory.getLogger(FactomToAccumulateBridge.class);

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Map<Key, CommitChainResponse> commitChainCache = new HashMap<>(); // FIXME
    private static final Map<Key, CommitEntryResponse> commitEntryCache = new HashMap<>(); // FIXME
    private static final Map<Key, LiteAccount> liteAccountCache = new HashMap<>(); // FIXME
    private static final Map<Key, TxID> txIdCache = new HashMap<>(); // FIXME
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
                final TweetNaclFast.Signature.KeyPair keyPair = TweetNaclFast.Signature.keyPair_fromSecretKey(privateKey);
                final LiteAccount liteAccount = new LiteAccount(new SignatureKeyPair(keyPair, SignatureType.ED25519)); // FIXME

                final CommitChainResponse commitChainResponse = new CommitChainResponse(
                        MESSAGE_CHAIN_COMMIT_SUCCESS,
                        "NA",
                        Hex.encodeHexString(entryHash),
                        Hex.encodeHexString(chainIdHash));

                final Key entryHashKey = new Key(entryHash);
                commitChainCache.put(entryHashKey, commitChainResponse);
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
        final CommitChainResponse commitChainResponse = commitChainCache.get(entryHashKey);
        final LiteAccount liteAccount = liteAccountCache.get(entryHashKey);

        final FactomEntry factomEntry = new FactomEntry(firstEntry.getContent());
        firstEntry.getExternalIds().forEach(extId -> {
            factomEntry.addExtRef(extId);
        });


        final CompletableFuture<FactomResponse<RpcResult>> futureResponse = accumulateApi.createLiteDataAccount(liteAccount, factomEntry)
                .thenApply(writeDataResult -> {
                    txIdCache.put(entryHashKey, writeDataResult.getTxID());
                    final RevealResponse revealChainResponse = new RevealResponse(MESSAGE_ENTRY_REVEAL_SUCCESS,
                            commitChainResponse.getEntryHash(), firstEntry.getChainId());
                    final RpcResult rpcResult = (RpcResult) revealChainResponse;
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
                                status = EntryTransactionResponse.Status.DBlockConfirmed;
                                break;
                            case PENDING:
                                status = EntryTransactionResponse.Status.TransactionACK;
                                break;
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


    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> commitEntry(final String message) {
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
                final TweetNaclFast.Signature.KeyPair keyPair = TweetNaclFast.Signature.keyPair_fromSecretKey(privateKey);
                final LiteAccount liteAccount = new LiteAccount(new SignatureKeyPair(keyPair, SignatureType.ED25519)); // FIXME

                final CommitEntryResponse commitEntryResponse = new CommitEntryResponse(
                        MESSAGE_ENTRY_COMMIT_SUCCESS,
                        "", // We don't have a txid until we did reveal-entry
                        Hex.encodeHexString(entryHash));

                final Key entryHashKey = new Key(entryHash);
                commitEntryCache.put(entryHashKey, commitEntryResponse);
                liteAccountCache.put(entryHashKey, liteAccount);
                final RpcResult rpcResult = (RpcResult) commitEntryResponse;
                return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, MESSAGE_CHAIN_COMMIT_SUCCESS);
            } catch (DecoderException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> revealEntry(String revealMessage, boolean logErrors) {
        final Entry firstEntry = decodeEntry(revealMessage);
        final byte[] entryHash = entryOperations.calculateEntryHash(firstEntry.getExternalIds(), firstEntry.getContent(), firstEntry.getChainId());
        final Key entryHashKey = new Key(entryHash);
        final CommitChainResponse commitChainResponse = commitChainCache.get(entryHashKey);
        final LiteAccount liteAccount = liteAccountCache.get(entryHashKey);

        final FactomEntry factomEntry = new FactomEntry(firstEntry.getContent());
        firstEntry.getExternalIds().forEach(extId -> {
            factomEntry.addExtRef(extId);
        });


        final CompletableFuture<FactomResponse<RpcResult>> futureResponse = accumulateApi.createLiteDataAccount(liteAccount, factomEntry)
                .thenApply(writeDataResult -> {
                    txIdCache.put(entryHashKey, writeDataResult.getTxID());
                    final RevealResponse revealEntryResponse = new RevealResponse(MESSAGE_ENTRY_REVEAL_SUCCESS,
                            commitChainResponse.getEntryHash(), firstEntry.getChainId());
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

    private <RpcResult> FactomResponse<RpcResult> toFactomResponse(TransactionQueryResult txQueryResult) {
        switch (txQueryResult.getTxType()) {
            case UNKNOWN:
                break;
            case CREATE_IDENTITY:
                break;
            case CREATE_TOKEN_ACCOUNT:
                break;
            case SEND_TOKENS:
                break;
            case CREATE_DATA_ACCOUNT:
                break;
            case WRITE_DATA:
                break;
            case WRITE_DATA_TO:
                break;
            case ACME_FAUCET:
                break;
            case CREATE_TOKEN:
                break;
            case ISSUE_TOKENS:
                break;
            case BURN_TOKENS:
                break;
            case CREATE_LITE_TOKEN_ACCOUNT:
                break;
            case CREATE_KEY_PAGE:
                break;
            case CREATE_KEY_BOOK:
                break;
            case ADD_CREDITS:
                break;
            case UPDATE_KEY_PAGE:
                break;
            case LOCK_ACCOUNT:
                break;
            case UPDATE_ACCOUNT_AUTH:
                break;
            case UPDATE_KEY:
                break;
            case REMOTE:
                break;
            case SYNTHETIC_CREATE_IDENTITY:
                break;
            case SYNTHETIC_WRITE_DATA:
                break;
            case SYNTHETIC_DEPOSIT_TOKENS:
                break;
            case SYNTHETIC_DEPOSIT_CREDITS:
                break;
            case SYNTHETIC_BURN_TOKENS:
                break;
            case SYNTHETIC_FORWARD_TRANSACTION:
                break;
            case SYSTEM_GENESIS:
                break;
            case DIRECTORY_ANCHOR:
                break;
            case BLOCK_VALIDATOR_ANCHOR:
                break;
            case SYSTEM_WRITE_DATA:
                break;
        }
        return null;
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
