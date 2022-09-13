package org.blockchain_innovation.accumulate.factombridge;

import io.accumulatenetwork.sdk.api.v2.AccumulateSyncApi;
import io.accumulatenetwork.sdk.api.v2.TransactionQueryResult;
import io.accumulatenetwork.sdk.api.v2.TransactionResult;
import io.accumulatenetwork.sdk.generated.apiv2.TransactionQueryResponse;
import io.accumulatenetwork.sdk.generated.apiv2.TxnQuery;
import io.accumulatenetwork.sdk.generated.errors.Status;
import io.accumulatenetwork.sdk.generated.protocol.AddCredits;
import io.accumulatenetwork.sdk.generated.protocol.AddCreditsResult;
import io.accumulatenetwork.sdk.generated.protocol.SignatureType;
import io.accumulatenetwork.sdk.generated.protocol.TransactionType;
import io.accumulatenetwork.sdk.protocol.TxID;
import io.accumulatenetwork.sdk.support.Retry;
import org.blockchain_innovation.accumulate.factombridge.impl.EntryApiImpl;
import org.blockchain_innovation.accumulate.factombridge.impl.FactomdAccumulateClientImpl;
import org.blockchain_innovation.accumulate.factombridge.model.LiteAccount;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.OfflineWalletdClientImpl;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class AbstractClientTest {

    static LiteAccount liteAccount;
    static boolean liteAccountFunded;

    protected String rootADI = "acc://factom-java-test-principal-" + new Random().nextInt() + ".acme"; // TODO make Url

    protected final FactomdAccumulateClientImpl factomdClient = new FactomdAccumulateClientImpl();
    protected final EntryApiImpl entryClient = new EntryApiImpl();
    protected final OfflineWalletdClientImpl offlineWalletdClient = new OfflineWalletdClientImpl();

    private AccumulateSyncApi accumulate;

    @BeforeClass
    public static void init() {
        liteAccount = LiteAccount.generate(SignatureType.ED25519);
    }

    @Before
    public void setup() throws IOException, URISyntaxException {

        final RpcSettingsImpl settings = new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, getProperties());
        factomdClient.setSettings(settings);
        entryClient.setFactomdClient(factomdClient);
        entryClient.setWalletdClient(offlineWalletdClient);

        accumulate = new AccumulateSyncApi(settings.getServer().getURL().toURI());
        if(!liteAccountFunded) {
            faucet();
            faucet();
            waitForAnchor();
            addCreditsToLiteAccount();
            liteAccountFunded = true;
        }
    }

    protected void faucet() {
        final TxID txId = accumulate.faucet(liteAccount.getAccount().getUrl());
        waitForTx(txId);
    }

    protected void addCreditsToLiteAccount() {
        final AddCredits addCredits = new AddCredits()
                .recipient(liteAccount.getAccount().getUrl())
                .amount(BigInteger.valueOf(2000000000L));
        final TransactionResult<AddCreditsResult> transactionResult = accumulate.addCredits(liteAccount, addCredits);
        final AddCreditsResult addCreditsResult = transactionResult.getResult();
        assertNotNull(addCreditsResult);
        assertTrue(addCreditsResult.getCredits() > 0);
        final TransactionQueryResult txQueryResult = waitForTx(transactionResult.getTxID());
        assertEquals(txQueryResult.getTxType(), TransactionType.ADD_CREDITS);
        waitForAnchor();
    }

    private TransactionQueryResult waitForTx(final TxID txId) {
        final AtomicReference<TransactionQueryResult> result = new AtomicReference<TransactionQueryResult>();
        final TxnQuery txnQuery = new TxnQuery()
                .txid(txId.getHash())
                .wait(Duration.ofMinutes(1));
        new Retry()
                .withTimeout(1, ChronoUnit.MINUTES)
                .withDelay(2, ChronoUnit.SECONDS)
                .withMessage("")
                .execute(() -> {
                    final TransactionQueryResult txQueryResult = accumulate.getTx(txnQuery);
                    assertNotNull(txQueryResult);
                    final TransactionQueryResponse queryResponse = txQueryResult.getQueryResponse();
                    if (queryResponse.getStatus().getCode() == Status.PENDING) {
                        return true;
                    }
                    if (!queryResponse.getType().equalsIgnoreCase("syntheticCreateIdentity")) { // TODO syntheticCreateIdentity returns CONFLICT?
                        assertEquals(Status.DELIVERED, queryResponse.getStatus().getCode());
                    }
                    if (queryResponse.getProduced() != null) {
                        for (TxID producedTxId : queryResponse.getProduced()) {
                            waitForTx(producedTxId);
                        }
                    }
                    result.set(txQueryResult);
                    return false;
                });
        return result.get();
    }

    protected void waitForAnchor() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }


    protected void assertValidResponse(FactomResponse<?> factomResponse) {
        assertNotNull(factomResponse);
        assertNotNull(factomResponse.getRpcResponse());
        assertEquals(200, factomResponse.getHTTPResponseCode());
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
