package org.blockchain_innovation.accumulate.factombridge;

import org.blockchain_innovation.factom.client.api.listeners.CommitAndRevealListener;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.*;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.api.rpc.RpcErrorResponse;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EntryApiTest extends AbstractClientTest {

    private static String chainId;

    @Test
    public void _01_testChain() {
        Chain chain = chain();
        AtomicReference<CommitEntryResponse> commitChainResponse = new AtomicReference<>();
        AtomicReference<RevealResponse> revealChainResponse = new AtomicReference<>();
        AtomicReference<EntryTransactionResponse> transactionAcknowledgedResponse = new AtomicReference<>();

        CommitAndRevealListener listener = new CommitAndRevealListener() {

            @Override
            public void onCompose(ComposeResponse composeResponse) {
                System.out.println("> Compose = " + composeResponse);
            }

            @Override
            public void onCommit(CommitEntryResponse commitResponse) {
                System.out.println("> Commit = " + commitResponse);
            }

            @Override
            public void onCommit(CommitChainResponse commitResponse) {
                System.out.println("> Commit = " + commitResponse);
                commitChainResponse.set(commitResponse);
            }

            @Override
            public void onReveal(RevealResponse revealResponse) {
                System.out.println("> Reveal = " + revealResponse);
                revealChainResponse.set(revealResponse);
            }

            @Override
            public void onTransactionAcknowledged(EntryTransactionResponse transactionResponse) {
                System.out.println("> TransactionAcknowledged = " + transactionResponse);
                transactionAcknowledgedResponse.set(transactionResponse);
            }

            @Override
            public void onCommitConfirmed(EntryTransactionResponse transactionResponse) {
                System.out.println("> ChainCommitConfirmed = " + transactionResponse);
            }

            @Override
            public void onError(RpcErrorResponse errorResponse) {
                System.out.println("e = " + errorResponse);
                Assert.fail(errorResponse.getJsonrpc());
            }
        };

        entryClient.clearListeners().addListener(listener);

        CommitAndRevealChainResponse commitAndRevealChain = entryClient.commitAndRevealChain(chain, liteAccount).join();

        Assert.assertEquals("Chain Commit Success", commitAndRevealChain.getCommitChainResponse().getMessage());
        Assert.assertEquals("Entry Reveal Success", commitAndRevealChain.getRevealResponse().getMessage());
        Assert.assertEquals(commitAndRevealChain.getCommitChainResponse().getEntryHash(), commitAndRevealChain.getRevealResponse().getEntryHash());
        Assert.assertEquals("Chain Commit Success", commitChainResponse.get().getMessage());
        Assert.assertEquals("Entry Reveal Success", revealChainResponse.get().getMessage());
        Assert.assertEquals(commitChainResponse.get().getEntryHash(), revealChainResponse.get().getEntryHash());
        Assert.assertNotNull(transactionAcknowledgedResponse.get());
        Assert.assertNotNull(commitAndRevealChain.getCommitChainResponse().getEntryHash(), transactionAcknowledgedResponse.get().getEntryHash());

        System.out.println("commitAndRevealChain = " + commitAndRevealChain);
        chainId = commitAndRevealChain.getRevealResponse().getChainId();
    }


    @Test
    public void _02_testEntry() throws InterruptedException {
        Entry entry = entry();
        AtomicReference<CommitEntryResponse> commitEntryResponse = new AtomicReference<>();
        AtomicReference<RevealResponse> revealEntryResponse = new AtomicReference<>();
        AtomicReference<EntryTransactionResponse> transactionAcknowledgedResponse = new AtomicReference<>();

        final CommitAndRevealListener listener = new CommitAndRevealListener() {

            @Override
            public void onCompose(ComposeResponse composeResponse) {
                System.out.println("> Compose = " + composeResponse);
            }

            @Override
            public void onCommit(CommitEntryResponse commitResponse) {
                System.out.println("> Commit = " + commitResponse);
                commitEntryResponse.set(commitResponse);
            }

            @Override
            public void onCommit(CommitChainResponse commitResponse) {
                System.out.println("> Commit = " + commitResponse);
                commitEntryResponse.set(commitResponse);
            }

            @Override
            public void onReveal(RevealResponse revealResponse) {
                System.out.println("> Reveal = " + revealResponse);
                revealEntryResponse.set(revealResponse);
            }

            @Override
            public void onTransactionAcknowledged(EntryTransactionResponse transactionResponse) {
                System.out.println("> TransactionAcknowledged = " + transactionResponse);
                transactionAcknowledgedResponse.set(transactionResponse);
            }

            @Override
            public void onCommitConfirmed(EntryTransactionResponse transactionResponse) {
                System.out.println("> ChainCommitConfirmed = " + transactionResponse);
            }

            @Override
            public void onError(RpcErrorResponse errorResponse) {
                System.out.println("e = " + errorResponse);
                Assert.fail(errorResponse.getJsonrpc());
            }
        };

        entryClient.clearListeners().addListener(listener);
        CompletableFuture<CommitAndRevealEntryResponse> commitFuture = entryClient.commitAndRevealEntry(entry, liteAccount);
        CommitAndRevealEntryResponse commitAndRevealEntry = commitFuture.join();

        Assert.assertEquals("Entry Commit Success", commitAndRevealEntry.getCommitEntryResponse().getMessage());
        Assert.assertEquals("Entry Reveal Success", commitAndRevealEntry.getRevealResponse().getMessage());
        Assert.assertEquals(commitAndRevealEntry.getCommitEntryResponse().getEntryHash(), commitAndRevealEntry.getRevealResponse().getEntryHash());

        Assert.assertEquals("Entry Commit Success", commitEntryResponse.get().getMessage());
        Assert.assertEquals("Entry Reveal Success", revealEntryResponse.get().getMessage());
        Assert.assertEquals(commitAndRevealEntry.getCommitEntryResponse().getEntryHash(), commitEntryResponse.get().getEntryHash());
        Assert.assertEquals(commitAndRevealEntry.getRevealResponse().getEntryHash(), revealEntryResponse.get().getEntryHash());

        Assert.assertNotNull(transactionAcknowledgedResponse.get());
        Assert.assertNotNull(commitAndRevealEntry.getCommitEntryResponse().getEntryHash(), transactionAcknowledgedResponse.get().getEntryHash());
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
        entry.setChainId(chainId);
        entry.setContent("abcdef");
        entry.setExternalIds(externalIds);

        return entry;
    }


    @Test
    public void _03_testChainAsync() throws InterruptedException, ExecutionException {
        Chain chain = chain();
        AtomicReference<CommitEntryResponse> commitChainResponse = new AtomicReference<>();
        AtomicReference<RevealResponse> revealChainResponse = new AtomicReference<>();
        AtomicReference<EntryTransactionResponse> transactionAcknowledgedResponse = new AtomicReference<>();

        CommitAndRevealListener listener = new CommitAndRevealListener() {

            @Override
            public void onCompose(ComposeResponse composeResponse) {
                System.out.println("> Compose = " + composeResponse);
            }

            @Override
            public void onCommit(CommitEntryResponse commitResponse) {
                System.out.println("> Commit = " + commitResponse);
            }

            @Override
            public void onCommit(CommitChainResponse commitResponse) {
                System.out.println("> Commit = " + commitResponse);
                commitChainResponse.set(commitResponse);
            }

            @Override
            public void onReveal(RevealResponse revealResponse) {
                System.out.println("> Reveal = " + revealResponse);
                revealChainResponse.set(revealResponse);
            }

            @Override
            public void onTransactionAcknowledged(EntryTransactionResponse transactionResponse) {
                System.out.println("> TransactionAcknowledged = " + transactionResponse);
                transactionAcknowledgedResponse.set(transactionResponse);
            }

            @Override
            public void onCommitConfirmed(EntryTransactionResponse transactionResponse) {
                System.out.println("> ChainCommitConfirmed = " + transactionResponse);
            }

            @Override
            public void onError(RpcErrorResponse errorResponse) {
                System.out.println("e = " + errorResponse);
                Assert.fail(errorResponse.getJsonrpc());
            }
        };

        entryClient.clearListeners().addListener(listener);
        CompletableFuture<CommitAndRevealChainResponse> future = entryClient.commitAndRevealChain(chain, liteAccount, true);

        int count = 0;
        while (transactionAcknowledgedResponse.get() == null && count < 100) {
            count++;
            Thread.sleep(1000);
            if (future.isCompletedExceptionally()) {
                Assert.fail(future.get().toString());
            }
        }
        future.cancel(true);

        Assert.assertEquals("Chain Commit Success", commitChainResponse.get().getMessage());
        Assert.assertEquals("Entry Reveal Success", revealChainResponse.get().getMessage());
        Assert.assertEquals(commitChainResponse.get().getEntryHash(), revealChainResponse.get().getEntryHash());

        Assert.assertNotNull(transactionAcknowledgedResponse.get());
        Assert.assertNotNull(commitChainResponse.get().getEntryHash(), transactionAcknowledgedResponse.get().getEntryHash());
        //chainId = revealChainResponse.get().getChainId();
    }

    @Test
    public void _04_testGetAllEntriesBlocks() throws InterruptedException, ExecutionException {

        final CompletableFuture<List<EntryBlockResponse>> future = entryClient.allEntryBlocks(chainId);
        future.thenAccept(entryResponses -> {
            Assert.assertEquals(2, entryResponses.size());

        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            Assert.fail("allEntries failed");
            return null;
        });
        int count = 0;
        while (count < 100) {
            count++;
            Thread.sleep(1000);
            if (future.isCompletedExceptionally()) {
                Assert.fail("allEntries failed");
            } else if (future.isDone()) {
                break;
            }
        }
        future.cancel(true);

    }

    @Test
    public void _05_testGetAllEntries() throws InterruptedException {
        Thread.sleep(7000);

        final CompletableFuture<List<EntryResponse>> future = entryClient.allEntries(chainId);
        future.thenAccept(entryResponses -> {
            Assert.assertEquals(2, entryResponses.size());
            Assert.assertEquals(Encoding.HEX.encode(Encoding.UTF_8.decode("abcdef")), entryResponses.get(0).getContent());
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            Assert.fail("allEntries failed");
            return null;
        });
        int count = 0;
        while (count < 100) {
            count++;
            Thread.sleep(1000);
            if (future.isCompletedExceptionally()) {
                Assert.fail("allEntries failed");
            } else if (future.isDone()) {
                break;
            }
        }
        future.cancel(true);

    }
}
