package org.blockchain_innovation.factom.client.jee.cdi;

import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.FactomdClient;
import org.blockchain_innovation.factom.client.api.WalletdClient;
import org.blockchain_innovation.factom.client.api.ops.Base58;
import org.blockchain_innovation.factom.client.api.ops.ByteOperations;
import org.blockchain_innovation.factom.client.api.ops.EncodeOperations;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;
import org.blockchain_innovation.factom.client.api.ops.StringUtils;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Provider;

@RunWith(Arquillian.class)
public class CDITest extends AbstractCDITest {

    @Inject
    private Base58 base58;

    @Inject
    private ByteOperations byteOperations;

    @Inject
    private EncodeOperations encodeOperations;

    @Inject
    private EntryOperations entryOperations;

    @Inject
    private StringUtils stringUtils;

    @Inject
    private AddressKeyConversions addressKeyConversions;

    @Inject
    @ManagedClient
    private Provider<FactomdClient> factomdClientProvider;

    @Inject
    @ManagedClient
    private Provider<WalletdClient> walletdClientProvider;


    @Test
    public void testCdi() {
        Assert.assertNotNull(base58);
        Assert.assertNotNull(encodeOperations);
        Assert.assertNotNull(byteOperations);
        Assert.assertNotNull(entryOperations);
        Assert.assertNotNull(stringUtils);
        Assert.assertNotNull(addressKeyConversions);
        Assert.assertNotNull(factomdClientProvider.get());
        Assert.assertNotNull(walletdClientProvider.get());

        Assert.assertNotNull(factomdClientProvider.get().heights().join().getRpcResponse().getResult());
    }


}
