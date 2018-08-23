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
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.FactomdClient;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

public class ExceptionTest extends AbstractClientTest {

    @Test
    public void testIncorrectCommitChainMessage() throws FactomException.ClientException {
        try {
            factomdClient.commitChain("incorrect-message");
        } catch (FactomException.RpcErrorException e) {
            FactomResponse response = e.getFactomResponse();
            Assert.assertEquals(400, response.getHTTPResponseCode());
            Assert.assertEquals("Bad Request", response.getHTTPResponseMessage());
            Assert.assertNotNull(response.getRpcErrorResponse());
            Assert.assertNotNull(response.getRpcErrorResponse().getError());
            Assert.assertEquals(-32602, response.getRpcErrorResponse().getError().getCode());
            Assert.assertEquals("Invalid params", response.getRpcErrorResponse().getError().getMessage());
            Assert.assertEquals("Invalid Commit Chain", response.getRpcErrorResponse().getError().getData());
            Assert.assertNull(response.getResult());
        }
    }

    @Test
    public void testIncorrectTxName() throws FactomException.ClientException {
        try {
            walletdClient.composeTransaction("incorrect-tx-name");
        } catch (FactomException.RpcErrorException e) {
            FactomResponse response = e.getFactomResponse();
            Assert.assertEquals(400, response.getHTTPResponseCode());
            Assert.assertEquals("Bad Request", response.getHTTPResponseMessage());
            Assert.assertNotNull(response.getRpcErrorResponse());
            Assert.assertNotNull(response.getRpcErrorResponse().getError());
            Assert.assertEquals(-32603, response.getRpcErrorResponse().getError().getCode());
            Assert.assertEquals("Internal error", response.getRpcErrorResponse().getError().getMessage());
            Assert.assertEquals("wallet: Transaction name was not found", response.getRpcErrorResponse().getError().getData());
        }
    }

    @Test(expected = FactomException.ClientException.class)
    public void testCommitNullChain() throws FactomException.ClientException {
        walletdClient.composeChain(null, "");
    }

    @Test(expected = FactomException.ClientException.class)
    public void testNoSettings() throws FactomException.ClientException {
        FactomdClient factomdClient = new FactomdClient();
        factomdClient.properties();
    }

    @Test(expected = FactomException.ClientException.class)
    public void testInvalidURLSettings() throws FactomException.ClientException {
        Properties properties = new Properties();
        RpcSettings settings = new RpcSettingsImpl(RpcSettings.SubSystem.WALLETD, properties);
        FactomdClient factomdClient = new FactomdClient();
        factomdClient.setSettings(settings);
        factomdClient.properties();
    }
}
