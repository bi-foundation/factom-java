package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.impl.EntryOfflineSigningClientApi;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class SigningTest extends AbstractClientTest {

    private EntryOfflineSigningClientApi entryClient = new EntryOfflineSigningClientApi();

    @Test
    public void testChain() {
        Chain chain = chain();
        CommitAndRevealChainResponse commitAndRevealChain = entryClient.commitAndRevealChain(chain, EC_PUBLIC_ADDRESS, EC_SECRET_ADDRESS).join();

        Assert.assertEquals("Entry Commit Success",commitAndRevealChain.getCommitChainResponse().getMessage());
        Assert.assertEquals("Entry Reveal Success",commitAndRevealChain.getRevealResponse().getMessage());
        Assert.assertEquals(commitAndRevealChain.getCommitChainResponse().getEntryHash(), commitAndRevealChain.getRevealResponse().getEntryHash());
    }

    @Test
    public void testComposeChain() {
        Chain chain = chain();

        String composeChainCommit = entryClient.composeChainCommit(chain, EC_PUBLIC_ADDRESS, EC_SECRET_ADDRESS);
        System.out.println("composeChainCommit = " + composeChainCommit);
        FactomResponse<CommitChainResponse> commitChainResponse = factomdClient.commitChain(composeChainCommit).join();
        assertValidResponse(commitChainResponse);

        CommitChainResponse commitChain = commitChainResponse.getResult();
        Assert.assertEquals("Chain Commit Success",commitChain.getMessage());
        Assert.assertNotNull(commitChain.getChainIdHash());
        Assert.assertNotNull(commitChain.getEntryHash());
        Assert.assertNotNull(commitChain.getTxId());

        String composeChainReveal = entryClient.composeChainReveal(chain);
        System.out.println("composeChainReveal = " + composeChainReveal);
        FactomResponse<RevealResponse> revealChainResponse = factomdClient.revealChain(composeChainReveal).join();
        assertValidResponse(revealChainResponse);

        RevealResponse revealEntry = revealChainResponse.getResult();
        Assert.assertEquals("Entry Reveal Success",revealEntry.getMessage());
        Assert.assertNotNull(revealEntry.getChainId());
        Assert.assertEquals(commitChain.getEntryHash(), revealEntry.getEntryHash());
    }

    @Test
    public void testEntry() {
        Entry entry = entry();
        CommitAndRevealEntryResponse commitAndRevealChain = entryClient.commitAndRevealEntry(entry, EC_PUBLIC_ADDRESS, EC_SECRET_ADDRESS).join();

        Assert.assertEquals("Entry Commit Success",commitAndRevealChain.getCommitEntryResponse().getMessage());
        Assert.assertEquals("Entry Reveal Success",commitAndRevealChain.getRevealResponse().getMessage());
        Assert.assertEquals(commitAndRevealChain.getCommitEntryResponse().getEntryHash(), commitAndRevealChain.getRevealResponse().getEntryHash());
    }

    @Test
    public void testComposeEntry() {
        Entry entry = entry();

        String composeEntryCommit = entryClient.composeEntryCommit(entry, EC_PUBLIC_ADDRESS, EC_SECRET_ADDRESS);
        FactomResponse<CommitEntryResponse> commitEntryResponse = factomdClient.commitEntry(composeEntryCommit).join();
        assertValidResponse(commitEntryResponse);

        CommitEntryResponse commitEntry = commitEntryResponse.getResult();
        Assert.assertEquals("Entry Commit Success",commitEntry.getMessage());
        Assert.assertNotNull(commitEntry.getTxId());
        Assert.assertNotNull(commitEntry.getEntryHash());

        String composeEntryReveal = entryClient.composeEntryReveal(entry);
        FactomResponse<RevealResponse> revealEntryResponse = factomdClient.revealEntry(composeEntryReveal).join();
        assertValidResponse(revealEntryResponse);

        RevealResponse revealEntry = revealEntryResponse.getResult();
        Assert.assertEquals("Entry Reveal Success",revealEntry.getMessage());
        Assert.assertNotNull(revealEntry.getChainId());
        Assert.assertEquals(commitEntry.getEntryHash(), revealEntry.getEntryHash());

    }

    private Chain chain() {
        List<String> externalIds = Arrays.asList(
                "80731950",
                "61626364",
                "31323334");

        Chain.Entry firstEntry = new Chain.Entry();
        firstEntry.setExternalIds(externalIds);
        firstEntry.setContent("3132333461626364");

        Chain chain = new Chain();
        chain.setFirstEntry(firstEntry);
        return chain;
    }

    private Entry entry() {
        List<String> externalIds = Arrays.asList("cd90", "90cd");

        Entry entry = new Entry();
        entry.setChainId("8008392c6baf81ab99aa14fd3ce7ac62726d76dc25c1c61029ad7a06c0531fb5");
        entry.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam bibendum, nibh sit amet auctor varius, enim mi aliquam odio, ut ornare quam lacus eget justo. Integer iaculis, risus sed pharetra commodo, tellus turpis euismod diam, quis mattis elit elit eget felis. Nam pretium metus nec imperdiet placerat. Sed in diam felis. Sed at sapien eu magna suscipit vulputate eget ut quam. Cras pellentesque condimentum imperdiet. Donec arcu nisl, hendrerit quis gravida in, hendrerit eget enim. Aenean tortor urna, pellentesque ac augue eget, viverra elementum nisi. Etiam condimentum tellus arcu, a varius libero faucibus id.\n" +
                "\n" +
                "Nullam et sodales nisl. Fusce pulvinar auctor odio eu sagittis. Vivamus in lectus volutpat, vulputate arcu quis, viverra nibh. Vestibulum viverra tellus auctor, ornare mi sit amet, placerat dolor. Aenean elementum odio ut velit suscipit rhoncus. Donec nec accumsan odio, a rhoncus velit. Maecenas mattis vel purus et consectetur. Ut pulvinar ligula odio, eget pretium arcu consectetur a. Morbi luctus rhoncus diam, sed pharetra mauris dapibus lacinia.\n" +
                "\n" +
                "Proin non commodo sapien, sit amet varius urna. Mauris ullamcorper ligula eu augue consequat, vel lobortis eros molestie. Integer a lectus arcu. Etiam auctor condimentum placerat. Nam quis erat id enim venenatis dignissim a et est. Praesent volutpat, turpis at maximus commodo, diam ante mollis arcu, ut efficitur lorem mi a augue. Donec ut lorem massa. Aliquam scelerisque, risus non scelerisque congue, ante urna tempor metus, at tempus justo metus et nisi. Pellentesque volutpat nec tellus at venenatis. Cras eget consequat diam. Morbi rhoncus risus non est malesuada auctor. Ut risus diam, lacinia nec scelerisque in, euismod a odio.\n" +
                "\n" +
                "Praesent turpis diam, consequat eu iaculis id, feugiat sed augue. Integer iaculis augue nec urna dapibus convallis. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Quisque velit sem, posuere non eleifend sit amet, maximus et lectus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Suspendisse faucibus sit amet ante eget tristique. In viverra, diam ac ultricies porttitor, felis dolor tristique nibh, ac porttitor ligula mauris ultrices ligula. Vivamus vel odio enim. Maecenas posuere eleifend diam sed tristique. Morbi id venenatis urna. Quisque quis nunc in sapien suscipit finibus sit amet ut ante. Quisque dignissim justo ut condimentum euismod. Donec condimentum, nulla aliquet tempus tincidunt, mauris diam semper sapien, dignissim placerat elit turpis in tortor. Cras tincidunt ligula nunc, eget tristique erat vestibulum tincidunt.\n" +
                "\n" +
                "Donec auctor nibh a commodo aliquam. In interdum egestas elit, quis vehicula risus lacinia non. Cras non purus a massa laoreet tristique sit amet eget est. Donec a aliquam eros, sit amet consequat neque. Nunc lorem diam, malesuada eu imperdiet a, volutpat consectetur dolor. Sed diam risus, eleifend eget nibh ac, pulvinar vehicula erat. Praesent arcu nibh, porta sit amet vulputate non, porta nec ipsum.");
        entry.setExternalIds(externalIds);

        return entry;
    }
}
