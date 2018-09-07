package org.blockchain_innovation.factom.client.api;

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


    public String keyToAddress(byte[] key, AddressType addressType) {
        String hexKey = Encoding.HEX.encode(key);
        if (hexKey.length() != 32) {
            throw new FactomRuntimeException.AssertionException("Invalid key supplied. Key " + hexKey + " is not 32 bytes long");
        }
        byte[] address = new ByteOperations().concat(Encoding.HEX.decode(addressType.getPrefix()), key);
        if (addressType == AddressType.FACTOID_PUBLIC) {
            address = Digests.SHA_256.doubleDigest(new ByteOperations().concat(RCDType.TYPE_1.getValue(), key));
        }
        byte[] checksum = Digests.SHA_256.doubleDigest(Arrays.copyOf(address, 4));
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
