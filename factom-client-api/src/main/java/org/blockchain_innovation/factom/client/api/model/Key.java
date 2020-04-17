package org.blockchain_innovation.factom.client.api.model;

import org.blockchain_innovation.factom.client.api.ops.Encoding;

import java.io.Serializable;

/**
 * Represents a key (public or private).
 */
public class Key implements Serializable {
    private final byte[] value;

    public Key(byte[] keyValue) {
        this.value = keyValue.clone();
    }

    public Key(String hexKey) {
        this.value = Encoding.HEX.decode(hexKey);
    }

    /**
     * Get the key as bytes.
     * @return The key.
     */
    public byte[] getValue() {
        if (value == null) {
            return null;
        }
        return value.clone();
    }

    /**
     * Get the key as hex encoded string.
     * @return The key in hex.
     */
    public String getValueAsHex() {
        return Encoding.HEX.encode(value);
    }

}
