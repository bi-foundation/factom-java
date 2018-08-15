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
import org.blockchain_innovation.factom.client.data.model.response.AdminBlockResponse;
import org.blockchain_innovation.factom.client.data.model.response.ChainHeadResponse;
import org.blockchain_innovation.factom.client.data.model.response.DirectoryBlockHeadResponse;
import org.blockchain_innovation.factom.client.data.model.response.DirectoryBlockResponse;
import org.blockchain_innovation.factom.client.data.model.response.EntryTransactionsResponse;
import org.blockchain_innovation.factom.client.data.model.response.FactoidTransactionsResponse;
import org.blockchain_innovation.factom.client.data.model.response.HeightsResponse;
import org.blockchain_innovation.factom.client.data.model.response.PropertiesResponse;
import org.blockchain_innovation.factom.client.data.model.response.RawDataResponse;
import org.blockchain_innovation.factom.client.data.model.response.TransactionResponse;
import org.blockchain_innovation.factom.client.data.model.rpc.Callback;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ClientAsyncTest {


    private final FactomdClientAsync client = new FactomdClientAsync();


    @Before
    public void setup() throws MalformedURLException {
        client.setUrl(new URL("http://136.144.204.97:8088/v2"));
    }

    @Test
    public void testAdminBlockHeight() throws FactomException.ClientException, ExecutionException, InterruptedException {
        Future<FactomResponse<AdminBlockResponse>> future = client.adminBlockByHeight(10);
        FactomResponse<AdminBlockResponse> response = future.get();
        Assert.assertNotNull(response);
    }

}
