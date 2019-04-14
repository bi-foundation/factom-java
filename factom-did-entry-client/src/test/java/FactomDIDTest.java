import did.DIDDocument;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.EntryApiImpl;
import org.blockchain_innovation.factom.client.impl.FactomdClientImpl;
import org.blockchain_innovation.factom.client.impl.OfflineWalletdClientImpl;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.blockchain_innovation.factom.identiy.did.entry.DIDEntryClient;
import org.blockchain_innovation.factom.identiy.did.entry.FactomDID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FactomDIDTest {

    protected final EntryApiImpl offlineEntryClient = new EntryApiImpl();
    protected final OfflineWalletdClientImpl offlineWalletdClient = new OfflineWalletdClientImpl();
    protected final FactomdClientImpl factomdClient = new FactomdClientImpl();
    protected final DIDEntryClient didEntryClient = new DIDEntryClient();


    protected static final String EC_SECRET_ADDRESS = System.getProperty("FACTOM_CLIENT_TEST_EC_SECRET_ADDRESS", "Es3Y6U6H1Pfg4wYag8VMtRZEGuEJnfkJ2ZuSyCVcQKweB6y4WvGH");

    @Test
    public void test() throws IOException {
        byte[] nonce = ("test" + System.currentTimeMillis()).getBytes();
        String chainId = FactomDID.FCTR_V1.determineChainId(nonce);
        String didReference = "did:fctr:" + chainId;
        String targetId = FactomDID.FCTR_V1.getTargetId(didReference);
        DIDDocument didDocument = DIDDocument.fromJson("{\n" +
                "  \"@context\": \"https://w3id.org/did/v1\",\n" +
                "  \"id\": \"did:fctr:"+ chainId +"\",\n" +
                "  \"authentication\": [{\n" +
                "    \"id\": \"" + chainId + "#keys-1\",\n" +
                "    \"type\": \"RsaVerificationKey2018\",\n" +
                "    \"controller\": \"did:example:123456789abcdefghi\",\n" +
                "    \"publicKeyPem\": \"-----BEGIN PUBLIC KEY...END PUBLIC KEY-----\\r\\n\"\n" +
                "  }],\n" +
                "  \"service\": [{\n" +
                "    \"id\": \""+ chainId + "#service123\",\n" +
                "    \"type\": \"ExampleService\",\n" +
                "    \"serviceEndpoint\": \"https://example.com/endpoint/8377464\"\n" +
                "  }]\n" +
                "}");

//        DIDDocument didDocument = DIDDocument.build(didReference, null, null, null);
        CommitAndRevealChainResponse commitAndRevealChainResponse = didEntryClient.create(didDocument, nonce, new Address(EC_SECRET_ADDRESS));

        Assert.assertNotNull(chainId);
    }


    @Before
    public void setup() throws IOException {

        factomdClient.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, getProperties()));
        offlineEntryClient.setFactomdClient(factomdClient);
        offlineEntryClient.setWalletdClient(offlineWalletdClient);
        didEntryClient.setEntryApi(offlineEntryClient);
    }

    protected Properties getProperties() throws IOException {
        Properties properties = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("settings.properties");
        properties.load(is);
        is.close();
        return properties;
    }
}
