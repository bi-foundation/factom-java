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

import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChainEntrySigningIT extends AbstractClientTest {

    private static String chainId;

    @Test
    public void _01_commitChain() {
        Chain chain = chain();
        Address secretAddress = new Address(EC_SECRET_ADDRESS);

        CommitAndRevealChainResponse response = offlineEntryClient.commitAndRevealChain(chain(), secretAddress).join();

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getCommitChainResponse());
        Assert.assertNotNull(response.getCommitChainResponse().getTxId());
        Assert.assertNotNull(response.getCommitChainResponse().getEntryHash());
        Assert.assertNotNull(response.getCommitChainResponse().getChainIdHash());
        Assert.assertEquals("Chain Commit Success", response.getCommitChainResponse().getMessage());
        Assert.assertNotNull(response.getRevealResponse());
        Assert.assertNotNull(response.getRevealResponse().getChainId());
        Assert.assertEquals("Entry Reveal Success", response.getRevealResponse().getMessage());
        Assert.assertNotNull(response.getRevealResponse().getEntryHash());

        chainId = response.getRevealResponse().getChainId();
    }

    @Test
    public void _02_commitEntry() {
        Assert.assertNotNull(chainId);
        Address secretAddress = new Address(EC_SECRET_ADDRESS);

        Entry entry = entry(chainId);
        CommitAndRevealEntryResponse response = offlineEntryClient.commitAndRevealEntry(entry, secretAddress).join();

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getCommitEntryResponse());
        Assert.assertNotNull(response.getCommitEntryResponse().getTxId());
        Assert.assertNotNull(response.getCommitEntryResponse().getEntryHash());
        Assert.assertEquals("Entry Commit Success", response.getCommitEntryResponse().getMessage());
        Assert.assertNotNull(response.getRevealResponse());
        Assert.assertNotNull(response.getRevealResponse().getEntryHash());
        Assert.assertEquals("Entry Reveal Success", response.getRevealResponse().getMessage());
        Assert.assertEquals(chainId, response.getRevealResponse().getChainId());
    }

    private Chain chain() {
        String randomness = new Date().toString();
        List<String> externalIds = Arrays.asList(
                "ChainEntrySigningIT",
                randomness
        );

        Entry firstEntry = new Entry();
        firstEntry.setExternalIds(externalIds);
        firstEntry.setContent("ChainEntry self signing integration test content");

        Chain chain = new Chain();
        chain.setFirstEntry(firstEntry);
        return chain;
    }

    private Entry entry(String chainId) {
        List<String> externalIds = Arrays.asList("Entry ExtID 1", "Entry ExtID 2");

        Entry entry = new Entry();
        entry.setChainId(chainId);
        entry.setContent("Entry self signing content");
        entry.setExternalIds(externalIds);
        return entry;
    }
}
