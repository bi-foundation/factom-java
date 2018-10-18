package org.blockchain_innovation.factom.client;

import org.junit.Assert;
import org.junit.Test;

public class BalanceApiTest extends AbstractClientTest {

    @Test
    public void checkBalance(){
        long balance = balanceApi.getBalance(FCT_PUBLIC_ADDRESS).join().getResult().getBalance();
        Assert.assertTrue(balance >= 0);
    }
}
