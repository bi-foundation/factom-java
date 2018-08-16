package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.data.FactomException;
import org.blockchain_innovation.factom.client.data.model.Address;
import org.blockchain_innovation.factom.client.data.model.Chain;
import org.blockchain_innovation.factom.client.data.model.Entry;
import org.blockchain_innovation.factom.client.data.model.Range;
import org.blockchain_innovation.factom.client.data.model.response.walletd.AddressResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.AddressesResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.BlockHeightTransactionsResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.ExecutedTransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.GetHeightResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.PropertiesResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.TmpTransactions;
import org.blockchain_innovation.factom.client.data.model.response.walletd.TransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.TransactionsResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.WalletBackupResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class WalletdClientTest {

    private final WalletdClient client = new WalletdClient();

    @Before
    public void setup() throws MalformedURLException {
        client.setUrl(new URL("http://136.144.204.97:8089/v2"));
    }

    @Test
    public void addEntryCreditOutput() throws FactomException.ClientException {
        String txName = "TX_NAME";
        String address = "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv";
        int amount = 1000;
        // FactomResponse<TransactionResponse> response = client.addEntryCreditOutput(txName, address, amount);
        // assertValidResponse(response);
    }

    @Test
    public void addFee() throws FactomException.ClientException {
        String txName = "TX_NAME";
        String address = "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv";
        FactomResponse<ExecutedTransactionResponse> response = client.addFee(txName, address);
        assertValidResponse(response);
    }

    @Test
    public void addInput() throws FactomException.ClientException {
        // client.addInput(txName, address, amount);
    }

    @Test
    public void addOutput() throws FactomException.ClientException {
        // client.addOutput(txName, address, amount);
    }

    @Test
    public void address() throws FactomException.ClientException {
        String address = "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv";
        FactomResponse<AddressResponse> response = client.address(address);
        assertValidResponse(response);
        AddressResponse addressResponse = response.getResult();
        Assert.assertEquals("EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv", addressResponse.getPublicAddress());
        Assert.assertNotNull(addressResponse.getSecret());
    }

    @Test
    public void allAddresses() throws FactomException.ClientException {
        FactomResponse<AddressesResponse> response = client.allAddresses();
        assertValidResponse(response);
    }

    @Test
    public void composeChain() throws FactomException.ClientException {
        List<String> externalIds = Arrays.asList(
                "61626364",
                "31323334");
        Chain.Entry firstEntry = new Chain.Entry();
        firstEntry.setExternalIds(externalIds);
        firstEntry.setContent("3132333461626364");
        Chain chain = new Chain();
        chain.setFirstEntry(firstEntry);

        FactomResponse<ComposeResponse> response = client.composeChain(chain, "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv");
        assertValidResponse(response);

        ComposeResponse composeResponse = response.getResult();
        Assert.assertNotNull(composeResponse.getCommit());
        Assert.assertNotNull(composeResponse.getCommit().getId());
        Assert.assertNotNull(composeResponse.getCommit().getParams());
        Assert.assertNotNull(composeResponse.getCommit().getParams().getMessage());
        Assert.assertNotNull(composeResponse.getReveal());
        Assert.assertNotNull(composeResponse.getReveal().getId());
        Assert.assertNotNull(composeResponse.getReveal().getParams());
        Assert.assertNotNull(composeResponse.getReveal().getParams().getEntry());
    }

    @Test
    public void composeEntry() throws FactomException.ClientException {
        List<String> externalIds = Arrays.asList("cd90", "90cd");
        Entry entry = new Entry();
        entry.setChainId("041ffaf76eb6370c94701c7aa60cc8c114fc68ede00d28389bc31850ef732c4f");
        entry.setContent("abcdef");
        entry.setExternalIds(externalIds);

        FactomResponse<ComposeResponse> response = client.composeEntry(entry, "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv");
        assertValidResponse(response);

        ComposeResponse composeResponse = response.getResult();
        Assert.assertNotNull(composeResponse.getCommit());
        Assert.assertNotNull(composeResponse.getCommit().getId());
        Assert.assertNotNull(composeResponse.getCommit().getParams());
        Assert.assertNotNull(composeResponse.getCommit().getParams().getMessage());
        Assert.assertNotNull(composeResponse.getReveal());
        Assert.assertNotNull(composeResponse.getReveal().getId());
        Assert.assertNotNull(composeResponse.getReveal().getParams());
        Assert.assertNotNull(composeResponse.getReveal().getParams().getEntry());
    }

    @Test
    public void composeTransaction() throws FactomException.ClientException {
        // client.composeTransaction("TX_NAME");
    }

    @Test
    public void deleteTransaction() throws FactomException.ClientException {
        // client.deleteTransaction("TX_NAME");
    }

    @Test
    public void generateEntryCreditAddress() throws FactomException.ClientException {
        FactomResponse<AddressResponse> response = client.generateEntryCreditAddress();
        assertValidResponse(response);
    }

    @Test
    public void generateFactoidAddress() throws FactomException.ClientException {
        FactomResponse<AddressResponse> response = client.generateFactoidAddress();
        assertValidResponse(response);
    }

    @Test
    public void getHeight() throws FactomException.ClientException {
        FactomResponse<GetHeightResponse> response = client.getHeight();
        assertValidResponse(response);
    }

    // @Test
    public void importAddresses() throws FactomException.ClientException {
        String secret = "Es3tXbGBVKZDhUWzDKzQtg4rcpmmHPXAY9vxSM2JddwJSD5td3f8";

        Address address = new Address();
        List<Address> addresses = Arrays.asList(address);

        FactomResponse<AddressesResponse> response = client.importAddresses(addresses);
        assertValidResponse(response);

        AddressesResponse addressesResponse = response.getResult();
        Assert.assertNotNull(addressesResponse);
        Assert.assertNotNull(addressesResponse.getAddresses());
        Assert.assertNotNull(addressesResponse.getAddresses().get(0));
        Assert.assertNotNull(addressesResponse.getAddresses().get(0).getPublicAddress());
        Assert.assertNotNull(secret, addressesResponse.getAddresses().get(0).getSecret());
    }

    @Test
    public void importKoinify() throws FactomException.ClientException {
        // client.importKoinify(words);
    }

    @Test
    public void newTransaction() throws FactomException.ClientException {
        // client.newTransaction(txName);
    }

    @Test
    public void properties() throws FactomException.ClientException {
        FactomResponse<PropertiesResponse> response = client.properties();
        assertValidResponse(response);

        PropertiesResponse properties = response.getResult();
        Assert.assertNotNull(properties);
        Assert.assertNotNull(properties.getWalletversion());
        Assert.assertNotNull(properties.getWalletapiversion());
    }

    @Test
    public void signTransaction() throws FactomException.ClientException {
        // client.signTransaction(txName);
    }

    @Test
    public void subFee() throws FactomException.ClientException {
        // client.subFee(txName, address);
    }

    @Test
    public void tmpTransactions() throws FactomException.ClientException {
        FactomResponse<TmpTransactions> response = client.tmpTransactions();
        assertValidResponse(response);

        TmpTransactions transactions = response.getResult();
        Assert.assertNotNull(transactions);
    }

    @Test
    public void transactionsByRange() throws FactomException.ClientException {
        Range range = new Range();
        range.setStart(39861);
        range.setEnd(40861);
        FactomResponse<BlockHeightTransactionsResponse> response = client.transactionsByRange(range);
        assertValidResponse(response);
    }

    @Test
    public void transactionsByTransaction() throws FactomException.ClientException {
        String transactionId = "89518f8208278a6cebf0b3c82167857049314ccbc7c04c16876957434720cba0";
        FactomResponse<TransactionsResponse> response = client.transactionsByTransaction(transactionId);
        assertValidResponse(response);

        TransactionsResponse transactions = response.getResult();
        Assert.assertNotNull(transactions);
        Assert.assertNotNull(transactions.getTransactions());
        Assert.assertFalse(transactions.getTransactions().isEmpty());
    }

    @Test
    public void transactionsByAddress() throws FactomException.ClientException {
        String address = "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv";
        FactomResponse<BlockHeightTransactionsResponse> response = client.transactionsByAddress(address);
        assertValidResponse(response);

        BlockHeightTransactionsResponse transactions = response.getResult();
        Assert.assertNotNull(transactions);
        Assert.assertNotNull(transactions.getTransactions());
        Assert.assertFalse(transactions.getTransactions().isEmpty());
    }

    @Test
    public void walletBackup() throws FactomException.ClientException {
        FactomResponse<WalletBackupResponse> response = client.walletBackup();
        assertValidResponse(response);
        WalletBackupResponse walletBackup = response.getResult();
        Assert.assertNotNull(walletBackup.getWalletSeed());
        Assert.assertNotNull(walletBackup.getAddresses());
        Assert.assertFalse(walletBackup.getAddresses().isEmpty());

    }

    private void assertValidResponse(FactomResponse<?> factomResponse) {
        Assert.assertNotNull(factomResponse);
        Assert.assertNotNull(factomResponse.getRpcResponse());
        Assert.assertEquals(200, factomResponse.getHTTPResponseCode());
        Assert.assertNull(factomResponse.getRpcErrorResponse());
        Assert.assertFalse(factomResponse.hasErrors());
    }
}