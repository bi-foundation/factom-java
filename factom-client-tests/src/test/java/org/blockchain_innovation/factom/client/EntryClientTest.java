package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.api.Encoding;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.EntryClient;
import org.blockchain_innovation.factom.client.impl.json.gson.GsonConverter;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class EntryClientTest extends AbstractClientTest {

    @Test
    public void testChain() {
        Chain chain = chain();
        CommitAndRevealChainResponse commitAndRevealChain = entryClient.commitAndRevealChain(chain, EC_PUBLIC_ADDRESS).join();

        Assert.assertEquals("Chain Commit Success",commitAndRevealChain.getCommitChainResponse().getMessage());
        Assert.assertEquals("Entry Reveal Success",commitAndRevealChain.getRevealResponse().getMessage());
        Assert.assertEquals(commitAndRevealChain.getCommitChainResponse().getEntryHash(), commitAndRevealChain.getRevealResponse().getEntryHash());
        System.out.println("commitAndRevealChain = " + commitAndRevealChain);
    }


    @Test
    public void testEntry() {
        Entry entry = entry();
        CompletableFuture<CommitAndRevealEntryResponse> commitFuture = entryClient.commitAndRevealEntry(entry, EC_PUBLIC_ADDRESS);
        CommitAndRevealEntryResponse commitAndRevealChain = commitFuture.join();

        Assert.assertEquals("Entry Commit Success",commitAndRevealChain.getCommitEntryResponse().getMessage());
        Assert.assertEquals("Entry Reveal Success",commitAndRevealChain.getRevealResponse().getMessage());
        Assert.assertEquals(commitAndRevealChain.getCommitEntryResponse().getEntryHash(), commitAndRevealChain.getRevealResponse().getEntryHash());
    }


    private Chain chain() {
        int randomness = 10000000 + new Random().nextInt(90000000);
        List<String> externalIds = Arrays.asList(
                String.valueOf(randomness),
                "80731950",
                "61626364");

        Entry firstEntry = new Entry();
        firstEntry.setExternalIds(externalIds);
        firstEntry.setContent("3132333461626364");

        Chain chain = new Chain();
        chain.setFirstEntry(firstEntry);
        return chain;
    }

    private Entry entry() {
        List<String> externalIds = Arrays.asList("cd94", "90cd", Encoding.HEX.encode(Encoding.UTF_8.decode("TEST2")));

        Entry entry = new Entry();
        entry.setChainId("8409e82079e958e9beb31e76634234c3dc2b8ecf5db888e440541678a79dd4db");
        entry.setContent("abcdef");
        entry.setExternalIds(externalIds);

        return entry;
    }

}
