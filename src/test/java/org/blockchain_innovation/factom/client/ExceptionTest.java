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

import org.blockchain_innovation.factom.client.data.FactomException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class ExceptionTest {

    private final FactomdClient factomdClient = new FactomdClient();
    private final WalletdClient walletdClient = new WalletdClient();

    @Before
    public void setup() throws MalformedURLException {
        factomdClient.setUrl(new URL("http://136.144.204.97:8088/v2"));
        walletdClient.setUrl(new URL("http://136.144.204.97:8089/v2"));
    }

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
            Assert.assertEquals("Internal error", response.getRpcErrorResponse().getError().getMessage());
            Assert.assertEquals("wallet: Transaction name was not found", response.getRpcErrorResponse().getError().getData());
        }
    }
}
