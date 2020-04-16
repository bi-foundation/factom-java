import com.google.gson.Gson;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.EntryApiImpl;
import org.blockchain_innovation.factom.client.impl.FactomdClientImpl;
import org.blockchain_innovation.factom.client.impl.OfflineWalletdClientImpl;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.blockchain_innovation.factom.identiy.did.IdAddressKeyOps;
import org.blockchain_innovation.factom.identiy.did.IdentityFactory;
import org.blockchain_innovation.factom.identiy.did.LowLevelIdentityClient;
import org.factomprotocol.identity.did.invoker.JSON;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.Properties;

public abstract class AbstractIdentityTest {
    protected static final IdentityFactory IDENTITY_FACTORY = new IdentityFactory();
    public static final IdAddressKeyOps ID_ADDRESS_KEY_CONVERSIONS = new IdAddressKeyOps();
    protected static final Gson GSON = JSON.createGson().create();
    protected final EntryApiImpl offlineEntryClient = new EntryApiImpl();
    protected final OfflineWalletdClientImpl offlineWalletdClient = new OfflineWalletdClientImpl();
    protected final FactomdClientImpl factomdClient = new FactomdClientImpl();
    protected final LowLevelIdentityClient lowLevelIdentityClient = new LowLevelIdentityClient();

    protected static final String EC_SECRET_ADDRESS = System.getProperty("FACTOM_CLIENT_TEST_EC_SECRET_ADDRESS", "Es3Y6U6H1Pfg4wYag8VMtRZEGuEJnfkJ2ZuSyCVcQKweB6y4WvGH");

    @BeforeEach
    public void setup() throws IOException {

        factomdClient.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, getProperties()));
        offlineEntryClient.setFactomdClient(factomdClient);
        offlineEntryClient.setWalletdClient(offlineWalletdClient);
        lowLevelIdentityClient.setEntryApi(offlineEntryClient);
    }

    protected Properties getProperties() throws IOException {
        Properties properties = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("settings.properties");
        properties.load(is);
        is.close();
        return properties;
    }

    protected KeyPairGenerator getKeyPairGenerator() {
        EdDSANamedCurveSpec curveSpec = EdDSANamedCurveTable.ED_25519_CURVE_SPEC;
        KeyPairGenerator generator = new KeyPairGenerator();
        try {
            generator.initialize(curveSpec, new SecureRandom());
        } catch (InvalidAlgorithmParameterException e) {
            throw new FactomRuntimeException.AssertionException(e);
        }
        return generator;
    }

    protected KeyPair generateKeyPair() {
        return getKeyPairGenerator().generateKeyPair();
    }

    protected EdDSAPrivateKey getPrivateKey(KeyPair keyPair) {
        return (EdDSAPrivateKey) keyPair.getPrivate();
    }


    protected EdDSAPublicKey getPublicKey(KeyPair keyPair) {
        return (EdDSAPublicKey) keyPair.getPublic();
    }
}
