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

import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.response.factomd.AdminBlockResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.ChainHeadResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CurrentMinuteResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.DirectoryBlockHeadResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.DirectoryBlockHeightResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.DirectoryBlockResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryBlockResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryCreditBalanceResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryCreditBlockResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryCreditRateResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.FactoidBalanceResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.FactoidBlockResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.FactoidSubmitResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.FactoidTransactionsResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.HeightsResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.PendingEntriesResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.PendingTransactionsResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.PropertiesResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RawDataResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.ReceiptResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.TransactionResponse;
import org.blockchain_innovation.factom.client.api.rpc.RpcMethod;
import org.blockchain_innovation.factom.client.impl.FactomRequestImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.fail;

public class FactomdClientTest extends AbstractClientTest {

    @Test
    public void manualRequest() throws FactomException.ClientException {
        Assert.assertEquals("null", new FactomRequestImpl(null).toString());

        FactomRequestImpl factomRequest = new FactomRequestImpl(RpcMethod.PROPERTIES.toRequestBuilder().id(5).build());
        Assert.assertNotNull(factomRequest.toString());
        FactomResponse<Map> response = factomdClient.exchange(factomRequest, Map.class).join();
        assertValidResponse(response);
        Assert.assertEquals(5, response.getRpcResponse().getId());
        Assert.assertEquals("2.0", response.getRpcResponse().getJsonrpc());
    }

    @Test
    public void testAdminBlockByHeight() throws FactomException.ClientException {
        FactomResponse<AdminBlockResponse> response = factomdClient.adminBlockByHeight(10).join();
        assertValidResponse(response);

        AdminBlockResponse adminBlock = response.getResult();
        Assert.assertNotNull(adminBlock);
        Assert.assertNotNull(adminBlock.getRawData());
        Assert.assertNotNull(adminBlock.getAdminBlock());
        Assert.assertNotNull(adminBlock.getAdminBlock().getLookUpHash());
        Assert.assertNotNull(adminBlock.getAdminBlock().getBackReferenceHash());

        Assert.assertNotNull(adminBlock.getAdminBlock().getHeader());
        Assert.assertNotNull(adminBlock.getAdminBlock().getHeader().getAdminChainId());
        Assert.assertNotNull(adminBlock.getAdminBlock().getHeader().getChainId());
        Assert.assertNotNull(adminBlock.getAdminBlock().getHeader().getHeaderExpansionArea());
        Assert.assertNotNull(adminBlock.getAdminBlock().getHeader().getPreviousBackReferenceHash());
        Assert.assertTrue(adminBlock.getAdminBlock().getHeader().getMessageCount() > 0);
        Assert.assertTrue(adminBlock.getAdminBlock().getHeader().getBodySize() > 0);
        Assert.assertTrue(adminBlock.getAdminBlock().getHeader().getDirectoryBlockHeight() > 0);
        Assert.assertTrue(adminBlock.getAdminBlock().getHeader().getHeaderExpansionSize() >= 0);

        Assert.assertNotNull(adminBlock.getAdminBlock().getAdminBlockEntries());
        Assert.assertFalse(adminBlock.getAdminBlock().getAdminBlockEntries().isEmpty());
        Assert.assertNotNull(adminBlock.getAdminBlock().getAdminBlockEntries().get(0).getIdentityAdminChainId());
        Assert.assertNotNull(adminBlock.getAdminBlock().getAdminBlockEntries().get(0).getPreviousDirectoryBlockSignature());
        Assert.assertNotNull(adminBlock.getAdminBlock().getAdminBlockEntries().get(0).getPreviousDirectoryBlockSignature().getPublicKey());
        Assert.assertNotNull(adminBlock.getAdminBlock().getAdminBlockEntries().get(0).getPreviousDirectoryBlockSignature().getSignature());
    }

    @Test
    public void testAckTransactions() throws FactomException.ClientException {
        FactomResponse<EntryTransactionResponse> response = factomdClient.ackTransactions("0fb1b4c917e933d1d7aeb157398360fa36af6902131ea5037b04af510483caa3", "2b079f0f6b1e84315827f5b2f799a9d3219fdd975ea59aafa2dcb7dfb0fc02f6", EntryTransactionResponse.class).join();
        assertValidResponse(response);

        EntryTransactionResponse entryTransaction = response.getResult();
        Assert.assertNotNull(entryTransaction);
        Assert.assertNotNull(entryTransaction.getCommitTxId());
        Assert.assertNotNull(entryTransaction.getEntryHash());
        Assert.assertNotNull(entryTransaction.getCommitData());
        Assert.assertNotNull(entryTransaction.getCommitData().getStatus());
        Assert.assertNotNull(entryTransaction.getEntryData());
        Assert.assertNotNull(entryTransaction.getEntryData().getStatus());
    }

    @Test
    public void testAckEntryTransactions() throws FactomException.ClientException {
        FactomResponse<EntryTransactionResponse> response = factomdClient.ackEntryTransactions("e96cca381bf25f6dd4dfdf9f7009ff84ee6edaa3f47f9ccf06d2787482438f4b").join();
        assertValidResponse(response);

        EntryTransactionResponse entryTransaction = response.getResult();
        Assert.assertNotNull(entryTransaction);
        Assert.assertNotNull(entryTransaction.getCommitTxId());
        Assert.assertNotNull(entryTransaction.getEntryHash());
        Assert.assertNotNull(entryTransaction.getCommitData());
        Assert.assertNotNull(entryTransaction.getCommitData().getStatus());
        Assert.assertNotNull(entryTransaction.getEntryData());
        Assert.assertNotNull(entryTransaction.getEntryData().getStatus());
    }

    @Test
    public void testAckFactoidTransactions() throws FactomException.ClientException {
        FactomResponse<FactoidTransactionsResponse> response = factomdClient.ackFactoidTransactions("0f4e09dd02236880b3bb7d22cda6c8f29fd4fabc02aba1229b2d8680b9043a2a").join();
        assertValidResponse(response);

        FactoidTransactionsResponse entryTransaction = response.getResult();
        Assert.assertNotNull(entryTransaction);
        Assert.assertNotNull(entryTransaction.getTxId());
        Assert.assertNotNull(entryTransaction.getStatus());
        Assert.assertNotNull(entryTransaction.getBlockDateString());
        Assert.assertNotNull(entryTransaction.getTransactionDateString());
        Assert.assertTrue(entryTransaction.getTransactionDate() > 0);
        Assert.assertTrue(entryTransaction.getBlockDate() > 0);
    }

    @Test
    public void testChainHead() throws FactomException.ClientException {
        FactomResponse<ChainHeadResponse> response = factomdClient.chainHead("000000000000000000000000000000000000000000000000000000000000000a").join();
        assertValidResponse(response);

        ChainHeadResponse chainHead = response.getResult();
        Assert.assertNotNull(chainHead);
        Assert.assertNotNull(chainHead.getChainHead());
    }

    @Test
    public void testAdminBlockByKeyMerkleRoot() throws FactomException.ClientException {
        FactomResponse<AdminBlockResponse> response = factomdClient.adminBlockByKeyMerkleRoot("343ffe17ca3b9775196475380feb91768e8cb3ceb888f2d617d4f0c2cc84a26a").join();
        assertValidResponse(response);

        AdminBlockResponse adminBlock = response.getResult();
        Assert.assertNotNull(adminBlock);
        Assert.assertNotNull(adminBlock.getRawData());
        Assert.assertNotNull(adminBlock.getAdminBlock());
        Assert.assertNotNull(adminBlock.getAdminBlock().getLookUpHash());
        Assert.assertNotNull(adminBlock.getAdminBlock().getBackReferenceHash());

        Assert.assertNotNull(adminBlock.getAdminBlock().getHeader());
        Assert.assertNotNull(adminBlock.getAdminBlock().getHeader().getAdminChainId());
        Assert.assertNotNull(adminBlock.getAdminBlock().getHeader().getChainId());
        Assert.assertNotNull(adminBlock.getAdminBlock().getHeader().getHeaderExpansionArea());
        Assert.assertNotNull(adminBlock.getAdminBlock().getHeader().getPreviousBackReferenceHash());
        Assert.assertTrue(adminBlock.getAdminBlock().getHeader().getMessageCount() > 0);
        Assert.assertTrue(adminBlock.getAdminBlock().getHeader().getBodySize() > 0);
        Assert.assertTrue(adminBlock.getAdminBlock().getHeader().getDirectoryBlockHeight() > 0);
        Assert.assertTrue(adminBlock.getAdminBlock().getHeader().getHeaderExpansionSize() >= 0);

        Assert.assertNotNull(adminBlock.getAdminBlock().getAdminBlockEntries());
        Assert.assertFalse(adminBlock.getAdminBlock().getAdminBlockEntries().isEmpty());
        Assert.assertNotNull(adminBlock.getAdminBlock().getAdminBlockEntries().get(0).getIdentityAdminChainId());
        //Assert.assertNotNull(adminBlock.getAdminBlock().getAdminBlockEntries().get(0).getMinuteNumber());
        Assert.assertNotNull(adminBlock.getAdminBlock().getAdminBlockEntries().get(0).getPreviousDirectoryBlockSignature());
        Assert.assertNotNull(adminBlock.getAdminBlock().getAdminBlockEntries().get(0).getPreviousDirectoryBlockSignature().getPublicKey());
        Assert.assertNotNull(adminBlock.getAdminBlock().getAdminBlockEntries().get(0).getPreviousDirectoryBlockSignature().getSignature());
    }

    @Test
    public void testCommitChain() {
    }

    @Test
    public void testCommitEntry() {
    }

    @Test
    public void testCurrentMinute() {
        FactomResponse<CurrentMinuteResponse> response = factomdClient.currentMinute().join();
        assertValidResponse(response);

        CurrentMinuteResponse currentMinute = response.getResult();
        Assert.assertTrue(currentMinute.getCurrentTime() > 0);
        Assert.assertTrue(currentMinute.getCurrentBlockStartTime() > 0);
        Assert.assertTrue(currentMinute.getCurrentMinuteStartTime() > 0);
        Assert.assertTrue(currentMinute.getDirectoryBlockHeight() > 0);
        Assert.assertTrue(currentMinute.getDirectoryBlockInSeconds() > 0);
        Assert.assertTrue(currentMinute.getLeaderHeight() > 0);
        Assert.assertTrue(currentMinute.getMinute() >= 0);
        Assert.assertFalse("Something is wrong with factom test network. Stall detected", currentMinute.isStallDetected());
    }

    @Test
    public void testDirectoryBlockByHeight() throws FactomException.ClientException {
        FactomResponse<DirectoryBlockHeightResponse> response = factomdClient.directoryBlockByHeight(39251).join();
        assertValidResponse(response);

        DirectoryBlockHeightResponse directoryBlock = response.getResult();
        Assert.assertNotNull(directoryBlock);
        Assert.assertNotNull(directoryBlock.getRawData());
        Assert.assertNotNull(directoryBlock.getDirectoryBlock());
        Assert.assertNotNull(directoryBlock.getDirectoryBlock().getDirectoryBlockHash());
        Assert.assertNotNull(directoryBlock.getDirectoryBlock().getKeyMR());

        Assert.assertNotNull(directoryBlock.getDirectoryBlock().getDirectoryBlockEntries());
        Assert.assertFalse(directoryBlock.getDirectoryBlock().getDirectoryBlockEntries().isEmpty());
        Assert.assertNotNull(directoryBlock.getDirectoryBlock().getDirectoryBlockEntries().get(0).getChainId());
        Assert.assertNotNull(directoryBlock.getDirectoryBlock().getDirectoryBlockEntries().get(0).getKeyMR());

        Assert.assertNotNull(directoryBlock.getDirectoryBlock().getHeader());
        Assert.assertNotNull(directoryBlock.getDirectoryBlock().getHeader().getChainId());
        Assert.assertNotNull(directoryBlock.getDirectoryBlock().getHeader().getBodyMR());
        Assert.assertNotNull(directoryBlock.getDirectoryBlock().getHeader().getPreviousKeyMR());
        Assert.assertNotNull(directoryBlock.getDirectoryBlock().getHeader().getPreviousFullHash());
        Assert.assertTrue(directoryBlock.getDirectoryBlock().getHeader().getBlockCount() > 0);
        Assert.assertTrue(directoryBlock.getDirectoryBlock().getHeader().getDirectoryBlockHeight() > 0);
        Assert.assertTrue(directoryBlock.getDirectoryBlock().getHeader().getTimestamp() > 0);
        Assert.assertTrue(directoryBlock.getDirectoryBlock().getHeader().getVersion() == 0);
    }

    @Test
    public void testDirectoryBlockByKeyMerkleRoot() throws FactomException.ClientException {
        FactomResponse<DirectoryBlockResponse> response = factomdClient.directoryBlockByKeyMerkleRoot("549e01d3815b521038fa3d29808dea1b06105e4dfc8c75d165033f7c1a08ee25").join();
        assertValidResponse(response);

        DirectoryBlockResponse directoryBlock = response.getResult();
        Assert.assertNotNull(directoryBlock);
        Assert.assertNotNull(directoryBlock.getEntryblockList());
        Assert.assertFalse(directoryBlock.getEntryblockList().isEmpty());
        Assert.assertNotNull(directoryBlock.getEntryblockList().get(0).getChainId());
        Assert.assertNotNull(directoryBlock.getEntryblockList().get(0).getKeyMR());

        Assert.assertNotNull(directoryBlock.getHeader());
        Assert.assertNotNull(directoryBlock.getHeader().getPreviousBlockKeyMR());
        Assert.assertTrue(directoryBlock.getHeader().getSequenceNumber() > 0);
        Assert.assertTrue(directoryBlock.getHeader().getTimestamp() > 0);
    }

    @Test
    public void testDirectoryBlockHead() throws FactomException.ClientException {
        FactomResponse<DirectoryBlockHeadResponse> response = factomdClient.directoryBlockHead().join();
        assertValidResponse(response);

        DirectoryBlockHeadResponse directoryBlockHead = response.getResult();
        Assert.assertNotNull(directoryBlockHead);
        Assert.assertNotNull(directoryBlockHead.getKeyMR());
    }

    @Test
    public void testEntryCreditBlockByHeight() throws FactomException.ClientException {
        FactomResponse<EntryCreditBlockResponse> response = factomdClient.entryCreditBlockByHeight(41565).join();
        assertValidResponse(response);

        EntryCreditBlockResponse entryCreditBlock = response.getResult();
        Assert.assertNotNull(entryCreditBlock);
        Assert.assertNotNull(entryCreditBlock.getRawData());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getBody());

        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getChainId());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getBodyHash());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getEntryCreditChainId());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getPreviousFullHash());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getPreviousHeaderHash());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getHeaderExpansionArea());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getDirectoryBlockHeight());
        Assert.assertTrue(entryCreditBlock.getEntryCreditBlock().getHeader().getBodySize() > 0);

        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getBody().getEntries());
        Assert.assertFalse(entryCreditBlock.getEntryCreditBlock().getBody().getEntries().isEmpty());
        Assert.assertTrue(entryCreditBlock.getEntryCreditBlock().getBody().getEntries().get(0).getNumber() > 0);
        /* find block with this data
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getBody().getEntries().get(0).getEntryHash());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getBody().getEntries().get(0).getEntryCreditPublicKey());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getBody().getEntries().get(0).getSiganture());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getBody().getEntries().get(0).getMilliTime());
        Assert.assertTrue(entryCreditBlock.getEntryCreditBlock().getBody().getEntries().get(0).getCredits() > 0);
        Assert.assertTrue(entryCreditBlock.getEntryCreditBlock().getHeader().getDirectoryBlockHeight() > 0);
        */
    }

    @Test
    public void testEntry() {
    }

    @Test
    public void testEntryBlockByKeyMerkleRoot() throws FactomException.ClientException {
        FactomResponse<EntryBlockResponse> response = factomdClient.entryBlockByKeyMerkleRoot("6272ed26f9e1b416f4b93672507270ecd5253c27af6365345ad0d1f68c23be91").join();
        assertValidResponse(response);

        EntryBlockResponse entryBlock = response.getResult();
        Assert.assertNotNull(entryBlock);
        Assert.assertNotNull(entryBlock.getEntryList());
        Assert.assertFalse(entryBlock.getEntryList().isEmpty());
        Assert.assertNotNull(entryBlock.getEntryList().get(0).getEntryHash());
        Assert.assertTrue(entryBlock.getEntryList().get(0).getTimestamp() > 0);

        Assert.assertNotNull(entryBlock.getHeader());
        Assert.assertNotNull(entryBlock.getHeader().getChainId());
        Assert.assertNotNull(entryBlock.getHeader().getPreviousKeyMR());
        Assert.assertTrue(entryBlock.getHeader().getBlockSequenceNumber() >= 0);
        Assert.assertTrue(entryBlock.getHeader().getDirectoryBlockHeight() > 0);
    }

    @Test
    public void testEntryCreditBalance() throws FactomException.ClientException {
        FactomResponse<EntryCreditBalanceResponse> response = factomdClient.entryCreditBalance(new Address(EC_PUBLIC_ADDRESS)).join();
        assertValidResponse(response);
        EntryCreditBalanceResponse entryCreditBalance = response.getResult();
        Assert.assertNotNull(entryCreditBalance);
        if (entryCreditBalance.getBalance() < 30) {
            fail(String.format("EC balance (%d) of %s is too low for other tests to run properly. Please go to %s to top up the balance", entryCreditBalance.getBalance(), new Address(EC_PUBLIC_ADDRESS), "https://faucet.factoid.org/"));
        }
    }

    @Test
    public void testEntryCreditBlock() throws FactomException.ClientException {
        FactomResponse<EntryCreditBlockResponse> response = factomdClient.entryCreditBlock("1064d10c37cca2c6ea819dd69586b5c1fef10f8b956f5960a12a36604ea31f8d").join();
        assertValidResponse(response);

        EntryCreditBlockResponse entryCreditBlock = response.getResult();
        Assert.assertNotNull(entryCreditBlock);
        Assert.assertNotNull(entryCreditBlock.getRawData());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getBody());

        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getChainId());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getBodyHash());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getEntryCreditChainId());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getPreviousFullHash());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getPreviousHeaderHash());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getHeaderExpansionArea());
        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getHeader().getDirectoryBlockHeight());
        Assert.assertTrue(entryCreditBlock.getEntryCreditBlock().getHeader().getBodySize() > 0);

        Assert.assertNotNull(entryCreditBlock.getEntryCreditBlock().getBody().getEntries());
        Assert.assertFalse(entryCreditBlock.getEntryCreditBlock().getBody().getEntries().isEmpty());
        Assert.assertTrue(entryCreditBlock.getEntryCreditBlock().getBody().getEntries().get(0).getNumber() > 0);
    }

    @Test
    public void testEntryCreditRate() throws FactomException.ClientException {
        FactomResponse<EntryCreditRateResponse> response = factomdClient.entryCreditRate().join();
        assertValidResponse(response);

        EntryCreditRateResponse creditRate = response.getResult();
        Assert.assertNotNull(creditRate);
        Assert.assertTrue(creditRate.getRate() > 0);
    }

    @Test
    public void testFactoidBalance() throws FactomException.ClientException {
        FactomResponse<FactoidBalanceResponse> response = factomdClient.factoidBalance(new Address(FCT_PUBLIC_ADDRESS)).join();
        assertValidResponse(response);

        FactoidBalanceResponse factoidBalance = response.getResult();
        Assert.assertNotNull(factoidBalance);
        if (factoidBalance.getBalance() < 30) {
            fail(String.format("Factoid balance (%d) of %s is too low for other tests to run properly. Please go to %s to top up the balance", factoidBalance.getBalance(), new Address(FCT_PUBLIC_ADDRESS), "https://faucet.factoid.org/"));
        }
    }

    @Test
    public void testFactoidBlock() throws FactomException.ClientException {
        FactomResponse<FactoidBlockResponse> response = factomdClient.factoidBlock("d1b0eb5b8045c055272dd5816527f9e9f0506f928392fe0b21b7cbca61580427").join();
        assertValidResponse(response);

        FactoidBlockResponse factoidBlock = response.getResult();
        Assert.assertNotNull(factoidBlock);
        Assert.assertNotNull(factoidBlock.getFactoidBlock());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getBodyMR());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getChainId());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getKeyMR());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getLedgerKeyMR());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getPreviousKeyMR());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getPreviousLedgerKeyMR());
        Assert.assertTrue(factoidBlock.getFactoidBlock().getDirectoryBlockHeight() > 0);
        Assert.assertTrue(factoidBlock.getFactoidBlock().getExchangeRate() > 0);

        Assert.assertNotNull(factoidBlock.getFactoidBlock().getTransactions());
        Assert.assertFalse(factoidBlock.getFactoidBlock().getTransactions().isEmpty());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getTransactions().get(0).getTxId());
        Assert.assertTrue(factoidBlock.getFactoidBlock().getTransactions().get(0).getMilliTimestamp() > 0);
    }

    @Test
    public void testFactoidSubmit() throws FactomException.ClientException {
        FactomResponse<FactoidSubmitResponse> response = factomdClient.factoidSubmit("0201656165ce80010001bde2204ef701663ab1b4a32eb31e658e471bdea484da4dfd9fd48caa09c53b09c2db6ebd84404f028c1f0d877cf15922169c2f05f3e972649159e4b6a9842e0a0b6da2da63b501e1061b8dcf36c1bb89b004533153478afc6cbc50813804c7f9b9540c3261a3bf42234354dc9082176e7ba56b5cfcf77e1eedf51ef21bc3dc1e8dc20637e7b75ca89144986410550df85b472622d4d792eb007d326b8138dea03224316a1ce504").join();
        assertValidResponse(response);

        FactoidSubmitResponse factoidSubmit = response.getResult();
        Assert.assertNotNull(factoidSubmit);
        Assert.assertNotNull(factoidSubmit.getTxId());
        Assert.assertEquals("Successfully submitted the transaction", factoidSubmit.getMessage());
    }

    @Test
    public void testFactoidBlockByHeight() throws FactomException.ClientException {
        FactomResponse<FactoidBlockResponse> response = factomdClient.factoidBlockByHeight(100).join();
        assertValidResponse(response);

        FactoidBlockResponse factoidBlock = response.getResult();
        Assert.assertNotNull(factoidBlock);
        Assert.assertNotNull(factoidBlock.getFactoidBlock());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getBodyMR());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getChainId());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getKeyMR());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getLedgerKeyMR());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getPreviousKeyMR());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getPreviousLedgerKeyMR());
        Assert.assertTrue(factoidBlock.getFactoidBlock().getDirectoryBlockHeight() > 0);
        Assert.assertTrue(factoidBlock.getFactoidBlock().getExchangeRate() > 0);

        Assert.assertNotNull(factoidBlock.getFactoidBlock().getTransactions());
        Assert.assertFalse(factoidBlock.getFactoidBlock().getTransactions().isEmpty());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getTransactions().get(0).getTxId());
        Assert.assertTrue(factoidBlock.getFactoidBlock().getTransactions().get(0).getMilliTimestamp() > 0);
    }

    @Test
    public void testHeights() throws FactomException.ClientException {
        FactomResponse<HeightsResponse> response = factomdClient.heights().join();
        assertValidResponse(response);

        HeightsResponse heights = response.getResult();
        Assert.assertNotNull(heights);
        Assert.assertTrue(heights.getEntryHeight() > 0);
        Assert.assertTrue(heights.getLeaderHeight() > 0);
        Assert.assertTrue(heights.getEntryBlockHeight() > 0);
        Assert.assertTrue(heights.getDirectoryBlockHeight() > 0);
    }

    @Test
    public void testPendingEntries() throws FactomException.ClientException {
        FactomResponse<PendingEntriesResponse> response = factomdClient.pendingEntries(41579).join();
        assertValidResponse(response);

        PendingEntriesResponse pendingEntries = response.getResult();
        Assert.assertNotNull(pendingEntries);
    }

    @Test
    public void testPendingTransactions() throws FactomException.ClientException {
        FactomResponse<PendingTransactionsResponse> response = factomdClient.pendingTransactions(41579).join();
        assertValidResponse(response);

        PendingTransactionsResponse pendingEntries = response.getResult();
        Assert.assertNotNull(pendingEntries);
        Assert.assertFalse(pendingEntries.isEmpty());
        PendingTransactionsResponse.PendingTransaction pendingTransaction = pendingEntries.get(0);
        Assert.assertTrue(pendingTransaction.getFees() > 0);
        Assert.assertNotNull(pendingTransaction.getTransactionId());
        Assert.assertNotNull(pendingTransaction.getStatus());
    }

    @Test
    public void testProperties() throws FactomException.ClientException {
        FactomResponse<PropertiesResponse> response = factomdClient.properties().join();
        assertValidResponse(response);

        PropertiesResponse properties = response.getResult();
        Assert.assertNotNull(properties.getFactomdApiVersion());
        Assert.assertNotNull(properties.getFactomdVersion());
    }

    @Test
    public void testRawData() throws FactomException.ClientException {
        FactomResponse<RawDataResponse> response = factomdClient.rawData("e84cabc86d26b548da00d28ff48bb458610b255b762be44597e5b971bd75f8d7").join();
        assertValidResponse(response);

        RawDataResponse rawData = response.getResult();
        Assert.assertNotNull(rawData.getData());
    }

    @Test
    public void testReceipt() throws FactomException.ClientException {
        FactomResponse<ReceiptResponse> response = factomdClient.receipt("1f9f3030597cca90365d2852c5e498cf412939076265c553133fe3adf415465b").join();
        assertValidResponse(response);

        ReceiptResponse receipt = response.getResult();
        Assert.assertNotNull(receipt);
        Assert.assertNotNull(receipt.getReceipt());
        Assert.assertNotNull(receipt.getReceipt().getDirectoryBlockKeyMR());
        Assert.assertNotNull(receipt.getReceipt().getEntryBlockKeyMR());
        Assert.assertNotNull(receipt.getReceipt().getEntry());
        Assert.assertNotNull(receipt.getReceipt().getEntry().getEntryHash());
        Assert.assertNotNull(receipt.getReceipt().getMerkleBranch());
        Assert.assertFalse(receipt.getReceipt().getMerkleBranch().isEmpty());
        Assert.assertNotNull(receipt.getReceipt().getMerkleBranch().get(0).getLeft());
        Assert.assertNotNull(receipt.getReceipt().getMerkleBranch().get(0).getRight());
        Assert.assertNotNull(receipt.getReceipt().getMerkleBranch().get(0).getTop());
    }

    @Test
    public void testRevealChain() throws FactomException.ClientException {
        FactomResponse<RevealResponse> response = factomdClient.revealChain("00527dd7ee7168adf6f4e1493bfb8f5dddb42a325371d3b2df3f490eb62b9aa10100120004268808420004616263640004313233343132333461626364").join();
        assertValidResponse(response);

        RevealResponse reveal = response.getResult();
        Assert.assertNotNull(reveal);
        Assert.assertEquals("Entry Reveal Success", reveal.getMessage());
        Assert.assertNotNull(reveal.getChainId());
        Assert.assertNotNull(reveal.getEntryHash());
    }

    @Test
    public void testRevealEntry() throws FactomException.ClientException {
        FactomResponse<RevealResponse> response = factomdClient.revealEntry("00527dd7ee7168adf6f4e1493bfb8f5dddb42a325371d3b2df3f490eb62b9aa10100080002cd90000290cdabcdef").join();
        assertValidResponse(response);

        RevealResponse reveal = response.getResult();
        Assert.assertNotNull(reveal);
        Assert.assertEquals("Entry Reveal Success", reveal.getMessage());
        Assert.assertNotNull(reveal.getChainId());
        Assert.assertNotNull(reveal.getEntryHash());
    }

    @Test
    public void testSendRawMessage() throws FactomException.ClientException {

    }

    @Test
    public void testTransactions() throws FactomException.ClientException {
        FactomResponse<TransactionResponse> response = factomdClient.transaction("092ebeb0865d8bb06a059aabc58eeafa92efa10d477150600937b635cd2805f4").join();
        assertValidResponse(response);

        TransactionResponse transaction = response.getResult();
        Assert.assertNotNull(transaction);
        Assert.assertTrue(transaction.getIncludedInDirectorybBockHeight() > 0);
        Assert.assertNotNull(transaction.getIncludedInDirectoryBlock());
        Assert.assertNotNull(transaction.getIncludedIntTansactionBlock());
        Assert.assertNotNull(transaction.getFactoidTransaction());
        Assert.assertTrue(transaction.getFactoidTransaction().getMilliTimestamp() > 0);

        Assert.assertNotNull(transaction.getFactoidTransaction().getInputs());
        Assert.assertFalse(transaction.getFactoidTransaction().getInputs().isEmpty());
        Assert.assertNotNull(transaction.getFactoidTransaction().getInputs().get(0).getAddress());
        Assert.assertNotNull(transaction.getFactoidTransaction().getInputs().get(0).getUserAddress());
        Assert.assertTrue(transaction.getFactoidTransaction().getInputs().get(0).getAmount() > 0);

        Assert.assertNotNull(transaction.getFactoidTransaction().getOutputs());
        Assert.assertFalse(transaction.getFactoidTransaction().getOutputs().isEmpty());
        Assert.assertNotNull(transaction.getFactoidTransaction().getOutputs().get(0).getAddress());
        Assert.assertNotNull(transaction.getFactoidTransaction().getOutputs().get(0).getUserAddress());
        Assert.assertTrue(transaction.getFactoidTransaction().getOutputs().get(0).getAmount() > 0);

        Assert.assertNotNull(transaction.getFactoidTransaction().getOutputEntryCredits());
        Assert.assertFalse(transaction.getFactoidTransaction().getOutputEntryCredits().isEmpty());
        Assert.assertNotNull(transaction.getFactoidTransaction().getOutputEntryCredits().get(0).getAddress());
        Assert.assertNotNull(transaction.getFactoidTransaction().getOutputEntryCredits().get(0).getUserAddress());
        Assert.assertTrue(transaction.getFactoidTransaction().getOutputEntryCredits().get(0).getAmount() > 0);

        Assert.assertNotNull(transaction.getFactoidTransaction().getRedeemConditionDataStructures());
        Assert.assertFalse(transaction.getFactoidTransaction().getRedeemConditionDataStructures().isEmpty());
        Assert.assertNotNull(transaction.getFactoidTransaction().getRedeemConditionDataStructures().get(0));

        Assert.assertNotNull(transaction.getFactoidTransaction().getSignatureBlocks());
        Assert.assertFalse(transaction.getFactoidTransaction().getSignatureBlocks().isEmpty());
        Assert.assertNotNull(transaction.getFactoidTransaction().getSignatureBlocks().get(0));
    }
}
