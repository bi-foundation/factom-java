import did.DIDDocument;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.identiy.did.DIDVersion;
import org.blockchain_innovation.factom.identiy.did.entry.CreateFactomDIDEntry;
import org.blockchain_innovation.factom.identiy.did.entry.EntryValidation;
import org.blockchain_innovation.factom.identiy.did.entry.FactomIdentityEntry;
import org.blockchain_innovation.factom.identiy.did.parse.RuleException;
import org.factomprotocol.identity.did.model.FactomDidContent;
import org.factomprotocol.identity.did.model.IdentityResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DIDTest extends AbstractIdentityTest {


    public static final String TEST_IDENTITY_CHAINID = "6aa7d4afe4932885b5b6e93accb5f4f6c14bd1827733e05e3324ae392c0b2764";


    @Test
    public void test() throws IOException, RuleException {
        String nonce = "test-" + System.currentTimeMillis();
        String chainId = new CreateFactomDIDEntry(DIDVersion.FACTOM_V1_JSON, null, nonce).getChainId();
        assertNotNull(chainId);

        FactomDidContent factomDidContent = GSON.fromJson("{\n" +
                " \"didMethodVersion\": \"0.2.0\",\n" +
                " \"managementKey\": [\n" +
                "   {\n" +
                "     \"id\": \"did:factom:" + chainId + "#management-0\",\n" +
                "     \"type\": \"Ed25519VerificationKey\",\n" +
                "     \"controller\": \"did:factom:" + chainId + "\",\n" +
                "     \"publicKeyBase58\": \"H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV\",\n" +
                "     \"priority\": 0\n" +
                "   }\n" +
                " ],\n" +
                " \"didKey\": [\n" +
                "   {\n" +
                "     \"id\": \"did:factom:" + chainId + "#public-0\",\n" +
                "     \"type\": \"Ed25519VerificationKey\",\n" +
                "     \"controller\": \"did:factom:" + chainId + "\",\n" +
                "     \"publicKeyBase58\": \"3uVAjZpfMv6gmMNam3uVAjZpfkcJCwDwnZn6MNam3uVA\",\n" +
                "     \"purpose\": [\"publicKey\", \"authentication\"],\n" +
                "     \"priorityRequirement\": 1\n" +
                "   },\n" +
                "   {\n" +
                "     \"id\": \"did:factom:" + chainId + "#authentication-0\",\n" +
                "     \"type\": \"ECDSASecp256k1VerificationKey\",\n" +
                "     \"controller\": \"did:factom:" + chainId + "\",\n" +
                "     \"publicKeyBase58\": \"H3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV\",\n" +
                "     \"purpose\": [\"authentication\"],\n" +
                "     \"priorityRequirement\": 2,\n" +
                "     \"bip44\": \"m / 44' / 0' / 0' / 0 / 0\"\n" +
                "   }\n" +
                " ],\n" +
                " \"service\": [\n" +
                "   {\n" +
                "     \"id\": \"did:factom:" + chainId + "#cr\",\n" +
                "     \"type\": \"CredentialRepositoryService\",\n" +
                "     \"serviceEndpoint\": \"https://repository.example.com/service/8377464\",\n" +
                "     \"priorityRequirement\": 1\n" +
                "   }\n" +
                " ]\n" +
                "}", FactomDidContent.class);


        CreateFactomDIDEntry createEntry = new CreateFactomDIDEntry(DIDVersion.FACTOM_V1_JSON, factomDidContent, nonce);

        String didURL = "did:factom:" + chainId;
        String targetId = DIDVersion.FACTOM_V1_JSON.getMethodSpecificId(didURL);

        String keyId = "did:factom:" + chainId + "#keys-1";
//        DIDDocument didDocument = DIDDocument.build(didReference, null, null, null);


        CommitAndRevealChainResponse commitAndRevealChainResponse = lowLevelIdentityClient.create(createEntry, new Address(EC_SECRET_ADDRESS));

        System.err.println(commitAndRevealChainResponse.getRevealResponse());
        List<FactomIdentityEntry<?>> identityEntries = lowLevelIdentityClient.getAllEntriesByIdentifier("did:factom:e21f9aef1ed841a7d7d634c9dd0dc204c694b51ad7152a44c326113326283eeb", EntryValidation.THROW_ERROR);
        assertNotNull(identityEntries);
        FactomIdentityEntry<?> identityEntry = identityEntries.get(0);
        assertNotNull(identityEntry);
        //        // todo This is not a proper update for now
//        CommitAndRevealEntryResponse updateEntryResponse = lowLevelDidClient.update(didDocument, nonce, keyId, new Address(EC_SECRET_ADDRESS));
//
//        // todo This is not a proper deactivate for now
//        CommitAndRevealEntryResponse deactivateEntryResponse = lowLevelDidClient.deactivate(didDocument, keyId, new Address(EC_SECRET_ADDRESS));

    }

    @Test
    public void getDidDocumentFromIdentityChain() throws RuleException {

        List<FactomIdentityEntry<?>> allEntries = lowLevelIdentityClient.getAllEntriesByIdentifier("did:factom:" + TEST_IDENTITY_CHAINID, EntryValidation.THROW_ERROR);
        assertNotNull(allEntries);

        IdentityResponse identityResponse = IDENTITY_FACTORY.toIdentity("did:factom:" + TEST_IDENTITY_CHAINID, allEntries);
        DIDDocument didDocument = IDENTITY_FACTORY.toDid("did:factom:" + TEST_IDENTITY_CHAINID, identityResponse);
        assertNotNull(didDocument);
        System.err.println(didDocument.toString());
    }


}
