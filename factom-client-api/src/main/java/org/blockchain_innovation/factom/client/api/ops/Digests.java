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


import org.blockchain_innovation.factom.client.api.FactomRuntimeException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum Digests {

    SHA_256("SHA-256"),
    SHA_512("SHA-512"),
    ;

    private final String algorithm;

    Digests(String algorithm) {
        this.algorithm = algorithm;
    }


    public byte[] digest(String message) {
        return digest(Encoding.UTF_8.decode(message));
    }

    public byte[] digest(byte[] message) {
        assertMessage(message);
        return md().digest(message);
    }

    public byte[] doubleDigest(String message) {
        return digest(digest(message));
    }

    public byte[] doubleDigest(byte[] message) {
        return digest(digest(message));
    }


    private void assertMessage(Object message) {
        if (message == null) {
            throw new FactomRuntimeException.AssertionException("You cannot get a digest of a null input");
        }
    }

    private MessageDigest md() {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new FactomRuntimeException(e);
        }
    }
}