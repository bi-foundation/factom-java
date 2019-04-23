package org.blockchain_innovation.factom.client.api.ops;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import org.blockchain_innovation.factom.client.api.SignatureProdiver;
import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.impl.OfflineAddressKeyConversions;

import java.security.*;

public class AddressSignatureProvider implements SignatureProdiver {
    private static final OfflineAddressKeyConversions CONVERSIONS = new OfflineAddressKeyConversions();

    private Address address;

    /**
     * Create an address related signature provider
     *
     * @param address The address to use for signing
     */
    public AddressSignatureProvider(Address address) {
        setAddress(address);
    }

    /**
     * Get the address for the provide
     *
     * @return The address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Set the address for the provider
     *
     * @param address The address
     */
    public void setAddress(Address address) {
        this.address = address;
    }


    @Override
    public Address getPublicECAddress() {
        return CONVERSIONS.addressToPublicAddress(getAddress());
    }

    @Override
    public byte[] sign(byte[] message) {
        byte[] privateKey = CONVERSIONS.addressToKey(getAddress());
        EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(privateKey, EdDSANamedCurveTable.ED_25519_CURVE_SPEC);
        EdDSAPrivateKey keyIn = new EdDSAPrivateKey(privateKeySpec);

        try {
            Signature instance = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
            instance.initSign(keyIn);
            instance.update(message);

            byte[] signed = instance.sign();
            return signed;
        } catch (InvalidKeyException e) {
            throw new FactomException.ClientException(String.format("invalid key: %s", e.getMessage()), e);
        } catch (SignatureException | NoSuchAlgorithmException e) {
            throw new FactomException.ClientException("failed to sign message", e);
        }
    }
}
