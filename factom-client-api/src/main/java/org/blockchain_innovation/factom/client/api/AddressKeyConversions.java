package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.model.types.RCDType;
import org.blockchain_innovation.factom.client.api.ops.ByteOperations;
import org.blockchain_innovation.factom.client.api.ops.Digests;
import org.blockchain_innovation.factom.client.api.ops.Encoding;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

/**
 * Address Conversions class to help with extracting keys from addresses or creating an address from a key.
 */
@Named
@Singleton
public class AddressKeyConversions {

    /**
     * Get the key from the address. This strips an identifiable prefix and a checksum from the address.
     * The result will be encoded in a given encoding.
     *
     * @param address
     * @param encoding of the address
     * @return the key of the address encoded by the given encoding
     */
    public String addressToKey(String address, Encoding encoding) {
        return encoding.encode(addressToKey(address));

    }

    /**
     * Get the key from the address. This strips an identifiable prefix and a checksum from the address.
     *
     * @param address
     * @return the key of the address
     */
    public byte[] addressToKey(String address) {
        AddressType.assertValidAddress(address);
        byte[] addressBytes = Encoding.BASE58.decode(address);
        if (addressBytes.length != 38) {
            throw new FactomRuntimeException.AssertionException(String.format("Address '%s' is not 38 bytes long!", address));
        }
        return Arrays.copyOfRange(addressBytes, 2, 34);

    }

    /**
     * Get the key from the address. This strips an identifiable prefix and a checksum from the address.
     *
     * @param address
     * @return the key of the address
     */
    public byte[] addressToKey(Address address) {
        return addressToKey(address.getValue());
    }

    public String fctAddressToRcdHash(String address, Encoding encoding) {
        return encoding.encode(fctAddressToRcdHash(address));

    }

    public byte[] fctAddressToRcdHash(String address) {
        AddressType.assertValidAddress(address, AddressType.FACTOID_PUBLIC);
        return addressToKey(address);
    }

    /**
     * Creates an address from a key. The address has an identifiable prefix and a checksum to prevent typos.
     *
     * @param key               of the address
     * @param targetAddressType type of address
     * @param keyEncoding
     * @return the address in a a given encoding
     */
    public String keyToAddress(String key, AddressType targetAddressType, Encoding keyEncoding) {
        return keyToAddress(keyEncoding.decode(key), targetAddressType);
    }

    /**
     * Creates an address from a key. The address has an identifiable prefix and a checksum to prevent typos.
     *
     * @param key               of the address
     * @param targetAddressType type of address
     * @return the address
     */
    public String keyToAddress(byte[] key, AddressType targetAddressType) {
        String hexKey = Encoding.HEX.encode(key);
        if (hexKey.length() != 64) {
            throw new FactomRuntimeException.AssertionException("Invalid key supplied. Key " + hexKey + " is not 64 bytes long");
        }
        byte[] address;

        //// TODO: 13/09/2018
        if (targetAddressType == AddressType.FACTOID_PUBLIC && false) {
            address = Digests.SHA_256.doubleDigest(new ByteOperations().concat(RCDType.TYPE_1.getValue(), key));
        } else {
            address = new ByteOperations().concat(targetAddressType.getAddressPrefix(), key);
        }
        byte[] checksum = Arrays.copyOf(Digests.SHA_256.doubleDigest(address), 4);
        return Encoding.BASE58.encode(new ByteOperations().concat(address, checksum));
    }
}
