package org.blockchain_innovation.accumulate.factombridge.model;

import io.accumulatenetwork.sdk.protocol.TxID;

import java.io.*;

import static org.blockchain_innovation.accumulate.factombridge.support.StreamUtil.readHashAsHex;
import static org.blockchain_innovation.accumulate.factombridge.support.StreamUtil.writeHexHash;

public class IndexRecord {

    private final byte recordType;
    private final String chainId;
    private final String txId;
    private final String prevTxId;
    private final String prevEntryHash;
    private final int blockTimeMinutes;
    private final int blockHeight;

    public IndexRecord(final byte[] buffer) {
        final var entryInfoDataStream = new DataInputStream(new ByteArrayInputStream(buffer));
        try {
            this.recordType = entryInfoDataStream.readByte();
            this.chainId = readHashAsHex(entryInfoDataStream);
            if (isFactom()) {
                this.txId = readHashAsHex(entryInfoDataStream);
                this.prevTxId = readHashAsHex(entryInfoDataStream);
            } else {
                this.txId = entryInfoDataStream.readUTF(entryInfoDataStream);
                this.prevTxId = entryInfoDataStream.readUTF(entryInfoDataStream);
            }
            this.prevEntryHash = readHashAsHex(entryInfoDataStream);
            this.blockTimeMinutes = entryInfoDataStream.readInt();
            this.blockHeight = entryInfoDataStream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public IndexRecord(final String chainId, final TxID txId, final String prevTxId, final String prevEntryHash) {
        recordType = 1; // Only Accumulate can create new records
        this.chainId = chainId;
        this.txId = txId.getUrl().string();
        this.prevTxId = prevTxId;
        this.prevEntryHash = prevEntryHash;
        this.blockTimeMinutes = 0;
        this.blockHeight = 0;
    }

    public byte getRecordType() {
        return recordType;
    }

    public String getChainId() {
        return chainId;
    }

    public String getTxId() {
        return txId;
    }

    public String getPrevTxId() {
        return prevTxId;
    }

    public String getPrevEntryHash() {
        return prevEntryHash;
    }

    public int getBlockTimeMinutes() {
        return blockTimeMinutes;
    }

    public int getBlockHeight() {
        return blockHeight;
    }


    public boolean isAccumulate() {
        return getRecordType() == 1;
    }

    public boolean isFactom() {
        return getRecordType() == 0;
    }

    public byte[] marshalBinary() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        try {
            dataOutputStream.writeByte(recordType);
            writeHexHash(dataOutputStream, getChainId());
            dataOutputStream.writeUTF(getTxId());
            dataOutputStream.writeUTF(getPrevTxId());
            writeHexHash(dataOutputStream, getPrevEntryHash());
            dataOutputStream.writeInt(getBlockTimeMinutes());
            dataOutputStream.writeInt(getBlockHeight());
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
