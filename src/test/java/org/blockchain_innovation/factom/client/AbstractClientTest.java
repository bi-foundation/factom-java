package org.blockchain_innovation.factom.client;

import org.junit.Assert;

public class AbstractClientTest {

    protected final static String EC_PUBLIC_KEY = System.getProperty("FACTOM_CLIENT_TEST_EC_PUBLIC_KEY", "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv");

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
