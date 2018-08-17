package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.data.FactomException;
import org.blockchain_innovation.factom.client.data.model.Chain;
import org.blockchain_innovation.factom.client.data.model.Entry;
import org.blockchain_innovation.factom.client.data.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.data.model.response.walletd.ComposeResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChainEntryTest extends AbstractClientTest {

    private static String chainId = "23fc40b5d301f8c40513cb1363439bc23e6c21856073abefdb1a2a2e49baba3b";
    private static String entryHash;
    private final FactomdClient factomdClient = new FactomdClient();
    private final WalletdClient walletdClient = new WalletdClient();
    private final String publicKey = "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv";

    @Before
    public void setup() throws MalformedURLException {
        factomdClient.setUrl(new URL("http://136.144.204.97:8088/v2"));
        walletdClient.setUrl(new URL("http://136.144.204.97:8089/v2"));
    }

    @Test
    public void _01_commitChain() throws FactomException.ClientException {
        // random 8 digit number to create new chain
        int value = 10000000 + new Random().nextInt(90000000);

        List<String> externalIds = Arrays.asList(
                String.valueOf(value),
                "61626364",
                "31323334");

        Chain.Entry firstEntry = new Chain.Entry();
        firstEntry.setExternalIds(externalIds);
        firstEntry.setContent("3132333461626364");

        Chain chain = new Chain();
        chain.setFirstEntry(firstEntry);

        FactomResponse<ComposeResponse> composeResponse = walletdClient.composeChain(chain, publicKey);
        assertValidResponse(composeResponse);

        ComposeResponse composeChain = composeResponse.getResult();
        Assert.assertNotNull(composeChain.getCommit());
        Assert.assertNotNull(composeChain.getCommit().getId());
        Assert.assertNotNull(composeChain.getCommit().getParams());

        String commitChainMessage = composeChain.getCommit().getParams().getMessage();
        Assert.assertNotNull(commitChainMessage);
        Assert.assertNotNull(composeChain.getReveal());
        Assert.assertNotNull(composeChain.getReveal().getId());
        Assert.assertNotNull(composeChain.getReveal().getParams());

        String revealChainEntry = composeChain.getReveal().getParams().getEntry();
        Assert.assertNotNull(revealChainEntry);

        FactomResponse<CommitChainResponse> commitChainResponse = factomdClient.commitChain(commitChainMessage);
        assertValidResponse(commitChainResponse);

        CommitChainResponse commitChain = commitChainResponse.getResult();
        Assert.assertNotNull(commitChain);
        Assert.assertEquals("Chain Commit Success", commitChain.getMessage());
        Assert.assertNotNull(commitChain.getEntryHash());
        Assert.assertNotNull(commitChain.getTxId());

        FactomResponse<RevealResponse> revealResponse = factomdClient.revealChain(revealChainEntry);
        assertValidResponse(revealResponse);

        RevealResponse revealChain = revealResponse.getResult();
        chainId = revealChain.getChainId();
        entryHash = revealChain.getEntryHash();
        Assert.assertNotNull(chainId);
        Assert.assertNotNull(entryHash);
        Assert.assertEquals("Entry Reveal Success", revealChain.getMessage());
    }

    @Test
    public void _02_verifyCommitChain() throws FactomException.ClientException, InterruptedException {
        int count = 0;
        boolean confirmed = false;
        while (!confirmed && count < 120) { // wait (10m * 60) / 5 sec
            Thread.sleep(5000);

            FactomResponse<EntryTransactionResponse> transactionsResponse = factomdClient.ackEntryTransactions(entryHash);
            assertValidResponse(transactionsResponse);

            EntryTransactionResponse entryTransaction = transactionsResponse.getResult();

            if (EntryTransactionResponse.CommitData.Status.DBlockConfirmed == entryTransaction.getCommitData().getStatus()) {
                confirmed = true;
            }

            count++;
        }

        Assert.assertTrue(confirmed);
    }

    @Test
    public void _03_commitEntry() throws FactomException.ClientException {
        List<String> externalIds = Arrays.asList("cd90", "90cd");

        Entry entry = new Entry();
        entry.setChainId(chainId);
        entry.setContent("abcdef");
        entry.setExternalIds(externalIds);

        FactomResponse<ComposeResponse> composeResponse = walletdClient.composeEntry(entry, publicKey);
        assertValidResponse(composeResponse);

        ComposeResponse composeEntry = composeResponse.getResult();
        Assert.assertNotNull(composeEntry.getCommit());
        Assert.assertNotNull(composeEntry.getCommit().getId());
        Assert.assertNotNull(composeEntry.getCommit().getParams());

        String commitEntryMessage = composeEntry.getCommit().getParams().getMessage();
        Assert.assertNotNull(commitEntryMessage);
        Assert.assertNotNull(composeEntry.getReveal());
        Assert.assertNotNull(composeEntry.getReveal().getId());
        Assert.assertNotNull(composeEntry.getReveal().getParams());

        String revealCommitMessage = composeEntry.getReveal().getParams().getEntry();
        Assert.assertNotNull(revealCommitMessage);

        FactomResponse<CommitEntryResponse> commitEntryResponse = factomdClient.commitEntry(commitEntryMessage);
        assertValidResponse(commitEntryResponse);

        CommitEntryResponse commitEntry = commitEntryResponse.getResult();
        Assert.assertNotNull(commitEntry.getMessage());
        Assert.assertNotNull(commitEntry.getTxId());
        Assert.assertNotNull(commitEntry.getEntryHash());

        FactomResponse<RevealResponse> revealResponse = factomdClient.revealChain(revealCommitMessage);
        assertValidResponse(revealResponse);

        RevealResponse revealEntry = revealResponse.getResult();
        Assert.assertEquals(chainId, revealEntry.getChainId());
        Assert.assertEquals("Entry Reveal Success", revealEntry.getMessage());

        entryHash = revealEntry.getEntryHash();
        Assert.assertNotNull(entryHash);
    }

    @Test
    public void _04_verifyCommitEntry() throws FactomException.ClientException, InterruptedException {
        int count = 0;
        boolean confirmed = false;
        while (!confirmed && count < 120) { // wait (10m * 60) / 5 sec
            Thread.sleep(5000);

            FactomResponse<EntryTransactionResponse> transactionsResponse = factomdClient.ackEntryTransactions(entryHash);
            assertValidResponse(transactionsResponse);

            EntryTransactionResponse entryTransaction = transactionsResponse.getResult();

            if (EntryTransactionResponse.CommitData.Status.DBlockConfirmed == entryTransaction.getCommitData().getStatus()) {
                confirmed = true;
            }

            count++;
        }

        Assert.assertTrue(confirmed);
    }
}
