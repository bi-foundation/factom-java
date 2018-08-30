package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.api.AddressType;
import org.junit.Assert;
import org.junit.Test;

public class AddressTest extends AbstractClientTest {

    @Test
    public void testPublicECAddress() {
        Assert.assertTrue(AddressType.isValidAddress(EC_PUBLIC_ADDRESS));
        AddressType type = AddressType.getType(EC_PUBLIC_ADDRESS);
        Assert.assertEquals(AddressType.ENTRY_CREDIT_PUBLIC, type);
        Assert.assertTrue(type.isPublic());
        Assert.assertFalse(type.isPrivate());
        Assert.assertTrue(type.isValid(EC_PUBLIC_ADDRESS));
        Assert.assertEquals(AddressType.Visibility.PUBLIC, type.getVisibility());
        Assert.assertEquals("EC", type.getPrefix());
        Assert.assertEquals(type.getPrefix(), AddressType.getPrefix(EC_PUBLIC_ADDRESS));
    }

    @Test
    public void testPrivateECAddress() {
        Assert.assertTrue(AddressType.isValidAddress(EC_SECRET_ADDRESS));
        AddressType type = AddressType.getType(EC_SECRET_ADDRESS);
        Assert.assertEquals(AddressType.ENTRY_CREDIT_SECRET, type);
        Assert.assertFalse(type.isPublic());
        Assert.assertTrue(type.isPrivate());
        Assert.assertEquals(AddressType.Visibility.PRIVATE, type.getVisibility());
        Assert.assertEquals("Es", type.getPrefix());
        Assert.assertEquals(type.getPrefix(), AddressType.getPrefix(EC_SECRET_ADDRESS));
    }


    @Test
    public void testPublicFCTAddress() {
        Assert.assertTrue(AddressType.isValidAddress(FCT_PUBLIC_ADDRESS));
        AddressType type = AddressType.getType(FCT_PUBLIC_ADDRESS);
        Assert.assertEquals(AddressType.FACTOID_PUBLIC, type);
        Assert.assertTrue(type.isPublic());
        Assert.assertFalse(type.isPrivate());
        Assert.assertEquals(AddressType.Visibility.PUBLIC, type.getVisibility());
        Assert.assertEquals("FA", type.getPrefix());
        Assert.assertEquals(type.getPrefix(), AddressType.getPrefix(FCT_PUBLIC_ADDRESS));
    }


    @Test
    public void testSecretFCTAddress() {
        Assert.assertTrue(AddressType.isValidAddress(FCT_SECRET_ADDRESS));
        AddressType type = AddressType.getType(FCT_SECRET_ADDRESS);
        Assert.assertEquals(AddressType.FACTOID_SECRET, type);
        Assert.assertFalse(type.isPublic());
        Assert.assertTrue(type.isPrivate());
        Assert.assertEquals(AddressType.Visibility.PRIVATE, type.getVisibility());
        Assert.assertEquals("Fs", type.getPrefix());
        Assert.assertEquals(type.getPrefix(), AddressType.getPrefix(FCT_SECRET_ADDRESS));
    }
}
