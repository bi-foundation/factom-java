/*
 * Copyright 2018 Blockchain Innovation Foundation <https://blockchain-innovation.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.blockchain_innovation.factom.client.api.ops;


import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Allows digests/hashes of input messages using different algorithms
 */
public enum Digests {

    SHA_256("SHA-256"),
    SHA_512("SHA-512");

    private final String algorithm;

    Digests(String algorithm) {
        this.algorithm = algorithm;
    }


    /**
     * Digest/hash an input string using the algorithm chosen by selecting the correct enum.
     *
     * @param message The message to hash/digest
     * @return The digested/hashed value
     */
    public byte[] digest(String message) {
        return digest(Encoding.UTF_8.decode(message));
    }


    /**
     * Digest/hash input bytes using the algorithm chosen by selecting the correct enum.
     *
     * @param message The message to hash/digest
     * @return The digested/hashed value
     */
    public byte[] digest(byte[] message) {
        assertMessage(message);
        return md().digest(message);
    }


    /**
     * Digest/hash an input string twice using the algorithm chosen by selecting the correct enum.
     *
     * @param message The message to hash/digest twice
     * @return The digested/hashed value
     */
    public byte[] doubleDigest(String message) {
        return digest(digest(message));
    }

    /**
     * Digest/hash input bytes twice using the algorithm chosen by selecting the correct enum.
     *
     * @param message The message to hash/digest twice
     * @return The digested/hashed value
     */
    public byte[] doubleDigest(byte[] message) {
        return digest(digest(message));
    }


    /**
     * Assert the input message is valid.
     *
     * @param message
     */
    private void assertMessage(Object message) {
        if (message == null) {
            throw new FactomRuntimeException.AssertionException("You cannot get a digest of a null input");
        }
    }

    /**
     * Get the correct message digest object based on the algorithm connected with current enum value.
     *
     * @return
     */
    private MessageDigest md() {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new FactomRuntimeException(e);
        }
    }
}
