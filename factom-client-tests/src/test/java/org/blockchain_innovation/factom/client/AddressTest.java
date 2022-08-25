package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.ECAddress;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class AddressTest extends AbstractClientTest {
    private static final AddressKeyConversions conversions = new AddressKeyConversions();


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
            Address address = new ECAddress("nope");
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
    public void testPublicIDENTITY1Address() {
        Assert.assertTrue(AddressType.isValidAddress(IDENTITY1_PUBLIC_ADDRESS));
        AddressType type = AddressType.getType(IDENTITY1_PUBLIC_ADDRESS);
        Assert.assertEquals(AddressType.IDENTITY_PUBLIC1, type);
        Assert.assertTrue(type.isPublic());
        Assert.assertFalse(type.isPrivate());
        Assert.assertEquals(AddressType.Visibility.PUBLIC, type.getVisibility());
        Assert.assertEquals("id1", type.getHumanReadablePrefix());
        Assert.assertEquals(type.getHumanReadablePrefix(), AddressType.getPrefix(IDENTITY1_PUBLIC_ADDRESS));
    }


    @Test
    public void testSecretIDENTITY1Address() {
        Assert.assertTrue(AddressType.isValidAddress(IDENTITY1_SECRET_ADDRESS));
        AddressType type = AddressType.getType(IDENTITY1_SECRET_ADDRESS);
        Assert.assertEquals(AddressType.IDENTITY_PRIVATE1, type);
        Assert.assertFalse(type.isPublic());
        Assert.assertTrue(type.isPrivate());
        Assert.assertEquals(AddressType.Visibility.PRIVATE, type.getVisibility());
        Assert.assertEquals("sk1", type.getHumanReadablePrefix());
        Assert.assertEquals(type.getHumanReadablePrefix(), AddressType.getPrefix(IDENTITY1_SECRET_ADDRESS));
    }

    @Test
    public void testConversionToPublicFCTAddress() {
        byte[] privKey = conversions.addressToKey(FCT_SECRET_ADDRESS);
        Assert.assertNotNull(privKey);
        String privAddress = conversions.keyToAddress(privKey, AddressType.FACTOID_SECRET);
        Assert.assertEquals(FCT_SECRET_ADDRESS, privAddress);
    }

    @Test
    public void testConversionToPublicIDENTITYAddress() {
        byte[] privKey = conversions.addressToKey(IDENTITY1_SECRET_ADDRESS);
        Assert.assertNotNull(privKey);
        String privAddress = conversions.keyToAddress(privKey, AddressType.IDENTITY_PRIVATE1);
        Assert.assertEquals(IDENTITY1_SECRET_ADDRESS, privAddress);
    }

    @Test
    public void testKeyToAddress() {
//        Assert.assertEquals("FA1y5ZGuHSLmf2TqNf6hVMkPiNGyQpQDTFJvDLRkKQaoPo4bmbgu", conversions.keyToAddress("0000000000000000000000000000000000000000000000000000000000000000", AddressType.FACTOID_PUBLIC, Encoding.HEX));
//        Assert.assertEquals("FA3upjWMKHmStAHR5ZgKVK4zVHPb8U74L2wzKaaSDQEonHajiLeq", conversions.keyToAddress("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", AddressType.FACTOID_PUBLIC, Encoding.HEX));
//        Assert.assertEquals("FA1y5ZGuHSLmf2TqNf6hVMkPiNGyQpQDTFJvDLRkKQaoPo4bmbgu", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.FACTOID_PUBLIC));
//        Assert.assertEquals("FA3upjWMKHmStAHR5ZgKVK4zVHPb8U74L2wzKaaSDQEonHajiLeq", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.FACTOID_PUBLIC));
        Assert.assertEquals("EC1m9mouvUQeEidmqpUYpYtXg8fvTYi6GNHaKg8KMLbdMBrFfmUa", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.ENTRY_CREDIT_PUBLIC));
        Assert.assertEquals("EC3htx3MxKqKTrTMYj4ApWD8T3nYBCQw99veRvH1FLFdjgN6GuNK", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.ENTRY_CREDIT_PUBLIC));
        Assert.assertEquals("Fs1KWJrpLdfucvmYwN2nWrwepLn8ercpMbzXshd1g8zyhKXLVLWj", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.FACTOID_SECRET));
        Assert.assertEquals("Fs3GFV6GNV6ar4b8eGcQWpGFbFtkNWKfEPdbywmha8ez5p7XMJyk", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.FACTOID_SECRET));
        Assert.assertEquals("Es2Rf7iM6PdsqfYCo3D1tnAR65SkLENyWJG1deUzpRMQmbh9F3eG", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.ENTRY_CREDIT_SECRET));
        Assert.assertEquals("Es4NQHwo8F4Z4oMnVwndtjV1rzZN3t5pP5u5jtdgiR1RA6FH4Tmc", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.ENTRY_CREDIT_SECRET));
        Assert.assertEquals("sk11pz4AG9XgB1eNVkbppYAWsgyg7sftDXqBASsagKJqvVRKYodCU", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.IDENTITY_PRIVATE1));
        Assert.assertEquals("sk13mjEPiBP6rEnC5TWQSY7qUTtnjbKb4QcpEZ7jNDJVvsupCg9DV", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.IDENTITY_PRIVATE1));
        Assert.assertEquals("sk229KM7j76STogyvuoDSWn8rvT6bRB1VoSMHgC5KD8W88E26iQM3", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.IDENTITY_PRIVATE2));
        Assert.assertEquals("sk2464XMB8ws92poWcho4WjTThNDD8piLgDzMnSE178A8WiU46gJy", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.IDENTITY_PRIVATE2));
        Assert.assertEquals("sk32Tee5C4fCkbjbN4zc4VPkr9vX4xg8n53XQuWZx6xAKm2cAP7gv", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.IDENTITY_PRIVATE3));
        Assert.assertEquals("sk34QPpJe6WdRpsQwmuBgVM5SvqdggKqcwqAV1kidzwpL9X86sVi9", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.IDENTITY_PRIVATE3));
        Assert.assertEquals("sk42myw2f2Dy3PnCoEBzgU1NqPPwYWBG4LehY8q4azmpXPqGY6Bqu", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.IDENTITY_PRIVATE4));
        Assert.assertEquals("sk44ij7G745Picv2Nw6aJTxhSAK4ADpxuDSLcF5DGtmUXnKs6XT1F", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.IDENTITY_PRIVATE4));
        Assert.assertEquals("id11qFJ7fe26N29hrY3f1gUQC7UYArUg2GEy1rpPp2ExbnJdSj3mN", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.IDENTITY_PUBLIC1));
        Assert.assertEquals("id13mzUM7fsX3FHXSExEdgRintPena8Ns92c5y4YVvEccAoEttNTG", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.IDENTITY_PUBLIC1));
        Assert.assertEquals("id229ab58barepCKHhF3df62BLwxePyoJXr9968tSv4coR7LbtoFL", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.IDENTITY_PUBLIC2));
        Assert.assertEquals("id246KmJadSHL3L8sQ9dFf3Ln7s5G7dW9QdnDCP38p4GoobsaTCHN", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.IDENTITY_PUBLIC2));
        Assert.assertEquals("id32Tut2bZ9cwcEvirSSFdheAaRP7wUvaoTKGKTP5otH13uzjcHTd", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.IDENTITY_PUBLIC3));
        Assert.assertEquals("id34Qf4G3b13cqNkJZM1sdexmMLVjf8dRgExLRhXmhsw1SQSzthdm", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.IDENTITY_PUBLIC3));
        Assert.assertEquals("id42nFAz4WiPEQHYA1dpscKG9otobUz3s54VPYmsihhwCgibnEPW5", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.IDENTITY_PUBLIC4));
        Assert.assertEquals("id44izMDWYZoudRMjiYQVcGakaovDCdkhwr8Tf22QbhbD5D934waE", conversions.keyToAddress(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), AddressType.IDENTITY_PUBLIC4));
    }

    @Test
    public void testAddressToKey() {
//        Assert.assertEquals("FA1y5ZGuHSLmf2TqNf6hVMkPiNGyQpQDTFJvDLRkKQaoPo4bmbgu", conversions.keyToAddress(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), AddressType.FACTOID_PUBLIC));
        Assert.assertArrayEquals(Encoding.HEX.decode("98fb8ffa591adc5f20ee4887affe06c18ca3b97cbda1a74a12944c1c26fdf864"), conversions.addressToKey("EC2vXWYkAPduo3oo2tPuzA44Tm7W6Cj7SeBr3fBnzswbG5rrkSTD"));
        Assert.assertArrayEquals(Encoding.HEX.decode("776b5cf08edea510711e2bc4a73f2b5118008906c5afd2e5786cf817fa279b80"), conversions.addressToKey("Es3LFXNj5vHBw8c9kM98HKR69CJjUTyTPv4BdxoRbMQJ8zifxkgV"));
        Assert.assertArrayEquals(Encoding.HEX.decode("d48189215e445ea7e8dbf707c48922ab25a23552d8eae40cc5e9cd6b1a36963c"), conversions.addressToKey("Fs2w6VL6cwBqt6SpUyPLvdo9TK834gCr52Y225z8C5aHPAFav36X"));
        Assert.assertArrayEquals(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), conversions.addressToKey("sk11pz4AG9XgB1eNVkbppYAWsgyg7sftDXqBASsagKJqvVRKYodCU"));
        Assert.assertArrayEquals(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), conversions.addressToKey("sk229KM7j76STogyvuoDSWn8rvT6bRB1VoSMHgC5KD8W88E26iQM3"));
        Assert.assertArrayEquals(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), conversions.addressToKey("sk32Tee5C4fCkbjbN4zc4VPkr9vX4xg8n53XQuWZx6xAKm2cAP7gv"));
        Assert.assertArrayEquals(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), conversions.addressToKey("sk42myw2f2Dy3PnCoEBzgU1NqPPwYWBG4LehY8q4azmpXPqGY6Bqu"));
        Assert.assertArrayEquals(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), conversions.addressToKey("id11qFJ7fe26N29hrY3f1gUQC7UYArUg2GEy1rpPp2ExbnJdSj3mN"));
        Assert.assertArrayEquals(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), conversions.addressToKey("id229ab58barepCKHhF3df62BLwxePyoJXr9968tSv4coR7LbtoFL"));
        Assert.assertArrayEquals(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), conversions.addressToKey("id32Tut2bZ9cwcEvirSSFdheAaRP7wUvaoTKGKTP5otH13uzjcHTd"));
        Assert.assertArrayEquals(Encoding.HEX.decode("0000000000000000000000000000000000000000000000000000000000000000"), conversions.addressToKey("id42nFAz4WiPEQHYA1dpscKG9otobUz3s54VPYmsihhwCgibnEPW5"));
        Assert.assertArrayEquals(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), conversions.addressToKey("sk13mjEPiBP6rEnC5TWQSY7qUTtnjbKb4QcpEZ7jNDJVvsupCg9DV"));
        Assert.assertArrayEquals(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), conversions.addressToKey("sk2464XMB8ws92poWcho4WjTThNDD8piLgDzMnSE178A8WiU46gJy"));
        Assert.assertArrayEquals(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), conversions.addressToKey("sk34QPpJe6WdRpsQwmuBgVM5SvqdggKqcwqAV1kidzwpL9X86sVi9"));
        Assert.assertArrayEquals(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), conversions.addressToKey("sk44ij7G745Picv2Nw6aJTxhSAK4ADpxuDSLcF5DGtmUXnKs6XT1F"));
        Assert.assertArrayEquals(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), conversions.addressToKey("id13mzUM7fsX3FHXSExEdgRintPena8Ns92c5y4YVvEccAoEttNTG"));
        Assert.assertArrayEquals(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), conversions.addressToKey("id246KmJadSHL3L8sQ9dFf3Ln7s5G7dW9QdnDCP38p4GoobsaTCHN"));
        Assert.assertArrayEquals(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), conversions.addressToKey("id34Qf4G3b13cqNkJZM1sdexmMLVjf8dRgExLRhXmhsw1SQSzthdm"));
        Assert.assertArrayEquals(Encoding.HEX.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), conversions.addressToKey("id44izMDWYZoudRMjiYQVcGakaovDCdkhwr8Tf22QbhbD5D934waE"));
    }

    @Test
    public void testEntryCreditAddressToPublic() {
        String publicAddress = conversions.addressToPublicAddress(EC_SECRET_ADDRESS);

        AddressType.ENTRY_CREDIT_PUBLIC.assertValid(publicAddress);
        Assert.assertEquals(EC_PUBLIC_ADDRESS, publicAddress);
    }

    @Test
    public void testKeyToPublicAddress() {
        for (Map.Entry<String, String> entry : publicPrivateKeyMap.entrySet()) {
            String publicAddress = conversions.addressToPublicAddress(entry.getValue());
            Assert.assertEquals(entry.getKey(), publicAddress);
        }
    }
}
