package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
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
    private static final Logger logger = LogFactory.getLogger(AddressKeyConversions.class);

    /**
     * Get the key from the address. This strips an identifiable prefix and a checksum from the address.
     * The result will be encoded in a given encoding.
     *
     * @param address  The address to get the key of.
     * @param encoding of the key.
     * @return the key of the address encoded using the supplied encoding.
     */
    public String addressToKey(String address, Encoding encoding) {
        String key = encoding.encode(addressToKey(address));
        logger.debug("Extracted key '%s' from '%s' using %s-encoding", key, address, encoding.name());
        return key;
    }

    /**
     * Get the key from the address. This strips an identifiable prefix and a checksum from the address.
     *
     * @param address The address to get the key of.
     * @return the key of the address in byte form.
     */
    public byte[] addressToKey(String address) {
        AddressType.assertValidAddress(address);
        byte[] addressBytes = Encoding.BASE58.decode(address);
        if (addressBytes.length == 38) {
            byte[] key = Arrays.copyOfRange(addressBytes, 2, 34);
            logger.debug("Extracted raw key from address '%s'", address);
            return key;
        } else if (addressBytes.length == 39) {
            byte[] key = Arrays.copyOfRange(addressBytes, 3, 35);
            logger.debug("Extracted raw key from address '%s'", address);
            return key;
        } else {
            throw new FactomRuntimeException.AssertionException(String.format("Address '%s' is not 38 bytes long!", address));

        }
    }

    /**
     * Get the key from the address. This strips an identifiable prefix and a checksum from the address.
     *
     * @param address The address to get the key of.
     * @return the key of the address in byte form.
     */
    public byte[] addressToKey(Address address) {
        return addressToKey(address.getValue());
    }

    /**
     * Converts a FCT address into an RCD hash. Checks for a valid FCT (public) address.
     *
     * @param address  The FCT address.
     * @param encoding The encoding to use.
     * @return The RCD hash using the supplied encoding.
     */
    public String fctAddressToRcdHash(String address, Encoding encoding) {
        return encoding.encode(fctAddressToRcdHash(address));

    }

    /**
     * Converts a FCT address into an RCD hash. Checks for a valid FCT (public) address.
     *
     * @param address The FCT address.
     * @return The RCD hash in raw form.
     */
    public byte[] fctAddressToRcdHash(String address) {
        AddressType.assertValidAddress(address, AddressType.FACTOID_PUBLIC);
        return addressToKey(address);
    }

    /**
     * Creates an address from a key. The address has an identifiable prefix and a checksum to prevent typos.
     *
     * @param key               of the address.
     * @param targetAddressType type of address.
     * @param keyEncoding       The encoding used for the supplied key.
     * @return the address in base58 encoding.
     */
    public String keyToAddress(String key, AddressType targetAddressType, Encoding keyEncoding) {
        return keyToAddress(keyEncoding.decode(key), targetAddressType);
    }

    /**
     * Creates an address from a key. The address has an identifiable prefix and a checksum to prevent typos.
     *
     * @param key               of the address.
     * @param targetAddressType type of address.
     * @return the address in base58 encoding.
     */
    public String keyToAddress(byte[] key, AddressType targetAddressType) {
        String hexKey = Encoding.HEX.encode(key);
        if (hexKey.length() != 64) {
            throw new FactomRuntimeException.AssertionException("Invalid key supplied. Key " + hexKey + " is not 64 bytes long but was " + hexKey.length());
        }

        byte[] addressKey = key;
        if (targetAddressType == AddressType.FACTOID_PUBLIC) {
            addressKey = Digests.SHA_256.doubleDigest(new ByteOperations().concat(RCDType.TYPE_1.getValue(), addressKey));
        }

        byte[] address = new ByteOperations().concat(targetAddressType.getAddressPrefix(), addressKey);

        byte[] checksum = Arrays.copyOf(Digests.SHA_256.doubleDigest(address), 4);
        String result = Encoding.BASE58.encode(new ByteOperations().concat(address, checksum));
        logger.debug("Extracted address '%s' from %s-key '%s'", result, targetAddressType.name(), hexKey);
        return result;
    }
}
