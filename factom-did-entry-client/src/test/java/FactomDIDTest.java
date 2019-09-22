import com.google.gson.Gson;
import did.DIDDocument;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryResponse;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.EntryApiImpl;
import org.blockchain_innovation.factom.client.impl.FactomdClientImpl;
import org.blockchain_innovation.factom.client.impl.OfflineWalletdClientImpl;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.blockchain_innovation.factom.identiy.did.entry.CreateFactomDIDEntry;
import org.blockchain_innovation.factom.identiy.did.entry.LowLevelDIDClient;
import org.blockchain_innovation.factom.identiy.did.entry.DIDVersion;
import org.factom_protocol.identifiers.did.invoker.JSON;
import org.factom_protocol.identifiers.did.model.FactomDidContent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class FactomDIDTest {
    protected static final Gson GSON = JSON.createGson().create();
    protected final EntryApiImpl offlineEntryClient = new EntryApiImpl();
    protected final OfflineWalletdClientImpl offlineWalletdClient = new OfflineWalletdClientImpl();
    protected final FactomdClientImpl factomdClient = new FactomdClientImpl();
    protected final LowLevelDIDClient lowLevelDidClient = new LowLevelDIDClient();


    protected static final String EC_SECRET_ADDRESS = System.getProperty("FACTOM_CLIENT_TEST_EC_SECRET_ADDRESS", "Es3Y6U6H1Pfg4wYag8VMtRZEGuEJnfkJ2ZuSyCVcQKweB6y4WvGH");

    @Test
    public void test() throws IOException {
        String nonce = "test-" + System.currentTimeMillis();
        String chainId = new CreateFactomDIDEntry(DIDVersion.FACTOM_V1_JSON, null, nonce).getChainId();
        Assert.assertNotNull(chainId);

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


        CommitAndRevealChainResponse commitAndRevealChainResponse = lowLevelDidClient.create(createEntry, new Address(EC_SECRET_ADDRESS));

        System.err.println(commitAndRevealChainResponse.getRevealResponse());
        List<EntryResponse> entrieResponses = lowLevelDidClient.getAllEntriesByIdentifier("did:factom:e21f9aef1ed841a7d7d634c9dd0dc204c694b51ad7152a44c326113326283eeb");
        Assert.assertNotNull(entrieResponses);
        EntryResponse entryResponse = entrieResponses.get(0);
        Entry entry = new Entry.Builder().setChainId(entryResponse.getChainId()).setExternalIds(entryResponse.getExtIds()).setContent(entryResponse.getContent()).build();
        CreateFactomDIDEntry resolvedFactomDidEntry = new CreateFactomDIDEntry(entry);
        Assert.assertEquals(entry.getChainId(), resolvedFactomDidEntry.toEntry().getChainId());

        //        // todo This is not a proper update for now
//        CommitAndRevealEntryResponse updateEntryResponse = lowLevelDidClient.update(didDocument, nonce, keyId, new Address(EC_SECRET_ADDRESS));
//
//        // todo This is not a proper deactivate for now
//        CommitAndRevealEntryResponse deactivateEntryResponse = lowLevelDidClient.deactivate(didDocument, keyId, new Address(EC_SECRET_ADDRESS));

    }


    @Before
    public void setup() throws IOException {

        factomdClient.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, getProperties()));
        offlineEntryClient.setFactomdClient(factomdClient);
        offlineEntryClient.setWalletdClient(offlineWalletdClient);
        lowLevelDidClient.setEntryApi(offlineEntryClient);
    }

    protected Properties getProperties() throws IOException {
        Properties properties = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("settings.properties");
        properties.load(is);
        is.close();
        return properties;
    }
}
