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

package org.blockchain_innovation.factom.client.api.model;

import net.i2p.crypto.eddsa.EdDSAKey;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSASecurityProvider;
import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.ops.Encoding;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Objects;

/**
 * An address represents a Factom Factoid or Entry Credit address. Either a private (secret) or a public address.
 */
public class Address implements Serializable {

    static {
        // add eddsa to the security providers
        Security.addProvider(new EdDSASecurityProvider());
    }

    // Do not rename as it is used during serialization!
    private String secret;

//    private String userAddress;

    /**
     * Create an address from bytes;
     *
     * @param address
     * @return The address.
     */
    public static Address fromBytes(byte[] address) {
        return new Address().setValue(new String(address, StandardCharsets.US_ASCII));
    }

    public static Address fromHexBytes(byte[] address) {
        return new Address().setValue(Encoding.HEX.encode(address));
    }

    /**
     * Create an address from a string.
     *
     * @param humanReadableAddress The hex address string.
     * @return The address.
     */
    public static Address fromString(String humanReadableAddress) {
        return new Address().setValue(humanReadableAddress);
    }

    public static Address randomPrivate(AddressType addressType) {
        if (addressType == null || !addressType.isPrivate()) {
            throw new FactomRuntimeException.AssertionException("A private address type is needed to generate a new random address");
        }
        try {
            KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(EdDSAKey.KEY_ALGORITHM);
            KeyPair keyPair = kpGenerator.generateKeyPair();
            byte[] privateKey = ((EdDSAPrivateKey)keyPair.getPrivate()).getSeed();
            return fromString(new AddressKeyConversions().keyToAddress(privateKey, addressType));

        } catch (NoSuchAlgorithmException e) {
            throw new FactomRuntimeException(e);
        }
    }

    /**
     * Either the static methods or string based constructor should be used.
     */
    private Address() {
    }

    /**
     * Return a new address associated with the hex string.
     *
     * @param humanReadableAddress The human readable address string.
     */
    public Address(String humanReadableAddress) {
        setValue(humanReadableAddress);
    }

    /**
     * Get the address value.
     *
     * @return Address as string.
     */
    public String getValue() {
        return secret;
    }

    /**
     * Set the address to the supplied value. Protected since this class should not be reused for multiple addresses.
     *
     * @param value The address value.
     * @return The address object.
     */
    protected Address setValue(String value) {
        AddressType.assertValidAddress(value);
        this.secret = value;
        return this;
    }

    /**
     * Get the address type. Factoid, Entry credit, Identity, Public or private.
     *
     * @return The address type.
     */
    public AddressType getType() {
        return AddressType.getType(getValue());
    }

    @Override
    public String toString() {
        return "Address{" + secret + '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Address address = (Address) o;
        return secret.equals(address.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(secret);
    }
}
