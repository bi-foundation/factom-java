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

import org.blockchain_innovation.factom.client.api.FactomException;
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
public class OfflineChainEntryIT extends ChainEntryIT {

    @Test
    public void _01_composeChain() throws FactomException.ClientException {
        Chain chain = chain();

        Address address = new Address(EC_SECRET_ADDRESS);
        FactomResponse<ComposeResponse> composeResponse = offlineWalletdClient.composeChain(chain, address).join();

        composeChain = composeResponse.getResult();
        Assert.assertNotNull(composeResponse.getResult().getCommit());
        Assert.assertNotNull(composeResponse.getResult().getCommit().getId());
        Assert.assertNotNull(composeResponse.getResult().getCommit().getParams());
    }

    @Test
    public void _02_commitChain() throws FactomException.ClientException {
        // override the methods to keep the same order
        super._02_commitChain();
    }

    @Test
    public void _03_verifyCommitChain() throws FactomException.ClientException, InterruptedException {
        super._03_verifyCommitChain();
    }

    @Test
    public void _04_composeEntry() throws FactomException.ClientException {
        Entry entry = entry(chainId);
        Address address = new Address(EC_SECRET_ADDRESS);

        FactomResponse<ComposeResponse> composeResponse = offlineWalletdClient.composeEntry(entry, address).join();

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
}
