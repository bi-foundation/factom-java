package org.blockchain_innovation.factom.client.jee.cdi;

import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;
import org.blockchain_innovation.factom.client.impl.EntryApiImpl;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(Arquillian.class)
public class EventTest extends AbstractCDITest {

    @Inject
    @ManagedClient
    private Provider<EntryApiImpl> entryApiProvider;

    static AtomicReference<CommitEntryResponse> commitChainResponseRef = new AtomicReference<>();
    static AtomicReference<RevealResponse> revealChainResponseRef = new AtomicReference<>();
    static AtomicReference<EntryTransactionResponse> transactionAcknowledgedResponseRef = new AtomicReference<>();


    @Test
    public void commitAndRevealEventTest() {
        Assert.assertNotNull(entryApiProvider.get());

        Chain chain = chain();

        EntryApiImpl entryClient = entryApiProvider.get();

        CommitAndRevealChainResponse commitAndRevealChain = entryClient.commitAndRevealChain(chain, EC_PUBLIC_ADDRESS).join();

        Assert.assertEquals("Chain Commit Success", commitAndRevealChain.getCommitChainResponse().getMessage());
        Assert.assertEquals("Entry Reveal Success", commitAndRevealChain.getRevealResponse().getMessage());
        Assert.assertEquals(commitAndRevealChain.getCommitChainResponse().getEntryHash(), commitAndRevealChain.getRevealResponse().getEntryHash());
        Assert.assertEquals("Chain Commit Success", commitChainResponseRef.get().getMessage());
        Assert.assertEquals("Entry Reveal Success", revealChainResponseRef.get().getMessage());
        Assert.assertEquals(commitChainResponseRef.get().getEntryHash(), revealChainResponseRef.get().getEntryHash());

        Assert.assertNotNull(transactionAcknowledgedResponseRef.get());
        Assert.assertNotNull(commitAndRevealChain.getCommitChainResponse().getEntryHash(), transactionAcknowledgedResponseRef.get().getEntryHash());

        System.out.println("commitAndRevealChain = " + commitAndRevealChain);

    }

    public void testEvent(@Observes ComposeResponse composeResponse) {
        System.err.println("COMPOSE RESONSE: "+ composeResponse);
    }

    public void commitChainResponse(@Observes CommitChainResponse commitChainResponse) {
        System.err.println("COMMIT CHAIN RESPONSE: "+ commitChainResponse);
        commitChainResponseRef.set(commitChainResponse);
    }
    public void revealChainResponse(@Observes RevealResponse revealResponse) {
        System.err.println("REVEAL RESPONSE: "+ revealResponse);
        revealChainResponseRef.set(revealResponse);
    }

    public void transactionAcknowledged(@Observes EntryTransactionResponse transactionResponse) {
        System.err.println("TRANSACTION RESPONSE: "+ transactionResponse);
        transactionAcknowledgedResponseRef.set(transactionResponse);
    }

    public void onError(@Observes RpcErrorResponse errorResponse) {
        System.err.println("ERROR RESPONSE: "+ errorResponse);
        Assert.fail(errorResponse.getError().getMessage());
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
