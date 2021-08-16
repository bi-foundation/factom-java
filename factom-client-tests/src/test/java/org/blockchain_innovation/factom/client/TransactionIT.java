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
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryCreditRateResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.FactoidSubmitResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.AddressResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ExecutedTransactionResponse;
import org.blockchain_innovation.factom.client.api.model.response.walletd.TransactionResponse;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionIT extends AbstractClientTest {

    private static final String TRANSACTION_NAME = "TransactionName" + System.currentTimeMillis();
    private static FactomResponse<EntryCreditRateResponse> entryCreditRateResponse;
    private static FactomResponse<AddressResponse> toAddressResponse;
    private static FactomResponse<ComposeTransactionResponse> composeTransactionResponse;


    @Test
    public void _01_getExchangeRate() throws FactomException.ClientException {
        entryCreditRateResponse = factomdClient.entryCreditRate().join();
        assertValidResponse(entryCreditRateResponse);

        Assert.assertTrue(entryCreditRateResponse.getResult().getRate() > 0);
    }

    @Test
    public void _02_newTransaction() throws FactomException.ClientException {
        FactomResponse<TransactionResponse> newTransactionResponse = walletdClient.newTransaction(TRANSACTION_NAME).join();
        assertValidResponse(newTransactionResponse);

        Assert.assertNotNull(newTransactionResponse.getResult().getTxId());
        Assert.assertEquals(TRANSACTION_NAME, newTransactionResponse.getResult().getName());
        Assert.assertEquals(0, newTransactionResponse.getResult().getTotalEntryCreditOutputs());
        Assert.assertEquals(0, newTransactionResponse.getResult().getTotalInputs());
        Assert.assertEquals(0, newTransactionResponse.getResult().getTotalOutputs());
        Assert.assertTrue(newTransactionResponse.getResult().getInputs() == null || newTransactionResponse.getResult().getInputs().isEmpty());
        Assert.assertTrue(newTransactionResponse.getResult().getOutputs() == null || newTransactionResponse.getResult().getOutputs().isEmpty());
        Assert.assertTrue(newTransactionResponse.getResult().getEntryCreditOutputs() == null || newTransactionResponse.getResult().getEntryCreditOutputs().isEmpty());
    }

    @Test
    public void _03_newToAddress() throws FactomException.ClientException {
        toAddressResponse = walletdClient.generateEntryCreditAddress().join();
        assertValidResponse(toAddressResponse);

        Assert.assertNotNull(toAddressResponse.getResult().getPublicAddress());
        Assert.assertNotNull(toAddressResponse.getResult().getSecret());
    }

    @Test
    public void _04_addInput() throws FactomException.ClientException {
        long fctCost = calculateCost();

        Address address = new Address(FCT_PUBLIC_ADDRESS);
        FactomResponse<ExecutedTransactionResponse> response = walletdClient.addInput(TRANSACTION_NAME, address, fctCost).join();
        assertValidResponse(response);

        Assert.assertFalse(response.getResult().getInputs().isEmpty());
        Assert.assertEquals(FCT_PUBLIC_ADDRESS, response.getResult().getInputs().get(0).getAddress());
        Assert.assertEquals(fctCost, response.getResult().getInputs().get(0).getAmount());
    }

    @Test
    public void _05_addEntryCreditOutput() throws FactomException.ClientException {
        String toAddress = toAddressResponse.getResult().getPublicAddress();
        Address address = new Address(toAddress);
        long fctCost = calculateCost();

        FactomResponse<TransactionResponse> response = walletdClient.addEntryCreditOutput(TRANSACTION_NAME, address, fctCost).join();
        assertValidResponse(response);

        Assert.assertFalse(response.getResult().getEntryCreditOutputs().isEmpty());
        Assert.assertEquals(toAddress, response.getResult().getEntryCreditOutputs().get(0).getAddress());
        Assert.assertEquals(fctCost, response.getResult().getEntryCreditOutputs().get(0).getAmount());
    }

    @Test
    public void _06_addFee() throws FactomException.ClientException {
        Address address = new Address(FCT_PUBLIC_ADDRESS);
        FactomResponse<ExecutedTransactionResponse> response = walletdClient.addFee(TRANSACTION_NAME, address).join();
        assertValidResponse(response);

        Assert.assertNotNull(response.getResult().getInputs().isEmpty());
    }

    @Test
    public void _07_signTransaction() throws FactomException.ClientException {
        FactomResponse<ExecutedTransactionResponse> response = walletdClient.signTransaction(TRANSACTION_NAME).join();
        assertValidResponse(response);

        Assert.assertTrue(response.getResult().isSigned());
    }

    @Test
    public void _08_composeTransaction() throws FactomException.ClientException {
        composeTransactionResponse = walletdClient.composeTransaction(TRANSACTION_NAME).join();
        assertValidResponse(composeTransactionResponse);

        Assert.assertNotNull(composeTransactionResponse.getResult().getParams().getTransaction());
    }

    @Test
    public void _09_submitTransaction() throws FactomException.ClientException {
        String transaction = composeTransactionResponse.getResult().getParams().getTransaction();
        FactomResponse<FactoidSubmitResponse> response = factomdClient.factoidSubmit(transaction).join();
        assertValidResponse(response);

        Assert.assertNotNull(response.getResult().getTxId());
        Assert.assertEquals("Successfully submitted the transaction", response.getResult().getMessage());
    }

    private long calculateCost() {
        int entryCreditAmount = 1000;
        long entryCreditRate = entryCreditRateResponse.getResult().getRate();
        return Math.round(entryCreditAmount * entryCreditRate + .49);
    }
}

