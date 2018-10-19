package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.api.model.FAT.Inputs;
import org.blockchain_innovation.factom.client.api.model.FAT.Outputs;
import org.blockchain_innovation.factom.client.api.model.FAT.TokenTransactionResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TransactionApiTest extends AbstractClientTest{

    @Test
    public void sendToken(){
        List<Inputs> inputs = new ArrayList<>();
        List<Outputs> outputs = new ArrayList<>();
        inputs.add(new Inputs("address1", 100));
        outputs.add(new Outputs("address2", 100));
        List<String> externalIds = new ArrayList<>();
        externalIds.add("sig1");
        externalIds.add("sig2");
        TokenTransactionResponse tokenTransactionResponse = transactionApi.sendToken(inputs, outputs, 1000342532, "thissaltisweird", externalIds).join();
        Assert.assertNotNull(tokenTransactionResponse);
        System.err.println(tokenTransactionResponse.getTxHash());
    }

}
