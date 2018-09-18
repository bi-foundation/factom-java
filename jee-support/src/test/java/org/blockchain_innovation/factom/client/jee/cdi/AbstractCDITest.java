package org.blockchain_innovation.factom.client.jee.cdi;

import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class AbstractCDITest {
    protected final static Address EC_PUBLIC_ADDRESS = new Address(System.getProperty("FACTOM_CLIENT_TEST_EC_PUBLIC_ADDRESS", "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv"));


    @Inject
    protected ManagedClientProducers managedClientProducers;


    @Before
    public void setup() throws IOException {
        managedClientProducers.addSettings(new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, getProperties()));
        managedClientProducers.addSettings(new RpcSettingsImpl(RpcSettings.SubSystem.WALLETD, getProperties()));
        managedClientProducers.enableCommitAndRevealEvents(true);
    }

    protected Properties getProperties() throws IOException {
        Properties properties = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("settings.properties");
        properties.load(is);
        is.close();
        return properties;
    }

    @Deployment
    public static JavaArchive createJavaTestArchive() {

        return ShrinkWrap.create(JavaArchive.class)
                .addPackage("org.blockchain_innovation.factom.client.jee.cdi")
                .addPackage("org.blockchain_innovation.factom.client.api")
                .addPackage("org.blockchain_innovation.factom.client.api.ops")
                .addPackage("org.blockchain_innovation.factom.client.impl")
//                .addPackage("org.blockchain_innovation.factom.client.impl.json.jee")
                .addPackage("org.blockchain_innovation.factom.client.impl.json.gson") //fixme
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

}
