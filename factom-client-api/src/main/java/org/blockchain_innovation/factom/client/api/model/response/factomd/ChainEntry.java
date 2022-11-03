package org.blockchain_innovation.factom.client.api.model.response.factomd;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

public class ChainEntry  implements Serializable {
    private String chainId;
    private List<String> extIds;
    private String content;
    private OffsetDateTime blockTime;
    private long block;
    private String status;
    private byte[] entryHash;

    public ChainEntry(final String chainId) {

        this.chainId = chainId;
    }

    public String getChainId() {
        return chainId;
    }

    public void setExtIds(final List<String> extIds) {
        this.extIds = extIds;
    }

    public List<String> getExtIds() {
        return extIds;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setBlockTime(final OffsetDateTime blockTime) {
        this.blockTime = blockTime;
    }

    public OffsetDateTime getBlockTime() {
        return blockTime;
    }

    public void setBlock(final long block) {
        this.block = block;
    }

    public long getBlock() {
        return block;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setEntryHash(final byte[] entryHash) {
        this.entryHash = entryHash;
    }

    public byte[] getEntryHash() {
        return entryHash;
    }
}
