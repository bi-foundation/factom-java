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
import org.blockchain_innovation.factom.client.impl.*;
import org.blockchain_innovation.factom.client.impl.settings.RpcSettingsImpl;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AbstractClientTest {

    protected static final String EC_PUBLIC_ADDRESS = System.getProperty("FACTOM_CLIENT_TEST_EC_PUBLIC_ADDRESS", "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv");
    protected static final String EC_SECRET_ADDRESS = System.getProperty("FACTOM_CLIENT_TEST_EC_SECRET_ADDRESS", "Es3Y6U6H1Pfg4wYag8VMtRZEGuEJnfkJ2ZuSyCVcQKweB6y4WvGH");
    protected static final String FCT_PUBLIC_ADDRESS = System.getProperty("FACTOM_CLIENT_TEST_FACTOID_PUBLIC_ADDRESS", "FA2ZrcG8xkwWWNfdMRw5pGNjMPEkLaxRGqacvzfLS6TGHEHZqAA4");
    protected static final String FCT_SECRET_ADDRESS = System.getProperty("FACTOM_CLIENT_TEST_FACTOID_SECRET_ADDRESS", "Fs1jQGc9GJjyWNroLPq7x6LbYQHveyjWNPXSqAvCEKpETNoTU5dP");
    protected static final Map<String, String> publicPrivateKeyMap = new HashMap<>();

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    protected final FactomdClientImpl factomdClient = new FactomdClientImpl();
    protected final WalletdClientImpl walletdClient = new WalletdClientImpl();
    protected final EntryApiImpl entryClient = new EntryApiImpl();
    protected final EntryApiImpl offlineEntryClient = new EntryApiImpl();
    protected final OfflineWalletdClientImpl offlineWalletdClient = new OfflineWalletdClientImpl();

    @Before
    public void setup() throws IOException {

        factomdClient.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.FACTOMD, getProperties()));
        walletdClient.setSettings(new RpcSettingsImpl(RpcSettings.SubSystem.WALLETD, getProperties()));

        entryClient.setFactomdClient(factomdClient);
        entryClient.setWalletdClient(walletdClient);

        offlineEntryClient.setFactomdClient(factomdClient);
        offlineEntryClient.setWalletdClient(offlineWalletdClient);

        publicPrivateKeyMap.put("EC2vm7dwnW3dXPQuDwahNdEswYK8w6JTab6xWBGuuTZQcGRMFVcj", "Es3kpax8Eef56QQpv2ZiXdy2VoJz9haiWYVfhu7UBFvphFXtCaUb");
        publicPrivateKeyMap.put("EC2MWUKvkzPKz1FJBgdQNm6N3BoCBD61Be6KqJFeP5EnuqwVv8T8", "Es4Hs2YFockNBCo3n2viDXokLQf21mSFVokYqedswej5Qi7bFd2r");
        publicPrivateKeyMap.put("EC2AkLsR4ZWxNvzGco73M6n2VspsvK12A3xRBqBj2J7Yq77AKWZd", "Es2aNJSS6RVBiBDdkbKm81ayCkyX6sbx6uXfW4PihbbAPLzoNLDB");
        publicPrivateKeyMap.put("EC2YA3d1ZLN2nPvbpRvKrdqdyGNDNehJ5T91QD2LDMtA7e7Bsg3W", "Es3thh34Lgp9kkxjiVSDpAXgaT7zr9dC9BDNbn2qjqAhWArnCN1c");
        publicPrivateKeyMap.put("FA3pJ4f86QJB9s9gUNWp8xrLWAwuxS4KtS62QG3gDEGYG5QDrFeH", "Fs2akEAe8rfHo3VKsPzJ3PHwgEnGgkG8SKxvqX65NMZtCiWWp5zc");
        publicPrivateKeyMap.put("FA3dSc43dbxGdSZ5zPSneZczgiLaKK5PF6DHtcK48vA7SxnnpnG5", "Fs27pk4KZQAoPar65dMM1u8opu8E2NkQp2teg4YxffVS5HgA6jRK");
        publicPrivateKeyMap.put("FA2GfDwQLJTHWxQ9mQqmi8JG4UPPfzP5uwgFNSpAhqxzoAxKSQv8", "Fs1U4xDbNNLGKAJEVewWDcp2g8j4bQv4JYR3oZ5hMjTdEqsJUenE");
        publicPrivateKeyMap.put("FA2HRV5a4Myw7nHNowr7KpFVr7btzztxwFc8GENSFdyLMsPXFVdA", "Fs1zFi7QFkGKoXvh85dUugt9iqAmsSrgVxu9dtsvwhW6XCEe929c");
    }

    protected Properties getProperties() throws IOException {
        Properties properties = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("settings.properties");
        properties.load(is);
        is.close();
        return properties;
    }

    protected void assertValidResponse(FactomResponse<?> factomResponse) {
        Assert.assertNotNull(factomResponse);
        Assert.assertNotNull(factomResponse.getRpcResponse());
        Assert.assertEquals(200, factomResponse.getHTTPResponseCode());
        Assert.assertNull(factomResponse.getRpcErrorResponse());
        Assert.assertFalse(factomResponse.hasErrors());
    }
}
