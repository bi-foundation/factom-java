package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.impl.OfflineAddressKeyConversions;
import org.junit.Assert;
import org.junit.Test;

public class AddressTest extends AbstractClientTest {
    private static AddressKeyConversions conversions = new AddressKeyConversions();

    @Test
    public void testInvalidAddress() {
        Assert.assertFalse(AddressType.isValidAddress("nope"));
        Assert.assertFalse(AddressType.isValidAddress(EC_PUBLIC_ADDRESS + "nope"));
        try {
            conversions.addressToKey("nope");
            Assert.fail("Invalid address should have failed here");
        } catch (FactomRuntimeException re) {
        }

        try {
            conversions.addressToKey(EC_PUBLIC_ADDRESS + "nope");
            Assert.fail("Invalid address should have failed here");
        } catch (FactomRuntimeException re) {
        }

        try {
            conversions.addressToKey((String) null);
            Assert.fail("Invalid address should have failed here");
        } catch (FactomRuntimeException re) {
        }
        try {
            Address address = new Address("nope");
            Assert.fail("Invalid address should have failed here");
        } catch (FactomRuntimeException re) {
        }


    }

    @Test
    public void testPublicECAddress() {
        Assert.assertTrue(AddressType.isValidAddress(EC_PUBLIC_ADDRESS));
        AddressType type = AddressType.getType(EC_PUBLIC_ADDRESS);
        Assert.assertEquals(AddressType.ENTRY_CREDIT_PUBLIC, type);
        Assert.assertTrue(type.isPublic());
        Assert.assertFalse(type.isPrivate());
        Assert.assertTrue(type.isValid(EC_PUBLIC_ADDRESS));
        Assert.assertEquals(AddressType.Visibility.PUBLIC, type.getVisibility());
        Assert.assertEquals("EC", type.getHumanReadablePrefix());
        Assert.assertEquals(type.getHumanReadablePrefix(), AddressType.getPrefix(EC_PUBLIC_ADDRESS));
    }

    @Test
    public void testPrivateECAddress() {
        Assert.assertTrue(AddressType.isValidAddress(EC_SECRET_ADDRESS));
        AddressType type = AddressType.getType(EC_SECRET_ADDRESS);
        Assert.assertEquals(AddressType.ENTRY_CREDIT_SECRET, type);
        Assert.assertFalse(type.isPublic());
        Assert.assertTrue(type.isPrivate());
        Assert.assertEquals(AddressType.Visibility.PRIVATE, type.getVisibility());
        Assert.assertEquals("Es", type.getHumanReadablePrefix());
        Assert.assertEquals(type.getHumanReadablePrefix(), AddressType.getPrefix(EC_SECRET_ADDRESS));
    }


    @Test
    public void testPublicFCTAddress() {
        Assert.assertTrue(AddressType.isValidAddress(FCT_PUBLIC_ADDRESS));
        AddressType type = AddressType.getType(FCT_PUBLIC_ADDRESS);
        Assert.assertEquals(AddressType.FACTOID_PUBLIC, type);
        Assert.assertTrue(type.isPublic());
        Assert.assertFalse(type.isPrivate());
        Assert.assertEquals(AddressType.Visibility.PUBLIC, type.getVisibility());
        Assert.assertEquals("FA", type.getHumanReadablePrefix());
        Assert.assertEquals(type.getHumanReadablePrefix(), AddressType.getPrefix(FCT_PUBLIC_ADDRESS));
    }


    @Test
    public void testSecretFCTAddress() {
        Assert.assertTrue(AddressType.isValidAddress(FCT_SECRET_ADDRESS));
        AddressType type = AddressType.getType(FCT_SECRET_ADDRESS);
        Assert.assertEquals(AddressType.FACTOID_SECRET, type);
        Assert.assertFalse(type.isPublic());
        Assert.assertTrue(type.isPrivate());
        Assert.assertEquals(AddressType.Visibility.PRIVATE, type.getVisibility());
        Assert.assertEquals("Fs", type.getHumanReadablePrefix());
        Assert.assertEquals(type.getHumanReadablePrefix(), AddressType.getPrefix(FCT_SECRET_ADDRESS));
    }

    @Test
    public void testConversionToPublicFCTAddress() {
        byte[] privKey = conversions.addressToKey(FCT_SECRET_ADDRESS);
        Assert.assertNotNull(privKey);
        String privAddress = conversions.keyToAddress(privKey, AddressType.FACTOID_SECRET);
        Assert.assertEquals(FCT_SECRET_ADDRESS, privAddress);
    }

    @Test
    public void testKeyToAddress() {
        Assert.assertEquals("FA1y5ZGuHSLmf2TqNf6hVMkPiNGyQpQDTFJvDLRkKQaoPo4bmbgu", conversions.keyToAddress("0000000000000000000000000000000000000000000000000000000000000000", AddressType.FACTOID_PUBLIC, Encoding.HEX));
        Assert.assertEquals("FA3upjWMKHmStAHR5ZgKVK4zVHPb8U74L2wzKaaSDQEonHajiLeq", conversions.keyToAddress("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", AddressType.FACTOID_PUBLIC, Encoding.HEX));
        Assert.assertEquals("FA1y5ZGuHSLmf2TqNf6hVMkPiNGyQpQDTFJvDLRkKQaoPo4bmbgu", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.FACTOID_PUBLIC));
        Assert.assertEquals("FA3upjWMKHmStAHR5ZgKVK4zVHPb8U74L2wzKaaSDQEonHajiLeq", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.FACTOID_PUBLIC));
        Assert.assertEquals("EC1m9mouvUQeEidmqpUYpYtXg8fvTYi6GNHaKg8KMLbdMBrFfmUa", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.ENTRY_CREDIT_PUBLIC));
        Assert.assertEquals("EC3htx3MxKqKTrTMYj4ApWD8T3nYBCQw99veRvH1FLFdjgN6GuNK", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.ENTRY_CREDIT_PUBLIC));
        Assert.assertEquals("Fs1KWJrpLdfucvmYwN2nWrwepLn8ercpMbzXshd1g8zyhKXLVLWj", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.FACTOID_SECRET));
        Assert.assertEquals("Fs3GFV6GNV6ar4b8eGcQWpGFbFtkNWKfEPdbywmha8ez5p7XMJyk", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.FACTOID_SECRET));
        Assert.assertEquals("Es2Rf7iM6PdsqfYCo3D1tnAR65SkLENyWJG1deUzpRMQmbh9F3eG", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.ENTRY_CREDIT_SECRET));
        Assert.assertEquals("Es4NQHwo8F4Z4oMnVwndtjV1rzZN3t5pP5u5jtdgiR1RA6FH4Tmc", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.ENTRY_CREDIT_SECRET));
    }

    @Test
    public void testAddressToKey() {
        Assert.assertEquals("FA1y5ZGuHSLmf2TqNf6hVMkPiNGyQpQDTFJvDLRkKQaoPo4bmbgu", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.FACTOID_PUBLIC));
        Assert.assertArrayEquals(Encoding.HEX.decode("98fb8ffa591adc5f20ee4887affe06c18ca3b97cbda1a74a12944c1c26fdf864"), conversions.addressToKey("EC2vXWYkAPduo3oo2tPuzA44Tm7W6Cj7SeBr3fBnzswbG5rrkSTD"));
        Assert.assertArrayEquals(Encoding.HEX.decode("776b5cf08edea510711e2bc4a73f2b5118008906c5afd2e5786cf817fa279b80"), conversions.addressToKey("Es3LFXNj5vHBw8c9kM98HKR69CJjUTyTPv4BdxoRbMQJ8zifxkgV"));
        Assert.assertArrayEquals(Encoding.HEX.decode("d48189215e445ea7e8dbf707c48922ab25a23552d8eae40cc5e9cd6b1a36963c"), conversions.addressToKey("Fs2w6VL6cwBqt6SpUyPLvdo9TK834gCr52Y225z8C5aHPAFav36X"));
    }

    @Test
    public void testAddressToPublic() {
        OfflineAddressKeyConversions conversions = new OfflineAddressKeyConversions();
        String publicAddress = conversions.addressToPublicAddress(EC_SECRET_ADDRESS);

        AddressType.ENTRY_CREDIT_PUBLIC.assertValid(publicAddress);
        Assert.assertEquals(EC_PUBLIC_ADDRESS, publicAddress);
    }
}
