package org.blockchain_innovation.factom.client.api.ops;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.SignatureProdiver;
import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.model.Address;

import javax.inject.Named;
import javax.inject.Singleton;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.security.NoSuchAlgorithmException;

@Named
@Singleton
public class SigningOperations {

    private final AddressKeyConversions addressKeyConversions = new AddressKeyConversions();

    public byte[] sign(byte[] message, SignatureProdiver signatureProdiver) {
        return signatureProdiver.sign(message);
    }

    /**
     * sign message.
     *
     * @param message
     * @param address
     * @return
     * @throws FactomException.ClientException
     */
    public byte[] sign(byte[] message, Address address) throws FactomException.ClientException {
        byte[] privateKey = addressKeyConversions.addressToKey(address);
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

    public boolean verifySign(byte[] signature, byte[] originalData, Address address) {
        byte[] publicKey = addressKeyConversions.addressToKey(address);
        return verifySign(signature, originalData, publicKey);
    }

    public boolean verifySign(byte[] signature, byte[] originalData, byte[] publicKey) {
        EdDSAPublicKeySpec publicKeySpec = new EdDSAPublicKeySpec(publicKey, EdDSANamedCurveTable.ED_25519_CURVE_SPEC);
        EdDSAPublicKey keyIn = new EdDSAPublicKey(publicKeySpec);
        try {
            Signature verifyInstance = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
            verifyInstance.initVerify(keyIn);
            verifyInstance.update(originalData);
            boolean verified = verifyInstance.verify(signature);
            return verified;
        } catch (NoSuchAlgorithmException | SignatureException e) {
            throw new FactomException.ClientException("failed to sign message", e);
        } catch (InvalidKeyException e) {
            throw new FactomException.ClientException(String.format("invalid key: %s", e.getMessage()), e);
        }
    }
}
