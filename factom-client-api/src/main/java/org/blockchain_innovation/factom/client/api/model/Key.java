package org.blockchain_innovation.factom.client.api.model;

import org.blockchain_innovation.factom.client.api.ops.Encoding;

public class Key {
    private final byte[] value;

    public Key(byte[] keyValue) {
        this.value = keyValue;
    }

    public Key(String hexKey) {
        this.value = Encoding.HEX.decode(hexKey);
    }

    public byte[] getValue() {
        if (value == null) {
            return null;
        }
        return value.clone();
    }

    public String getValueAsHex() {
        return Encoding.HEX.encode(value);
    }

}
