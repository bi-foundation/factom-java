package org.blockchain_innovation.accumulate.factombridge;

import io.accumulatenetwork.sdk.generated.protocol.SignatureType;
import org.blockchain_innovation.accumulate.factombridge.impl.FactomdAccumulateClientImpl;
import org.blockchain_innovation.accumulate.factombridge.model.LiteAccount;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.EntryApiImpl;
import org.blockchain_innovation.factom.client.impl.OfflineWalletdClientImpl;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public class AbstractClientTest {

    static LiteAccount liteAccount;

    protected String rootADI = "acc://factom-java-test-principal-" + new Random().nextInt() + ".acme"; // TODO make Url

    protected final FactomdAccumulateClientImpl factomdClient = new FactomdAccumulateClientImpl();
    protected final EntryApiImpl entryClient = new EntryApiImpl();
    protected final EntryApiImpl offlineEntryClient = new EntryApiImpl();
    protected final OfflineWalletdClientImpl offlineWalletdClient = new OfflineWalletdClientImpl();


    @BeforeClass
    public static void init() {
        liteAccount = LiteAccount.generate(SignatureType.ED25519);
    }

    @Before
    public void setup() throws IOException {

        factomdClient.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, getProperties()));
        entryClient.setFactomdClient(factomdClient);

        offlineEntryClient.setFactomdClient(factomdClient);
        offlineEntryClient.setWalletdClient(offlineWalletdClient);
    }


    protected void assertValidResponse(FactomResponse<?> factomResponse) {
        Assert.assertNotNull(factomResponse);
        Assert.assertNotNull(factomResponse.getRpcResponse());
        Assert.assertEquals(200, factomResponse.getHTTPResponseCode());
        Assert.assertNull(factomResponse.getRpcErrorResponse());
        Assert.assertFalse(factomResponse.hasErrors());
    }

    protected Properties getProperties() throws IOException {
        Properties properties = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("settings.properties");
        properties.load(is);
        is.close();
        return properties;
    }
}
