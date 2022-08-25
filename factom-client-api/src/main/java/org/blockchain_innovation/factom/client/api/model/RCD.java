package org.blockchain_innovation.factom.client.api.model;

import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.model.types.RCDType;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class RCD {
    private static final AddressKeyConversions CONVERSIONS = new AddressKeyConversions();
    private final RCDType type;
    private final Address address;
    private final byte[] hash;
    private final Optional<byte[]> publicKey;


    private RCD(RCDType type, Address address, byte[] hash, Optional<byte[]> publicKey) {
        this.type = type;
        this.address = address;
        this.hash = hash;
        this.publicKey = publicKey;
    }

    public static RCD fromAddress(RCDType type, Address address) {
        AddressType.assertValidAddress(address.getValue(), AddressType.FACTOID_PUBLIC);
        return new RCD(type, address, CONVERSIONS.fctAddressToRcdHash(address.getValue()), Optional.empty());
    }

    public static RCD fromAddress(RCDType type, String address) {
        return fromAddress(type, ECAddress.fromString(address));
    }

    public static RCD fromHash(RCDType type, byte[] hash) {
        return new RCD(type, ECAddress.fromString(CONVERSIONS.rcdHashToFctAddress(hash)), hash, Optional.empty());
    }

    public static RCD fromPublicKey(RCDType type, byte[] publicKey) {
        final Address address = ECAddress.fromString(CONVERSIONS.keyToAddress(publicKey, AddressType.FACTOID_PUBLIC));
        return new RCD(type, address, CONVERSIONS.fctAddressToRcdHash(address.getValue()), Optional.of(publicKey));
    }


    public RCDType getType() {
        return type;
    }


    public Address getAddress() {
        return address;
    }

    public byte[] hash() {
        return hash;
    }

    public byte[] getPublicKey() {
        return publicKey.orElseThrow(() -> new FactomRuntimeException.AssertionException("No public key was available, probably because the RCD was constructed from a hash or address instead of a publicKey"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RCD rcd = (RCD) o;
        return type == rcd.type && address.equals(rcd.address) && Arrays.equals(hash, rcd.hash);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type, address);
        result = 31 * result + Arrays.hashCode(hash);
        return result;
    }
}
