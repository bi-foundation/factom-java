package org.blockchain_innovation.factom.client.spring;

import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.ops.Base58;
import org.blockchain_innovation.factom.client.api.ops.ByteOperations;
import org.blockchain_innovation.factom.client.api.ops.EncodeOperations;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;
import org.blockchain_innovation.factom.client.api.ops.StringUtils;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@ContextConfiguration(classes = FactomConfiguration.class)
public class SpringTest {

    public static final String TESTNET = "testnet";
    @Autowired
    private Base58 base58;

    @Autowired
    private ByteOperations byteOperations;

    @Autowired
    private EncodeOperations encodeOperations;

    @Autowired
    private EntryOperations entryOperations;

    @Autowired
    private StringUtils stringUtils;

    @Autowired
    private AddressKeyConversions addressKeyConversions;

    @Autowired
    private SpringNetworks networks;
  /*  @Autowired
    private FactomdClient factomdClient;

    @Autowired
    private WalletdClient walletdClient;
*/
    @Test
    public void testAutowirings() {
        Assert.assertNotNull(base58);
        Assert.assertNotNull(byteOperations);
        Assert.assertNotNull(encodeOperations);
        Assert.assertNotNull(encodeOperations);
        Assert.assertNotNull(stringUtils);
        Assert.assertNotNull(addressKeyConversions);
        Assert.assertNotNull(networks.factomd(Optional.of(TESTNET)));
        Assert.assertNotNull(networks.walletd(Optional.of(TESTNET)));
    }

    @Test
    public void testConfig() {
        RpcSettings settings = networks.factomd(Optional.of(TESTNET)).lowLevelClient().getSettings();
        Assert.assertEquals("http://136.144.204.97:8088/v2", settings.getServer().getURL().toString());
        Assert.assertEquals(60, settings.getServer().getTimeout());

        settings = networks.walletd(Optional.of(TESTNET)).lowLevelClient().getSettings();
        Assert.assertEquals("http://136.144.204.97:8089/v2", settings.getServer().getURL().toString());
        Assert.assertEquals(25, settings.getServer().getTimeout());

    }

}
