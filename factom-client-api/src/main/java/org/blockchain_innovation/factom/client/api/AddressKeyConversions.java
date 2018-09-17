package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.model.types.RCDType;
import org.blockchain_innovation.factom.client.api.ops.ByteOperations;
import org.blockchain_innovation.factom.client.api.ops.Digests;
import org.blockchain_innovation.factom.client.api.ops.Encoding;

import java.util.Arrays;

public class AddressKeyConversions {


    public String addressToKey(String address, Encoding encoding) {
        return encoding.encode(addressToKey(address));

    }

    public byte[] addressToKey(String address) {
        AddressType.assertValidAddress(address);
        byte[] addressBytes = Encoding.BASE58.decode(address);
        if (addressBytes.length != 38) {
            throw new FactomRuntimeException.AssertionException(String.format("Address '%s' is not 38 bytes long!", address));
        }
        return Arrays.copyOfRange(addressBytes, 2, 34);

    }

    public String fctAddressToRcdHash(String address, Encoding encoding) {
        return encoding.encode(fctAddressToRcdHash(address));

    }

    public byte[] fctAddressToRcdHash(String address) {
        AddressType.assertValidAddress(address, AddressType.FACTOID_PUBLIC);
        return addressToKey(address);
    }

    public String keyToAddress(String key, AddressType targetAddressType, Encoding keyEncoding) {
        return keyToAddress(keyEncoding.decode(key), targetAddressType);
    }

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

    public String addressToPublicAddress(String address) {
        AddressType addressType = AddressType.getType(address);
        if (addressType.isPublic()) {
            return address;
        }
        byte[] pk = addressToKey(address);
        return keyToAddress(pk, AddressType.getType(address));
    }


}
