package org.blockchain_innovation.factom.client.api.ops;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.SignatureProdiver;
import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.model.Address;

import javax.inject.Named;
import javax.inject.Singleton;
import java.security.*;

@Named
@Singleton
public class SigningOperations {

    private final AddressKeyConversions addressKeyConversions = new AddressKeyConversions();

    public byte[] sign(byte[] message, SignatureProdiver signatureProdiver) {
        return signatureProdiver.sign(message);
    }

    /**
     * sign message. We delegate to teh Address Signature Provider.
     *
     * @param message The input message (digest)
     * @param address The address to sign (public or private)
     * @return Teh signature using ed25519
     * @throws FactomException.ClientException
     */
    public byte[] sign(byte[] message, Address address) throws FactomException.ClientException {
        return new AddressSignatureProvider(address).sign(message);
    }


    /**
     * Verify whether a signature is valid and has been signed by the supplied address
     *
     * @param signature    The signature
     * @param originalData The original message
     * @param address      The address used
     * @return A boolean whether the signatre matches the address and original data
     */
    public boolean verifySign(byte[] signature, byte[] originalData, Address address) {
        byte[] publicKey = addressKeyConversions.addressToKey(address);
        return verifySign(signature, originalData, publicKey);
    }

    /**
     * Verify whether a signature is valid and has been signed by the public key
     *
     * @param signature    The signature
     * @param originalData The original data
     * @param publicKey    The public key
     * @return Whether the original data has been signed by the private key belonging to the supplied public key
     */
    public boolean verifySign(byte[] signature, byte[] originalData, byte[] publicKey) {
        EdDSAPublicKeySpec publicKeySpec = new EdDSAPublicKeySpec(publicKey, EdDSANamedCurveTable.ED_25519_CURVE_SPEC);
        EdDSAPublicKey keyIn = new EdDSAPublicKey(publicKeySpec);
        try {
            Signature verifyInstance = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
            verifyInstance.initVerify(keyIn);
            verifyInstance.update(originalData);
            return verifyInstance.verify(signature);
        } catch (NoSuchAlgorithmException | SignatureException e) {
            throw new FactomException.ClientException("failed to sign message", e);
        } catch (InvalidKeyException e) {
            throw new FactomException.ClientException(String.format("invalid key: %s", e.getMessage()), e);
        }
    }
}
