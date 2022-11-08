package org.blockchain_innovation.accumulate.factombridge.impl;

import io.accumulatenetwork.sdk.commons.codec.binary.Hex;
import io.accumulatenetwork.sdk.protocol.TxID;
import io.accumulatenetwork.sdk.protocol.Url;
import org.apache.commons.lang3.StringUtils;
import org.blockchain_innovation.accumulate.factombridge.model.*;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
import org.blockchain_innovation.factom.client.api.model.response.factomd.*;
import org.blockchain_innovation.factom.client.api.rpc.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.blockchain_innovation.accumulate.factombridge.support.StreamUtil.readHashAsHex;

// TODO split key-value db code into other class
public class AccumulateFactomIndexDBLayer {

    private static final Logger logger = LogFactory.getLogger(AccumulateFactomIndexDBLayer.class);
    private AccumulateFactomIndexDB indexDb;
    private FactomToAccumulateBridge bridge;

    public AccumulateFactomIndexDBLayer(final AccumulateFactomIndexDB indexDb, final FactomToAccumulateBridge bridge) {
        this.indexDb = indexDb;
        this.bridge = bridge;
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> entryBlockByTxId(final String txId, final boolean logErrors) {
        final CompletableFuture<FactomResponse<RpcResult>> futureFinalResponse = CompletableFuture.supplyAsync(() -> {
            try {
                final IndexRecord indexRecord = fetchIndexRecord(txId);
                if (indexRecord.isAccumulate()) { // Accumulate
                    final CompletableFuture<FactomResponse<RpcResult>> accResult = bridge.entryBlockByTxId(indexRecord.getTxId(), indexRecord.getPrevTxId(), logErrors);
                    return accResult.get();
                } else if (indexRecord.isFactom()) { // Factom
                    final String entryHash = txId;
                    final List<EntryBlockResponse.Entry> entryList = new ArrayList<>();
                    final EntryBlockResponse.Header header = new EntryBlockResponse.Header();
                    final EntryBlockResponse entryBlockResponse = new EntryBlockResponse(header, entryList);
                    final RpcResult rpcResult = (RpcResult) entryBlockResponse;
                    header.setChainid(indexRecord.getChainId());
                    header.setPrevkeymr(indexRecord.getPrevEntryHash()); // EntryBlocks are flattened in the index, so use entryHash now
                    header.setBlocksequencenumber(indexRecord.getBlockHeight());
                    header.setTimestamp(indexRecord.getBlockTimeMinutes());

                    final EntryResponse entryResponse = buildEntryResponse(indexRecord.getChainId(), entryHash);
                    final EntryBlockResponse.Entry entry = new EntryBlockResponse.Entry(entryHash, entryResponse);
                    entry.setTimestamp(header.getTimestamp());
                    entryList.add(entry);
                    return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, FactomToAccumulateBridge.MESSAGE_GET_ENTRY_SUCCESS);
                }

                return null;
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        if (logErrors) {
            futureFinalResponse.exceptionally(throwable -> {
                logger.error(String.format("queryEntriesByChainId failed for txId %s", txId), throwable);
                return null;
            });
        }
        return futureFinalResponse;
    }

    public IndexRecord fetchIndexRecord(final String txIdOrEntryHash) {
        try {
            if (StringUtils.contains(txIdOrEntryHash, '@')) {
                try {
                    final Url txId = Url.parse(txIdOrEntryHash);
                    return new IndexRecord(indexDb.get(IndexDB.ACCUMULATE_INDEX, new AccUrlKey(DbBucket.ENTRY_HASHES, txId)));
                } catch (URISyntaxException e) {
                }
            }
            return new IndexRecord(indexDb.get(IndexDB.ACCUMULATE_INDEX, new HexStringKey(DbBucket.ENTRY_HASHES, txIdOrEntryHash)));
        } catch (KeyNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> getEntry(final String queryUrl, final boolean logErrors) {
        String entryHash;
        final int protocolIndex = Math.max(queryUrl.indexOf("//"), 0) + 2;
        final int split = queryUrl.indexOf('/', protocolIndex);
        if (split > 0) {
            entryHash = queryUrl.substring(split + 1);
        } else {
            entryHash = queryUrl;
        }
        final CompletableFuture<FactomResponse<RpcResult>> futureResponse = CompletableFuture.supplyAsync(() -> {
            RpcResult rpcResult = null;
            try {
                final IndexRecord indexRecord = fetchIndexRecord(entryHash);
                if (indexRecord.isAccumulate()) { // Accumulate
                    final CompletableFuture<FactomResponse<RpcResult>> accResult = bridge.getEntry(queryUrl, logErrors);
                    return accResult.get();
                } else if (indexRecord.isFactom()) { // Factom
                    final EntryResponse entryResponse = buildEntryResponse(indexRecord.getChainId(), entryHash);
                    rpcResult = (RpcResult) entryResponse;
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, bridge.MESSAGE_GET_ENTRY_SUCCESS);
        });

        if (logErrors) {
            futureResponse.exceptionally(throwable -> {
                logger.error(String.format("getEntry failed for URL %s", queryUrl), throwable);
                return null;
            });
        }
        return futureResponse;
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> queryChain(final String chainId, final boolean expand, final boolean logErrors) {
        final QueryChainResponse queryChainResponse = new QueryChainResponse();
        final List<ChainEntry> chainEntries = queryChainResponse.getChainEntries();
        final RpcResult rpcResult = (RpcResult) queryChainResponse;

        final CompletableFuture<FactomResponse<RpcResult>> futureFinalResponse = CompletableFuture.supplyAsync(() -> {
            try {
                final List<byte[]> allEntries = indexDb.getAll(IndexDB.ACCUMULATE_INDEX, new HexStringKey(DbBucket.CHAIN_ENTRIES, chainId, null), 45);
                logger.info(String.format("getAll returned %d records for chain %s", allEntries.size(), chainId));
                allEntries.stream()
                        .forEach(entryHashBin -> {
                            final String entryHash = Hex.encodeHexString(entryHashBin);

                            ChainEntry chainEntry = null;
                            final IndexRecord indexRecord = fetchIndexRecord(entryHash);
                            if (indexRecord.isAccumulate()) { // Accumulate
                                chainEntry = bridge.createChainEntry(chainId, entryHashBin, expand, new TxID(Url.toAccURL(indexRecord.getTxId())));
                            } else if (indexRecord.isFactom()) { // Factom
                                chainEntry = new ChainEntry(chainId);
                                final EntryResponse entryResponse = buildEntryResponse(chainId, entryHash);
                                chainEntry.setBlockTime(OffsetDateTime.ofInstant(Instant.ofEpochMilli(indexRecord.getBlockTimeMinutes() * 60000L), ZoneId.systemDefault()));
                                chainEntry.setBlock(indexRecord.getBlockHeight());
                                chainEntry.setExtIds(entryResponse.getExtIds());
                                chainEntry.setContent(entryResponse.getContent());
                                chainEntry.setEntryHash(entryHashBin);
                            }
                            queryChainResponse.getChainEntries().add(chainEntry);
                        });
            } catch (KeyNotFoundException e) {
                throw new RuntimeException(e);
            }
            return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, bridge.MESSAGE_GET_ENTRY_SUCCESS);
        });
        if (logErrors) {
            futureFinalResponse.exceptionally(throwable -> {
                logger.error(String.format("queryEntriesByChainId failed for chain ID %s", chainId), throwable);
                return null;
            });
        }
        return futureFinalResponse;
    }

    private EntryResponse buildEntryResponse(final String chainId, final String entryHash) {
        try {
            final byte[] entryBuf = indexDb.get(IndexDB.FACTOM_ENTRIES, new HexStringKey(DbBucket.ENTRY, entryHash));
            final var dataInputStream = new DataInputStream(new ByteArrayInputStream(entryBuf));
            dataInputStream.readByte(); // version
            final var entryChainId = readHashAsHex(dataInputStream);
          /*  if (!entryChainId.equals(chainId)) {
                throw new RuntimeException(String.format("Chain id mismatch between entry (%s) info and the actual entry (%s)", chainId, entryChainId));
            }*/
            final List<String> hexExtIds = new ArrayList<>();
            short extBlockSize = dataInputStream.readShort();
            while (extBlockSize > 0) {
                final short extIdSize = dataInputStream.readShort();
                extBlockSize -= 2;
                extBlockSize -= extIdSize;
                final byte[] extId = new byte[extIdSize];
                dataInputStream.read(extId);
                hexExtIds.add(Hex.encodeHexString(extId));
            }
            final byte[] contentBuf = dataInputStream.readAllBytes();
            final String hexContent = Hex.encodeHexString(contentBuf);
            final EntryResponse entryResponse = new EntryResponse(chainId, hexExtIds, hexContent);
            return entryResponse;
        } catch (KeyNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <RpcResult> CompletableFuture<FactomResponse<RpcResult>> chainHead(final String chainId, final boolean logErrors) {
        final CompletableFuture<FactomResponse<RpcResult>> futureResponse = CompletableFuture.supplyAsync(() -> {
            try {
                final byte[] entryHash = indexDb.get(IndexDB.ACCUMULATE_INDEX, new HexStringKey(DbBucket.CHAIN_HEAD, chainId));
                final var indexRecord = fetchIndexRecord(Hex.encodeHexString(entryHash));
                if (indexRecord.isAccumulate()) {
                    // Get from Accumulate tbs, it's not static like Factom is now
                    final CompletableFuture<FactomResponse<RpcResult>> accResult = bridge.chainHead(chainId, logErrors);
                    return accResult.get();
                } else {
                    final ChainHeadResponse chainHeadResponse = new ChainHeadResponse(indexRecord.getTxId(), false, Hex.encodeHexString(entryHash));
                    final RpcResult rpcResult = (RpcResult) chainHeadResponse;
                    return new FactomResponseImpl<>(new RpcResponse<>(rpcResult), 200, FactomToAccumulateBridge.MESSAGE_CHAIN_HEAD_SUCCESS);
                }
            } catch (KeyNotFoundException | InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        if (logErrors) {
            futureResponse.exceptionally(throwable -> {
                logger.error(String.format("chainHead failed for chain ID %s", chainId), throwable);
                return null;
            });
        }
        return futureResponse;
    }
}
