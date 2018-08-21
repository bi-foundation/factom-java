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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sun.org.apache.regexp.internal.RE;
import org.blockchain_innovation.factom.client.data.FactomException;
import org.blockchain_innovation.factom.client.data.model.response.factomd.AdminBlockResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.ChainHeadResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.DirectoryBlockHeadResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.DirectoryBlockHeightResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.DirectoryBlockResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.EntryBlockResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.EntryCreditBalanceResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.EntryCreditBlockResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.EntryCreditRateResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.EntryTransactionResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.FactoidBalanceResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.FactoidBlockResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.FactoidTransactionsResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.HeightsResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.PendingEntriesResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.PendingTransactionsResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.PropertiesResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.RawDataResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.ReceiptResponse;
import org.blockchain_innovation.factom.client.data.model.response.factomd.TransactionResponse;
import org.blockchain_innovation.factom.client.data.model.rpc.RpcMethod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class FactomdClientTest extends AbstractClientTest {

    private final FactomdClient client = new FactomdClient();

    @Before
    public void setup() throws MalformedURLException {
        client.setUrl(new URL("http://136.144.204.97:8088/v2"));
    }

    @Test
    public void manualRequest() throws FactomException.ClientException {
        Assert.assertNull(new FactomRequestImpl(null).toString());

        FactomRequestImpl factomRequest = new FactomRequestImpl(RpcMethod.PROPERTIES.toRequestBuilder().id(5).build());
        Assert.assertNotNull(factomRequest.toString());
        FactomResponse<Map> response = client.exchange(factomRequest, Map.class);
        assertValidResponse(response);
        Assert.assertEquals(5, response.getRpcResponse().getId());
        Assert.assertEquals("2.0", response.getRpcResponse().getJsonrpc());
    }

    @Test
    public void testAdminBlockByHeight() throws FactomException.ClientException {
        FactomResponse<AdminBlockResponse> response = client.adminBlockByHeight(10);
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
    public void testAckTransactions() {

    }

    @Test
    public void testAckEntryTransactions() throws FactomException.ClientException {
        FactomResponse<EntryTransactionResponse> response = client.ackEntryTransactions("e96cca381bf25f6dd4dfdf9f7009ff84ee6edaa3f47f9ccf06d2787482438f4b");
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
        FactomResponse<FactoidTransactionsResponse> response = client.ackFactoidTransactions("0f4e09dd02236880b3bb7d22cda6c8f29fd4fabc02aba1229b2d8680b9043a2a");
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
        FactomResponse<ChainHeadResponse> response = client.chainHead("000000000000000000000000000000000000000000000000000000000000000a");
        assertValidResponse(response);

        ChainHeadResponse chainHead = response.getResult();
        Assert.assertNotNull(chainHead);
        Assert.assertNotNull(chainHead.getChainHead());
    }

    @Test
    public void testAdminBlockByKeyMerkleRoot() throws FactomException.ClientException {
        FactomResponse<AdminBlockResponse> response = client.adminBlockByKeyMerkleRoot("343ffe17ca3b9775196475380feb91768e8cb3ceb888f2d617d4f0c2cc84a26a");
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
    public void testDirectoryBlockByHeight() throws FactomException.ClientException {
        FactomResponse<DirectoryBlockHeightResponse> response = client.directoryBlockByHeight(39251);
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
        FactomResponse<DirectoryBlockResponse> response = client.directoryBlockByKeyMerkleRoot("549e01d3815b521038fa3d29808dea1b06105e4dfc8c75d165033f7c1a08ee25");
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
        FactomResponse<DirectoryBlockHeadResponse> response = client.directoryBlockHead();
        assertValidResponse(response);

        DirectoryBlockHeadResponse directoryBlockHead = response.getResult();
        Assert.assertNotNull(directoryBlockHead);
        Assert.assertNotNull(directoryBlockHead.getKeyMR());
    }

    @Test
    public void testEntryCreditBlockByHeight() throws FactomException.ClientException {
        FactomResponse<EntryCreditBlockResponse> response = client.entryCreditBlockByHeight(41565);
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

    // TODO find entry block key mr
    // @Test
    public void testEntryBlockByKeyMerkleRoot() throws FactomException.ClientException {
        FactomResponse<EntryBlockResponse> response = client.entryBlockByKeyMerkleRoot("a39efedcabc916833799bf1380664eb9d15dafddfe49b5165972dd80cf6f9bcb");
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
        Assert.assertTrue(entryBlock.getHeader().getBlockSequenceNumber() > 0);
        Assert.assertTrue(entryBlock.getHeader().getDirectoryBlockHeight() > 0);
    }

    @Test
    public void testEntryCreditBalance() throws FactomException.ClientException {
        FactomResponse<EntryCreditBalanceResponse> response = client.entryCreditBalance(EC_PUBLIC_KEY);
        assertValidResponse(response);
        EntryCreditBalanceResponse entryCreditBalance = response.getResult();
        Assert.assertNotNull(entryCreditBalance);
        if (entryCreditBalance.getBalance() < 30) {
            fail(String.format("EC balance (%d) of %s is too low for other tests to run properly. Please go to %s to top up the balance", entryCreditBalance.getBalance(), EC_PUBLIC_KEY, "https://faucet.factoid.org/"));
        }
    }

    @Test
    public void testEntryCreditBlock() throws FactomException.ClientException {
        FactomResponse<EntryCreditBlockResponse> response = client.entryCreditBlock("1064d10c37cca2c6ea819dd69586b5c1fef10f8b956f5960a12a36604ea31f8d");
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
        FactomResponse<EntryCreditRateResponse> response = client.entryCreditRate();
        assertValidResponse(response);

        EntryCreditRateResponse creditRate = response.getResult();
        Assert.assertNotNull(creditRate);
        Assert.assertTrue(creditRate.getRate() > 0);
    }

    @Test
    public void testFactoidBalance() throws FactomException.ClientException {
        FactomResponse<FactoidBalanceResponse> response = client.factoidBalance(FACTOID_PUBLIC_KEY);
        assertValidResponse(response);

        FactoidBalanceResponse factoidBalance = response.getResult();
        Assert.assertNotNull(factoidBalance);
        if (factoidBalance.getBalance() < 30) {
            fail(String.format("Factoid balance (%d) of %s is too low for other tests to run properly. Please go to %s to top up the balance", factoidBalance.getBalance(), FACTOID_PUBLIC_KEY, "https://faucet.factoid.org/"));
        }
    }

    @Test
    public void testFactoidBlock() throws FactomException.ClientException {
        FactomResponse<FactoidBlockResponse> response = client.factoidBlock("d1b0eb5b8045c055272dd5816527f9e9f0506f928392fe0b21b7cbca61580427");
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
        Assert.assertTrue(factoidBlock.getFactoidBlock().getExchangeRate()> 0);

        Assert.assertNotNull(factoidBlock.getFactoidBlock().getTransactions());
        Assert.assertFalse(factoidBlock.getFactoidBlock().getTransactions().isEmpty());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getTransactions().get(0).getTxId());
        Assert.assertTrue(factoidBlock.getFactoidBlock().getTransactions().get(0).getMilliTimestamp() > 0);
    }

    @Test
    public void testFactoidSubmit() {

    }

    @Test
    public void testFactoidBlockByHeight() throws FactomException.ClientException {
        FactomResponse<FactoidBlockResponse> response = client.factoidBlockByHeight(100);
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
        Assert.assertTrue(factoidBlock.getFactoidBlock().getExchangeRate()> 0);

        Assert.assertNotNull(factoidBlock.getFactoidBlock().getTransactions());
        Assert.assertFalse(factoidBlock.getFactoidBlock().getTransactions().isEmpty());
        Assert.assertNotNull(factoidBlock.getFactoidBlock().getTransactions().get(0).getTxId());
        Assert.assertTrue(factoidBlock.getFactoidBlock().getTransactions().get(0).getMilliTimestamp() > 0);
    }

    @Test
    public void testHeights() throws FactomException.ClientException {
        FactomResponse<HeightsResponse> response = client.heights();
        assertValidResponse(response);

        HeightsResponse heights = response.getResult();
        Assert.assertNotNull(heights);
        Assert.assertTrue(heights.getEntryHeight() > 0);
        Assert.assertTrue(heights.getLeaderHeight() > 0);
        Assert.assertTrue(heights.getEntryBlockHeight() > 0);
        Assert.assertTrue(heights.getDirectoryBlockHeight() > 0);
    }

    // TODO  test with valid height
    // @Test
    public void testPendingEntries() throws FactomException.ClientException {
        FactomResponse<PendingEntriesResponse> response = client.pendingEntries(41579);
        assertValidResponse(response);

        PendingEntriesResponse pendingEntries = response.getResult();
        Assert.assertNotNull(pendingEntries);
        Assert.assertNotNull(pendingEntries.getChainId());
        Assert.assertNotNull(pendingEntries.getEntryHash());
        Assert.assertNotNull(pendingEntries.getStatus());

    }

    @Test
    public void testPendingTransactions() throws FactomException.ClientException {
        FactomResponse<PendingTransactionsResponse> response = client.pendingTransactions(41579);
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
        FactomResponse<PropertiesResponse> response = client.properties();
        assertValidResponse(response);

        PropertiesResponse properties = response.getResult();
        Assert.assertNotNull(properties.getFactomdApiVersion());
        Assert.assertNotNull(properties.getFactomdVersion());
    }

    @Test
    public void testRawData() throws FactomException.ClientException {
        FactomResponse<RawDataResponse> response = client.rawData("e84cabc86d26b548da00d28ff48bb458610b255b762be44597e5b971bd75f8d7");
        assertValidResponse(response);

        RawDataResponse rawData = response.getResult();
        Assert.assertNotNull(rawData.getData());
    }

    // TODO test with valid hash
    // @Test
    public void testReceipt() throws FactomException.ClientException {
        FactomResponse<ReceiptResponse> response = client.receipt("0ae2ab2cf543eed52a13a5a405bded712444cc8f8b6724a00602e1c8550a4ec2");
        assertValidResponse(response);

        ReceiptResponse receipt = response.getResult();
        Assert.assertNotNull(receipt);
        Assert.assertNotNull(receipt.getBitcoinBlockHash());
        Assert.assertNotNull(receipt.getBitcoinTransactionHash());
        Assert.assertNotNull(receipt.getDirectoryBlockKeyMR());
        Assert.assertNotNull(receipt.getEntryBlockKeyMR());
        Assert.assertNotNull(receipt.getEntry());
        Assert.assertNotNull(receipt.getEntry().getEntryHash());

    }

    @Test
    public void testRevealChain() {
    }

    @Test
    public void testRevealEntry() {
    }

    @Test
    public void testSendRawMessage() {
    }

    @Test
    public void testTransactions() throws FactomException.ClientException {
        FactomResponse<TransactionResponse> response = client.transaction("092ebeb0865d8bb06a059aabc58eeafa92efa10d477150600937b635cd2805f4");
        assertValidResponse(response);

        TransactionResponse transaction= response.getResult();
        Assert.assertNotNull(transaction);
        Assert.assertTrue(transaction.getIncludedInDirectorybBockHeight() > 0);
        Assert.assertNotNull(transaction.getIncludedInDirectoryBlock());
        Assert.assertNotNull(transaction.getIncludedIntTansactionBlock());
        Assert.assertNotNull(transaction.getFactoidTransaction());
        Assert.assertTrue(transaction.getFactoidTransaction().getMilliTimestamp() >0);

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
