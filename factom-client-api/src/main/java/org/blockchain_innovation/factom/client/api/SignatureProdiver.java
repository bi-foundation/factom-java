package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.model.Address;

public interface SignatureProdiver {
    /**
     * Get the public EC address belonging to the signature operation
     * @return
     */
    Address getPublicECAddress();

    /**
     * Signs the input (digest). Please note that this needs to provide an ed25519 signature as used by Factom
     *
     * @param input The input
     * @return The signature using ed25519 in bytes
     */
    byte[] sign(byte[] input);
}
