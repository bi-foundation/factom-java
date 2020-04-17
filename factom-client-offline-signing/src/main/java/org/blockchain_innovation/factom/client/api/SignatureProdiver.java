package org.blockchain_innovation.factom.client.api;

public interface SignatureProdiver {
    byte[] sign(byte[] input);
}
