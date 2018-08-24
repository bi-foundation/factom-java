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
import org.blockchain_innovation.factom.client.api.settings.RpcSettings;
import org.blockchain_innovation.factom.client.impl.FactomdClient;
import org.blockchain_innovation.factom.client.impl.WalletdClient;
import org.blockchain_innovation.factom.client.impl.json.gson.GsonConverter;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class AbstractClientTest {

    protected final static String EC_PUBLIC_KEY = System.getProperty("FACTOM_CLIENT_TEST_EC_PUBLIC_KEY", "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv");
    protected final static String EC_SECRET_KEY = System.getProperty("FACTOM_CLIENT_TEST_EC_SECRET_KEY", "Es3Y6U6H1Pfg4wYag8VMtRZEGuEJnfkJ2ZuSyCVcQKweB6y4WvGH");
    protected final static String FACTOID_PUBLIC_KEY = System.getProperty("FACTOM_CLIENT_TEST_FACTOID_PUBLIC_KEY", "FA2ZrcG8xkwWWNfdMRw5pGNjMPEkLaxRGqacvzfLS6TGHEHZqAA4");

    protected final FactomdClient factomdClient = new FactomdClient();
    protected final WalletdClient walletdClient = new WalletdClient();

    @Before
    public void setup() throws IOException {

        //// FIXME: 06/08/2018 Only needed now to iinit the converter
        GsonConverter conv = new GsonConverter();

        factomdClient.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, getProperties()));
        walletdClient.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.WALLETD, getProperties()));
    }

    protected Properties getProperties() throws IOException {
        Properties properties = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("settings.properties");
        properties.load(is);
        is.close();
        return properties;
    }

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    protected void assertValidResponse(FactomResponse<?> factomResponse) {
        Assert.assertNotNull(factomResponse);
        Assert.assertNotNull(factomResponse.getRpcResponse());
        Assert.assertEquals(200, factomResponse.getHTTPResponseCode());
        Assert.assertNull(factomResponse.getRpcErrorResponse());
        Assert.assertFalse(factomResponse.hasErrors());
    }
}
