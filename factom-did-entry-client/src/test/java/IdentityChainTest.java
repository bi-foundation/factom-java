import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.identiy.did.DIDVersion;
import org.blockchain_innovation.factom.identiy.did.IdentityFactory;
import org.blockchain_innovation.factom.identiy.did.OperationValue;
import org.blockchain_innovation.factom.identiy.did.entry.*;
import org.blockchain_innovation.factom.identiy.did.parse.RuleException;
import org.factomprotocol.identity.did.model.*;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class IdentityChainTest extends AbstractIdentityTest {
    public static final String IDSEC_ALL_ZEROS = "idsec19zBQP2RjHg8Cb8xH2XHzhsB1a6ZkB23cbS21NSyH9pDbzhnN6";
    public static final String IDPUB_ALL_ZEROS = "idpub2Cy86teq57qaxHyqLA8jHwe5JqqCvL1HGH4cKRcwSTbymTTh5n";
    public static final String IDSEC_ALL_ONES = "idsec1ARpkDoUCT9vdZuU3y2QafjAJtCsQYbE2d3JDER8Nm56CWk9ix";
    public static final String IDPUB_ALL_ONES = "idpub2op91ghJbRLrukBArtxeLJotFgXhc6E21syu3Ef8V7rCcRY5cc";

    public static final String TEST_IDENTITY_CHAINID = "6aa7d4afe4932885b5b6e93accb5f4f6c14bd1827733e05e3324ae392c0b2764";

    @Test
    public void testIdPubConversionFromKeyPair() {
        KeyPair keyPair1 = generateKeyPair();
        KeyPair keyPair2 = generateKeyPair();
        KeyPair keyPair3 = generateKeyPair();


        String idPub1 = ID_ADDRESS_KEY_CONVERSIONS.toIdPubAddress(KeyType.ED25519VERIFICATIONKEY, getPublicKey(keyPair1).getAbyte());
        String idSec1 = ID_ADDRESS_KEY_CONVERSIONS.toIdSecAddress(KeyType.ED25519VERIFICATIONKEY, getPrivateKey(keyPair1).getSeed());
        String idPub2 = ID_ADDRESS_KEY_CONVERSIONS.toIdPubAddress(KeyType.ED25519VERIFICATIONKEY, getPublicKey(keyPair2).getAbyte());
        String idSec2 = ID_ADDRESS_KEY_CONVERSIONS.toIdSecAddress(KeyType.ED25519VERIFICATIONKEY, getPrivateKey(keyPair2).getSeed());
        String idPub3 = ID_ADDRESS_KEY_CONVERSIONS.toIdPubAddress(KeyType.ED25519VERIFICATIONKEY, getPublicKey(keyPair3).getAbyte());
        String idSec3 = ID_ADDRESS_KEY_CONVERSIONS.toIdSecAddress(KeyType.ED25519VERIFICATIONKEY, getPrivateKey(keyPair3).getSeed());

        String edSecond = Encoding.BASE58.encode(getPublicKey(keyPair2).getAbyte());

        CreateIdentityRequest identityRequest = new CreateIdentityRequest().
                addTagsItem("Java identity client test").
                addTagsItem(UUID.randomUUID().toString()).
                version(1).
                addKeysItem(new FactomKey().type(KeyType.IDPUB).publicValue(idPub1)).
                addKeysItem(new FactomKey().type(KeyType.IDPUB).publicValue(idPub2));

        CreateIdentityRequestEntry identityContentEntry = new CreateIdentityRequestEntry(identityRequest);
        CommitAndRevealChainResponse idChainResponse = lowLevelIdentityClient.create(identityContentEntry, new Address(AbstractIdentityTest.EC_SECRET_ADDRESS));
        assertNotNull(idChainResponse);
        assertNotNull(idChainResponse.getCommitChainResponse());
        assertNotNull(idChainResponse.getRevealResponse());
        String chainId = idChainResponse.getRevealResponse().getChainId();

        byte[] signature = ID_ADDRESS_KEY_CONVERSIONS.signKeyReplacement(chainId, idPub2, idPub3, getPrivateKey(keyPair1));
        ReplaceKeyIdentityChainEntry replaceEntry = new ReplaceKeyIdentityChainEntry(chainId, idPub2, idPub3, signature, idPub1);
        CommitAndRevealEntryResponse idReplaceResponse = lowLevelIdentityClient.update(replaceEntry, new Address(EC_SECRET_ADDRESS));
        assertNotNull(idReplaceResponse);


    }

    @Test
    public void testEntries() throws RuleException {
        List<FactomIdentityEntry<?>> identityEntries = lowLevelIdentityClient.getAllEntriesByIdentifier("did:factom:" + TEST_IDENTITY_CHAINID, EntryValidation.IGNORE_ERROR);
        assertNotNull(identityEntries);
        assertTrue(identityEntries.size() > 1);
        FactomIdentityEntry<?> firstEntry = identityEntries.get(0);

        assertEquals(DIDVersion.FACTOM_IDENTITY_CHAIN, firstEntry.getDidVersion());
        assertEquals(OperationValue.IDENTITY_CHAIN_CREATION, firstEntry.getOperationValue());
        assertNotNull(firstEntry.getExternalIds());
        assertEquals(3, firstEntry.getExternalIds().size());
        CreateIdentityContentEntry createIdentityContentEntry = (CreateIdentityContentEntry) firstEntry;
        assertNotNull(createIdentityContentEntry.getNonce());
        assertEquals(1, createIdentityContentEntry.getAdditionalTags().size());
        assertTrue(createIdentityContentEntry.getBlockInfo().isPresent());


        FactomIdentityEntry<?> secondEntry = identityEntries.get(1);
        assertEquals(DIDVersion.FACTOM_IDENTITY_CHAIN, secondEntry.getDidVersion());
        assertEquals(OperationValue.IDENTITY_CHAIN_REPLACE_KEY, secondEntry.getOperationValue());
        assertNotNull(secondEntry.getExternalIds());
        assertEquals(5, secondEntry.getExternalIds().size());
        assertTrue(secondEntry.getBlockInfo().isPresent());

        List<FactomIdentityEntry<?>> allEntries = lowLevelIdentityClient.getAllEntriesByIdentifier("did:factom:" + firstEntry.getChainId(), EntryValidation.THROW_ERROR);
        assertNotNull(allEntries);
        assertTrue(allEntries.size() > 1);
        IdentityFactory identityFactory = new IdentityFactory();
        IdentityResponse identityResponse = identityFactory.toIdentity("did:factom:" + firstEntry.getChainId(), allEntries);
        assertNotNull(identityResponse);

    }

    @Test
    public void createIdentity() {
        IdentityEntry identityEntry = new IdentityEntry();
        identityEntry.setVersion(1);
        identityEntry.addKeysItem("idpub2WVoZkrsxpRravfti99Mo9zK9jt8eYd3JLdgQ9hiJ69oFN5UHb");
        CreateIdentityContentEntry identityContentEntry =
                new CreateIdentityContentEntry(
                        identityEntry,
                        "Off-Blocks",
                        "Identity",
                        "3d5fa912acb2231ad3abc2a97b62dfcea7828b156bcc56736ea8535d0d628dd1");
        lowLevelIdentityClient.create(identityContentEntry, new Address(EC_SECRET_ADDRESS));
    }

}
