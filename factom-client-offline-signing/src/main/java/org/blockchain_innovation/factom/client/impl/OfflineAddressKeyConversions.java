package org.blockchain_innovation.factom.client.impl;

import net.i2p.crypto.eddsa.math.GroupElement;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.ops.Digests;

import java.util.Arrays;

public class OfflineAddressKeyConversions extends AddressKeyConversions {
    /**
     * Create a public address. If the address is public it will return itself. Otherwise it will extract the key
     * from the address. Calculates the public key from the private key. This will be returned as an address.
     * If a Factoid Address is given, an Factoid Private Key will be returned.
     * If a Entry Credit Address is given, an Entry Credit Private Key is returned
     *
     * @param address
     * @return an address
     */
    public Address addressToPublicAddress(Address address) {
        return new Address(addressToPublicAddress(address.getValue()));
    }

    /**
     * Create a public address. If the address is public it will return itself. Otherwise it will extract the key
     * from the address. Calculates the public key from the private key. This will be returned as an address.
     * If a Factoid Address is given, an Factoid Private Key will be returned.
     * If a Entry Credit Address is given, an Entry Credit Private Key is returned
     *
     * @param address
     * @return an address
     */
    public String addressToPublicAddress(String address) {
        AddressType addressType = AddressType.getType(address);
        if (addressType.isPublic()) {
            return address;
        }
        byte[] privateKey = addressToKey(address);

        // EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(privateKey, EdDSANamedCurveTable.ED_25519_CURVE_SPEC);
        // EdDSAPrivateKey keyIn = new EdDSAPrivateKey(privateKeySpec);
        // byte[] pk = keyIn.getA().toByteArray();

        byte[] digest = Digests.SHA_512.digest(privateKey);
        digest[0] &= 248;
        digest[31] &= 127;
        digest[31] |= 64;

        byte[] hBytes = Arrays.copyOf(digest, 32);

        // GeScalarMultBase computes h = a*B, where
        // a = a[0]+256*a[1]+...+256^31 a[31]
        // B is the Ed25519 base point (x,4/5) with x positive.
        GroupElement elementA = EdDSANamedCurveTable.ED_25519_CURVE_SPEC.getB().scalarMultiply(hBytes);
        byte[] publicKey = elementA.toByteArray();

        AddressType targetAddressType = addressType == AddressType.FACTOID_SECRET ? AddressType.FACTOID_PUBLIC : AddressType.ENTRY_CREDIT_PUBLIC;

        return keyToAddress(publicKey, targetAddressType);
    }

    public byte[] addressToPublicKey(String address) {
        byte[] privateKey = addressToKey(address);

        // EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(privateKey, EdDSANamedCurveTable.ED_25519_CURVE_SPEC);
        // EdDSAPrivateKey keyIn = new EdDSAPrivateKey(privateKeySpec);
        // byte[] pk = keyIn.getA().toByteArray();

        byte[] digest = Digests.SHA_512.digest(privateKey);
        digest[0] &= 248;
        digest[31] &= 127;
        digest[31] |= 64;

        byte[] hBytes = Arrays.copyOf(digest, 32);

        // GeScalarMultBase computes h = a*B, where
        // a = a[0]+256*a[1]+...+256^31 a[31]
        // B is the Ed25519 base point (x,4/5) with x positive.
        GroupElement elementA = EdDSANamedCurveTable.ED_25519_CURVE_SPEC.getB().scalarMultiply(hBytes);
        return elementA.toByteArray();
    }
}
