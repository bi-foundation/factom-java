/*
 * Copyright 2018 Blockchain Innovation Foundation <https://blockchain-innovation.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeResponse;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChainEntryIT extends AbstractClientTest {

    protected static String chainId /*= "23fc40b5d301f8c40513cb1363439bc23e6c21856073abefdb1a2a2e49baba3b"*/;
    protected static String entryHash;
    protected static ComposeResponse composeChain;
    protected static ComposeResponse composeEntry;

    @Test
    public void _01_composeChain() throws FactomException.ClientException, ExecutionException, InterruptedException {
        Chain chain = chain();

        Address address = new Address(EC_PUBLIC_ADDRESS);
        FactomResponse<ComposeResponse> composeResponse = walletdClient.composeChain(chain, address).join();
        assertValidResponse(composeResponse);

        composeChain = composeResponse.getResult();
        Assert.assertNotNull(composeChain.getCommit());
        Assert.assertNotNull(composeChain.getCommit().getId());
        Assert.assertNotNull(composeChain.getCommit().getParams());
    }

    @Test
    public void _02_commitChain() throws FactomException.ClientException {
        String commitChainMessage = composeChain.getCommit().getParams().getMessage();
        Assert.assertNotNull(commitChainMessage);
        Assert.assertNotNull(composeChain.getReveal());
        Assert.assertNotNull(composeChain.getReveal().getId());
        Assert.assertNotNull(composeChain.getReveal().getParams());

        String revealChainEntry = composeChain.getReveal().getParams().getEntry();
        Assert.assertNotNull(revealChainEntry);

        FactomResponse<CommitChainResponse> commitChainResponse = factomdClient.commitChain(commitChainMessage).join();
        assertValidResponse(commitChainResponse);

        CommitChainResponse commitChain = commitChainResponse.getResult();
        Assert.assertNotNull(commitChain);
        Assert.assertEquals("Chain Commit Success", commitChain.getMessage());
        Assert.assertNotNull(commitChain.getEntryHash());
        Assert.assertNotNull(commitChain.getChainIdHash());
        Assert.assertNotNull(commitChain.getTxId());

        FactomResponse<RevealResponse> revealResponse = factomdClient.revealChain(revealChainEntry).join();
        assertValidResponse(revealResponse);

        RevealResponse revealChain = revealResponse.getResult();
        chainId = revealChain.getChainId();
        entryHash = revealChain.getEntryHash();
        Assert.assertNotNull(chainId);
        Assert.assertNotNull(entryHash);
        Assert.assertEquals("Entry Reveal Success", revealChain.getMessage());
    }

    @Test
    public void _03_verifyCommitChain() throws FactomException.ClientException, InterruptedException {
        boolean confirmed = waitOnConfirmation(EntryTransactionResponse.Status.TransactionACK, 15);
        Assert.assertTrue(confirmed);
        Thread.sleep(2000);
    }

    @Test
    public void _04_composeEntry() throws FactomException.ClientException {
        Entry entry = entry(chainId);
        Address address = new Address(EC_PUBLIC_ADDRESS);

        FactomResponse<ComposeResponse> composeResponse = walletdClient.composeEntry(entry, address).join();
        assertValidResponse(composeResponse);

        composeEntry = composeResponse.getResult();
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

    }

    @Test
    public void _05_commitEntry() throws FactomException.ClientException {
        String commitEntryMessage = composeEntry.getCommit().getParams().getMessage();
        String revealCommitMessage = composeEntry.getReveal().getParams().getEntry();

        FactomResponse<CommitEntryResponse> commitEntryResponse = factomdClient.commitEntry(commitEntryMessage).join();
        assertValidResponse(commitEntryResponse);

        CommitEntryResponse commitEntry = commitEntryResponse.getResult();
        Assert.assertNotNull(commitEntry.getMessage());
        Assert.assertNotNull(commitEntry.getTxId());
        Assert.assertNotNull(commitEntry.getEntryHash());

        FactomResponse<RevealResponse> revealResponse = factomdClient.revealChain(revealCommitMessage).join();
        assertValidResponse(revealResponse);

        RevealResponse revealEntry = revealResponse.getResult();
        Assert.assertEquals(chainId, revealEntry.getChainId());
        Assert.assertEquals("Entry Reveal Success", revealEntry.getMessage());

        entryHash = revealEntry.getEntryHash();
        Assert.assertNotNull(entryHash);
    }

    @Test
    public void _06_verifyCommitEntry() throws FactomException.ClientException, InterruptedException {
        boolean confirmed = waitOnConfirmation(EntryTransactionResponse.Status.TransactionACK, 10);
        Assert.assertTrue(confirmed);
    }

    private boolean waitOnConfirmation(EntryTransactionResponse.Status desiredStatus, int maxSeconds) throws InterruptedException, FactomException.ClientException {
        int seconds = 0;
        while (seconds < maxSeconds) {
            System.out.println("At verification second: " + seconds);
            FactomResponse<EntryTransactionResponse> transactionsResponse = factomdClient.ackTransactions(entryHash, chainId, EntryTransactionResponse.class).join();
            assertValidResponse(transactionsResponse);

            EntryTransactionResponse entryTransaction = transactionsResponse.getResult();
            System.out.println("---");
            EntryTransactionResponse.Status status = entryTransaction.getCommitData().getStatus();
            if (seconds > 12 && seconds % 6 == 0 && EntryTransactionResponse.Status.TransactionACK != status) {
                System.err.println("Transaction still not in desired status after: " + seconds + "State: " + status + ". Probably will not succeed!");
            } else if (desiredStatus == status) {
                return true;
            }
            seconds++;
            Thread.sleep(1000);
        }
        return false;
    }

    protected Chain chain() {
        String randomness = /*"1234859584933";*/new Date().toString();
        List<String> externalIds = Arrays.asList(
                "ChainEntryIT",
                randomness
        );

        Entry firstEntry = new Entry();
        firstEntry.setExternalIds(externalIds);
        firstEntry.setContent("ChainEntry integration test content");

        Chain chain = new Chain();
        chain.setFirstEntry(firstEntry);
        return chain;
    }

    protected Entry entry(String chainId) {
        List<String> externalIds = Arrays.asList("Entry ExtID 1", "Entry ExtID 2");

        Entry entry = new Entry();
        entry.setChainId(chainId);
        entry.setContent("Entry content");
        entry.setExternalIds(externalIds);
        return entry;
    }
}
