package org.blockchain_innovation.accumulate.factombridge;

import io.accumulatenetwork.sdk.generated.protocol.SignatureType;
import org.blockchain_innovation.accumulate.factombridge.model.LiteAccount;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.impl.EntryApiImpl;
import org.blockchain_innovation.factom.client.impl.FactomdClientImpl;
import org.blockchain_innovation.factom.client.impl.OfflineWalletdClientImpl;
import org.blockchain_innovation.factom.client.impl.WalletdClientImpl;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.Random;

public class AbstractClientTest {

    static LiteAccount liteAccount;

    protected String rootADI = "acc://factom-java-test-principal-" + new Random().nextInt() + ".acme"; // TODO make Url

    protected final FactomdClientImpl factomdClient = new FactomdClientImpl();
    protected final WalletdClientImpl walletdClient = new WalletdClientImpl();
    protected final EntryApiImpl entryClient = new EntryApiImpl();
    protected final EntryApiImpl offlineEntryClient = new EntryApiImpl();
    protected final OfflineWalletdClientImpl offlineWalletdClient = new OfflineWalletdClientImpl();

    @BeforeClass
    public static void init() {
        liteAccount = LiteAccount.generate(SignatureType.ED25519);
    }


    protected void assertValidResponse(FactomResponse<?> factomResponse) {
        Assert.assertNotNull(factomResponse);
        Assert.assertNotNull(factomResponse.getRpcResponse());
        Assert.assertEquals(200, factomResponse.getHTTPResponseCode());
        Assert.assertNull(factomResponse.getRpcErrorResponse());
        Assert.assertFalse(factomResponse.hasErrors());
    }


}
