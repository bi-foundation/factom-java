package org.blockchain_innovation.factom.client.api.ops;

import org.blockchain_innovation.factom.client.AbstractClientTest;
import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.model.ECAddress;
import org.junit.Assert;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SigningOperationsTest extends AbstractClientTest {
    private static final SigningOperations signingOperations = new SigningOperations();
    private static final AddressKeyConversions keyConversions = new AddressKeyConversions();

    @Test
    public void testSignAndVerify() {
        Assert.assertTrue(testSignAndVerify(FCT_SECRET_ADDRESS));
        Assert.assertTrue(testSignAndVerify(EC_SECRET_ADDRESS));
        Assert.assertTrue(testSignAndVerify(IDENTITY1_SECRET_ADDRESS));
    }

    private boolean testSignAndVerify(String privateAddress) {
        byte[] bytesToSign = new byte[64];
        try {
            SecureRandom.getInstanceStrong().nextBytes(bytesToSign);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] signature = signingOperations.sign(bytesToSign, new ECAddress(privateAddress));
        Assert.assertNotNull(signature);
        byte[] publicKey = keyConversions.addressToPublicKey(privateAddress);

        return signingOperations.verifySign(signature, bytesToSign, publicKey);
    }
}
