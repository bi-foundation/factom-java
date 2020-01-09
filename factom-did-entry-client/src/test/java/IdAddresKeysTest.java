import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.identiy.did.IdAddressKeyOps;
import org.factomprotocol.identity.did.model.KeyType;
import org.junit.jupiter.api.Test;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.*;

public class IdAddresKeysTest extends AbstractIdentityTest {
    public static final String IDSEC_ALL_ZEROS = "idsec19zBQP2RjHg8Cb8xH2XHzhsB1a6ZkB23cbS21NSyH9pDbzhnN6";
    public static final String IDPUB_ALL_ZEROS = "idpub2Cy86teq57qaxHyqLA8jHwe5JqqCvL1HGH4cKRcwSTbymTTh5n";
    public static final String IDSEC_ALL_ONES = "idsec1ARpkDoUCT9vdZuU3y2QafjAJtCsQYbE2d3JDER8Nm56CWk9ix";
    public static final String IDPUB_ALL_ONES = "idpub2op91ghJbRLrukBArtxeLJotFgXhc6E21syu3Ef8V7rCcRY5cc";

    @Test
    public void testIdPubConversionFromKeyPair() throws InvalidAlgorithmParameterException {
        KeyPairGenerator generator = getKeyPairGenerator();

        KeyPair keyPair = generator.generateKeyPair();
        EdDSAPrivateKey privateKey = (EdDSAPrivateKey) keyPair.getPrivate();
        EdDSAPublicKey publicKey = (EdDSAPublicKey) keyPair.getPublic();

        IdAddressKeyOps idAddressKeyOps = new IdAddressKeyOps();

        String idSec = idAddressKeyOps.toIdSecAddress(KeyType.ED25519VERIFICATIONKEY, privateKey.getSeed());
        assertNotNull(idSec);
        assertEquals(idSec, idAddressKeyOps.toIdSecAddress(KeyType.IDPUB, idSec, Encoding.BASE58));
        assertTrue(AddressType.isValidAddress(idSec));
        assertEquals(AddressType.IDENTITY_IDSEC, AddressType.getType(idSec));

        String idPub = idAddressKeyOps.toIdPubAddress(KeyType.ED25519VERIFICATIONKEY, publicKey.getA().toByteArray());
        assertNotNull(idPub);
        assertEquals(idPub, idAddressKeyOps.toIdPubAddress(KeyType.IDPUB, idPub, Encoding.BASE58));
        assertTrue(AddressType.isValidAddress(idPub));
        assertEquals(AddressType.IDENTITY_IDPUB, AddressType.getType(idPub));


        String idPubFromPrivKey = idAddressKeyOps.toIdPubAddress(KeyType.ED25519VERIFICATIONKEY, privateKey.getA().toByteArray());
        assertEquals(idPub, idPubFromPrivKey);

        byte[] publicKeyBytes = idAddressKeyOps.toEd25519KeyBytes(idPub);
        assertArrayEquals(publicKey.getAbyte(), publicKeyBytes);

        byte[] seedBytes = idAddressKeyOps.toEd25519KeyBytes(idSec);
        assertArrayEquals(privateKey.getSeed(), seedBytes);

        assertEquals(publicKey, idAddressKeyOps.toEd25519Key(idPub));
        assertEquals(privateKey, idAddressKeyOps.toEd25519Key(idSec));
    }

    @Test
    public void testAllZeros() {
        IdAddressKeyOps idAddressKeyOps = new IdAddressKeyOps();
        byte[] allZerosPub = idAddressKeyOps.toEd25519KeyBytes(IDPUB_ALL_ZEROS);
        byte[] allZerosSec = idAddressKeyOps.toEd25519KeyBytes(IDSEC_ALL_ZEROS);
        for (byte zero : allZerosSec) {
            assertEquals(0, zero);
        }

        EdDSAPrivateKey allZerosPrivateKey = idAddressKeyOps.toEd25519PrivateKey(IDSEC_ALL_ZEROS);
        EdDSAPublicKey allZerosPublicKey = (EdDSAPublicKey) idAddressKeyOps.toEd25519Key(IDPUB_ALL_ZEROS);
        assertArrayEquals(allZerosSec, allZerosPrivateKey.getSeed());
        assertArrayEquals(allZerosPub, allZerosPublicKey.getAbyte());

        // check public key from private private address (idsec)
        assertEquals(allZerosPublicKey, idAddressKeyOps.toEd25519PublicKey(IDPUB_ALL_ZEROS));
    }

    @Test
    public void testAllOnes() {
        IdAddressKeyOps idAddressKeyOps = new IdAddressKeyOps();
        byte[] allOnesPub = idAddressKeyOps.toEd25519KeyBytes(IDPUB_ALL_ONES);
        byte[] allOnesSec = idAddressKeyOps.toEd25519KeyBytes(IDSEC_ALL_ONES);
        for (byte one : allOnesSec) {
            assertEquals(1, one);
        }

        EdDSAPrivateKey allOnesPrivateKey = idAddressKeyOps.toEd25519PrivateKey(IDSEC_ALL_ONES);
        EdDSAPublicKey allOnesPublicKey = (EdDSAPublicKey) idAddressKeyOps.toEd25519Key(IDPUB_ALL_ONES);
        assertArrayEquals(allOnesSec, allOnesPrivateKey.getSeed());
        assertArrayEquals(allOnesPub, allOnesPublicKey.getAbyte());

        // check public key from private private address (idsec)
        assertEquals(allOnesPublicKey, idAddressKeyOps.toEd25519PublicKey(IDSEC_ALL_ONES));
    }

    @Test
    public void testImpossiblePrivateKeyFromPubAddress() {
        assertThrows(
                FactomRuntimeException.AssertionException.class,
                () -> new IdAddressKeyOps().toEd25519PrivateKey(IDPUB_ALL_ONES)
        );
    }


    @Test
    public void testUnsupportedKeyType() {
        assertThrows(
                FactomRuntimeException.AssertionException.class,
                () -> new IdAddressKeyOps().toIdPubAddress(KeyType.ECDSASECP256K1VERIFICATIONKEY, IDPUB_ALL_ONES, Encoding.BASE58)
        );
    }

    @Test
    public void invalidAddresses() {
        assertThrows(
                FactomRuntimeException.AssertionException.class,
                () -> new IdAddressKeyOps().assertValidAddress(IDPUB_ALL_ONES + "1")
        );

        assertThrows(
                FactomRuntimeException.AssertionException.class,
                () -> new IdAddressKeyOps().assertValidAddress(IDPUB_ALL_ONES.replaceFirst("id", ""))
        );
    }


}
