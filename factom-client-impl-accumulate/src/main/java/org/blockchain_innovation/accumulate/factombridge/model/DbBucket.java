package org.blockchain_innovation.accumulate.factombridge.model;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public enum DbBucket {
    ENTRY(toBytes("Entry")),
    ENTRY_HASHES(toBytes("EHashes")),
    CHAIN_HEAD(toBytes("ChainHead")),
    CHAIN_ENTRIES(toBytes("ChainEntries"));

    private byte[] value;

    DbBucket(final byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return Arrays.copyOf(value, value.length);
    }

    private static byte[] toBytes(final String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
