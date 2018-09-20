package org.blockchain_innovation.factom.client.api.model;

import org.blockchain_innovation.factom.client.api.ops.Encoding;

import java.io.Serializable;

public class Key implements Serializable {
    private final byte[] value;

    public Key(byte[] keyValue) {
        this.value = keyValue.clone();
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
