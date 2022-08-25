package org.blockchain_innovation.factom.client.api.ops;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.SignatureProvider;
import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;

import java.nio.charset.StandardCharsets;
import java.security.*;

public class AddressSignatureProvider implements SignatureProvider {
    private static final AddressKeyConversions CONVERSIONS = new AddressKeyConversions();

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
        AddressType.assertVisibility(address.getValue(), AddressType.Visibility.PRIVATE);
        this.address = address;
    }


    @Override
    public Address getPublicAddress() {
        return CONVERSIONS.addressToPublicAddress(getAddress());
    }

    @Override
    public AddressType getAddressType() {
        return address.getType();
    }

    @Override
    public byte[] sign(byte[] message) {

        final Address address = getAddress();
        if(address.getType() == AddressType.LITE_ACCOUNT) {
            return getAddress().getValue().getBytes(StandardCharsets.UTF_8);
        } else {
            byte[] privateKey = CONVERSIONS.addressToKey(address);
            EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(privateKey, EdDSANamedCurveTable.ED_25519_CURVE_SPEC);
            EdDSAPrivateKey keyIn = new EdDSAPrivateKey(privateKeySpec);

            try {
                Signature instance = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
                instance.initSign(keyIn);
                instance.update(message);

                return instance.sign();
            } catch (InvalidKeyException e) {
                throw new FactomException.ClientException(String.format("invalid key: %s", e.getMessage()), e);
            } catch (SignatureException | NoSuchAlgorithmException e) {
                throw new FactomException.ClientException("failed to sign message", e);
            }
        }
    }
}
