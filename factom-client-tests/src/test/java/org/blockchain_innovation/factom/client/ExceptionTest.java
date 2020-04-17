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
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CurrentMinuteResponse;
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.FactomdClientImpl;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ExceptionTest extends AbstractClientTest {

    @Test
    public void testIncorrectCommitChainMessage() throws FactomException.ClientException {

        factomdClient.commitChain("incorrect-message").exceptionally(throwable -> {
                    FactomException.RpcErrorException e = (FactomException.RpcErrorException) throwable.getCause();
                    FactomResponse<?> response = e.getFactomResponse();
                    Assert.assertEquals(400, response.getHTTPResponseCode());
                    Assert.assertEquals("Bad Request", response.getHTTPResponseMessage());
                    Assert.assertNotNull(response.getRpcErrorResponse());
                    Assert.assertNotNull(response.getRpcErrorResponse().getError());
                    Assert.assertEquals(-32602, response.getRpcErrorResponse().getError().getCode());
                    Assert.assertEquals("Invalid params", response.getRpcErrorResponse().getError().getMessage());
                    Assert.assertEquals("Invalid Commit Chain", response.getRpcErrorResponse().getError().getData());
                    Assert.assertNull(response.getResult());
                    return null;
                }
        ).join();

    }

    @Test
    public void testIncorrectTxName() throws FactomException.ClientException {

        walletdClient.composeTransaction("incorrect-tx-name").exceptionally(throwable -> {
                    FactomException.RpcErrorException e = (FactomException.RpcErrorException) throwable.getCause();
                    FactomResponse<?> response = e.getFactomResponse();
                    Assert.assertEquals(400, response.getHTTPResponseCode());
                    Assert.assertEquals("Bad Request", response.getHTTPResponseMessage());
                    Assert.assertNotNull(response.getRpcErrorResponse());
                    Assert.assertNotNull(response.getRpcErrorResponse().getError());
                    Assert.assertEquals(-32603, response.getRpcErrorResponse().getError().getCode());
                    Assert.assertEquals("Internal error", response.getRpcErrorResponse().getError().getMessage());
                    Assert.assertEquals("wallet: Transaction name was not found", response.getRpcErrorResponse().getError().getData());
                    return null;
                }
        ).join();
    }

    @Test(expected = FactomRuntimeException.AssertionException.class)
    public void testCommitNullChain() {
        walletdClient.composeChain(null, new Address(null)).join();
    }

    @Test(expected = FactomException.ClientException.class)
    public void testNoSettings() throws FactomException.ClientException {
        FactomdClientImpl factomdClient = new FactomdClientImpl();
        factomdClient.properties();
    }

    @Ignore
    @Test(expected = FactomException.ClientException.class)
    public void testInvalidURLSettings() throws IOException {
        Properties properties = getProperties();
        RpcSettings settings = new RpcSettingsImpl(RpcSettings.SubSystem.WALLETD, properties);
        FactomdClientImpl factomdClient = new FactomdClientImpl();
        factomdClient.setSettings(settings);
        FactomResponse<CurrentMinuteResponse> currentMinuteResponseFactomResponse = factomdClient.currentMinute().join();
    }


    @Test(expected = FactomRuntimeException.AssertionException.class)
    public void testWrongAddressType() {
        Address correctAddress = new Address(EC_SECRET_ADDRESS);
        Address invalidAddress1 = new Address("Es3Y6U6H1Pfg4wYag8VMtRZEGuEJnfkJ2ZuSyCVcQKweB6y4WvVH");
        Address invalidAddress2 = new Address("Es3Y6U6H1Pfg4wYag8VMtRZEGuEJnfkJ2ZuSyCVcQKweB6y4WvGD");
        List<Address> addresses = Arrays.asList(correctAddress, invalidAddress1, invalidAddress2);
        walletdClient.importAddresses(addresses);
    }

    @Test(expected = FactomRuntimeException.AssertionException.class)
    public void testWrongAddressTypeCompose() {
        Address address = new Address(FCT_PUBLIC_ADDRESS.substring(0, FCT_PUBLIC_ADDRESS.length() - 1));
        walletdClient.composeEntry(new Entry(), address);
    }

    @Test(expected = FactomRuntimeException.AssertionException.class)
    public void testWrongAddressTransaction() {
        Address address = new Address(FCT_PUBLIC_ADDRESS.substring(0, FCT_PUBLIC_ADDRESS.length() - 1));
        walletdClient.transactionsByAddress(address).join();
    }

    @Test(expected = FactomRuntimeException.AssertionException.class)
    public void testWrongAddressFactoidBalance() {
        Address address = new Address(EC_PUBLIC_ADDRESS);
        factomdClient.factoidBalance(address);
    }

    @Test(expected = FactomRuntimeException.AssertionException.class)
    public void testWrongAddressCreditBalance() {
        Address address = new Address(EC_SECRET_ADDRESS);
        factomdClient.entryCreditBalance(address);
    }

}
