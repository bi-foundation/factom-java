package org.blockchain_innovation.factom.client.api;

public enum RCDType {


    TYPE_1(Encoding.HEX.decode("01"));

    private final byte[] value;

    RCDType(byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    public String getValue(Encoding encoding) {
        return encoding.encode(getValue());
    }


}
