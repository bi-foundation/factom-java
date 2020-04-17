package org.blockchain_innovation.factom.client.api.model.types;

import org.blockchain_innovation.factom.client.api.ops.Encoding;

/**
 * The Factom Redeem Condition Type.
 */
public enum RCDType {

    TYPE_1(Encoding.HEX.decode("01"));

    private final byte[] value;

    RCDType(byte[] value) {
        this.value = value.clone();
    }

    /**
     * Get the hex value of the RCD type.
     *
     * @return The RCD value.
     */
    public byte[] getValue() {
        return value.clone();
    }

    /**
     * Get the value of the RCD type using the supplied encoding.
     *
     * @param encoding Encode the value.
     * @return The RCD value (encoded).
     */
    public String getValue(Encoding encoding) {
        return encoding.encode(getValue());
    }
}
