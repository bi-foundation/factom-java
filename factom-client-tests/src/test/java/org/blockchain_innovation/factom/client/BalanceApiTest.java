package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.api.model.FAT.Token;
import org.blockchain_innovation.factom.client.api.model.FAT.TokenBalanceResponse;
import org.junit.Assert;
import org.junit.Test;

public class BalanceApiTest extends AbstractClientTest {

    @Test
    public void checkBalance(){
        TokenBalanceResponse balance = balanceApi.getBalance(FCT_PUBLIC_ADDRESS).join();
        Assert.assertNotNull(balance);
        for(Token token : balance.getBalance()){
            System.out.println(token.getName() + ": " + token.getAmount());
        }
    }
}
