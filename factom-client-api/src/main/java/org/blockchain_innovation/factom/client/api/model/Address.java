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

import org.blockchain_innovation.factom.client.api.model.types.AddressType;

import java.io.Serializable;

/**
 * An address represents a Factom Factoid or Entry Credit address. Either a private (secret) or a public address.
 */
public class Address implements Serializable {


    private String secret;

    /**
     * Create an address from bytes;
     *
     * @param hexAddress
     * @return The address.
     */
    public static Address fromBytes(byte[] hexAddress) {
        return new Address().setValue(String.valueOf(hexAddress));
    }

    /**
     * Create an address from a string.
     *
     * @param hexAddress The hex address string.
     * @return The address.
     */
    public static Address fromString(String hexAddress) {
        return new Address().setValue(hexAddress);
    }

    /**
     * Either the static methods or string based constructor should be used.
     */
    private Address() {
    }

    /**
     * Return a new address associated with the hex string.
     *
     * @param hexAddress The hex address string.
     */
    public Address(String hexAddress) {
        setValue(hexAddress);
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
}
