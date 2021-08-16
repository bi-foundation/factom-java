package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.RCD;
import org.blockchain_innovation.factom.client.api.model.Transaction;
import org.blockchain_innovation.factom.client.api.model.response.factomd.FactoidSubmitResponse;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.model.types.RCDType;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class OfflineTransactionTest extends AbstractClientTest {
    private static final AddressKeyConversions CONVERSIONS = new AddressKeyConversions();


    @Test
    public void testTransactionMarshalling() {
        final Address secretInputFctAddress = Address.fromString(FCT_SECRET_ADDRESS);
        final Address secretOutputFctAddress1 = Address.fromString("Fs1eDBmMknhc2kJZwCjQrgWeWWkjqBWGfDsLcLfUesxVEHv6PxmU");
        final Address secretOutputFctAddress2 = Address.fromString("Fs1xBWD9ocnxNu3ssPF8NEU9PV8HQ83UMth5zr9cD7WEFoBj4qdb");
        final Address publicOutputFctAddress1 = CONVERSIONS.addressToPublicAddress(secretOutputFctAddress1);
        final Address publicOutputFctAddress2 = CONVERSIONS.addressToPublicAddress(secretOutputFctAddress2);
        Assert.assertNotNull(publicOutputFctAddress1);
        Assert.assertNotNull(publicOutputFctAddress2);

        final Transaction transaction = new Transaction.Builder()
                .addInput(2, secretInputFctAddress)
                .addOutput(1, publicOutputFctAddress1)
                .addOutput(1, publicOutputFctAddress2)
                .timestamp(Instant.ofEpochMilli(123))
                .build();

        Assert.assertEquals("169efc0781241a794d8474ac55857acacc7fd9b614cd9abdffa240a35da9a43e", transaction.getId());
        Assert.assertEquals("9336f2c9d1069ca897337a2d2d730e3f2cb1222ef7450f472d69bd69a7801ebc", transaction.getHash());
        Assert.assertEquals(123, transaction.getTimestamp());

        Assert.assertEquals(1, transaction.getInputs().size());
        final RCD rcd = transaction.getInputs().get(0).getRcd();
        Assert.assertNotNull(rcd);
        Assert.assertEquals(RCDType.TYPE_1, rcd.getType());
        Assert.assertEquals(CONVERSIONS.addressToPublicAddress(secretInputFctAddress), rcd.getAddress());
        Assert.assertEquals(CONVERSIONS.addressToPublicAddress(secretInputFctAddress).getValue(), CONVERSIONS.keyToAddress(rcd.getPublicKey(), AddressType.FACTOID_PUBLIC));
        Assert.assertArrayEquals(new byte[]{8, 17, 95, -106, -21, -75, -29, 90, -100, -128, 109, -23, -49, -2, 76, -103, 69, 90, 12, 90, 28, 68, -118, 29, 46, -74, -44, 106, -69, 126, -88, -26}, rcd.hash());
        Assert.assertArrayEquals(new byte[]{68, 41, -73, -111, 97, -30, 46, -109, -110, -54, -16, 58, -105, -112, -57, -55, -99, 73, -59, -11, 55, 117, 89, -37, 83, 22, -83, -108, -113, -92, 38, 10}, rcd.getPublicKey());

        final byte[] marshalled = transaction.marshal();
        // Didn't want to include the whole bytearray, so lets test a few bytes
        Assert.assertEquals(206, marshalled.length);
        Assert.assertEquals(2, marshalled[0]);
        Assert.assertEquals(123, marshalled[6]);
        Assert.assertEquals(-75, marshalled[16]);
        Assert.assertEquals(-67, marshalled[199]);

    }


    // TODO: 13/08/2021 This is an IT, move to separate class
    @Test
    public void testSubmittingBackAndForthTransactions() {
        final Address secretInputFctAddress = Address.fromString(FCT_SECRET_ADDRESS);
        final Address secretOutputFctAddress1 = Address.fromString("Fs1eDBmMknhc2kJZwCjQrgWeWWkjqBWGfDsLcLfUesxVEHv6PxmU");
        final Address secretOutputFctAddress2 = Address.fromString("Fs1xBWD9ocnxNu3ssPF8NEU9PV8HQ83UMth5zr9cD7WEFoBj4qdb");
        final Address publicOutputFctAddress1 = CONVERSIONS.addressToPublicAddress(secretOutputFctAddress1);
        final Address publicOutputFctAddress2 = CONVERSIONS.addressToPublicAddress(secretOutputFctAddress2);
        System.out.println(String.format("Public Address1: %s", publicOutputFctAddress1));
        System.out.println(String.format("Public Address2: %s", publicOutputFctAddress2));

        final Transaction transaction1 = new Transaction.Builder()
                .addInput(15200000, secretInputFctAddress)
                .addOutput(15100000, publicOutputFctAddress1)
                .build();

        final Transaction transaction2 = new Transaction.Builder()
                .addInput(15100000, secretOutputFctAddress1)
                .addOutput(15000000, Address.fromString(FCT_PUBLIC_ADDRESS))
                .build();


        final CompletableFuture<FactoidSubmitResponse> responseFuture1 = submitTransaction(transaction1);
        final CompletableFuture<FactoidSubmitResponse> responseFuture2 = submitTransaction(transaction2);

        try {
            CompletableFuture<Void> combined = CompletableFuture.allOf(responseFuture1, responseFuture2);
            combined.get();
        } catch (Exception e) {
            failOnException(e);
        }
        Assert.assertTrue(responseFuture1.isDone());
        Assert.assertTrue(responseFuture2.isDone());
    }

    private CompletableFuture<FactoidSubmitResponse> submitTransaction(Transaction transaction) {
        return factomdClient.factoidSubmit(Encoding.HEX.encode(transaction.marshal()))
                .handle((submitResponse2, throwable) -> {
                    failOnException(throwable);

                    final String txIdFromResponse2 = submitResponse2.getResult().getTxId();
                    Assert.assertEquals(txIdFromResponse2, transaction.getId());
                    System.out.println(String.format("Transaction id : %s, inputs(%s), outputs(%s)", txIdFromResponse2,
                            transaction.getInputs().stream().map(input ->
                                    input.getAddress().getValue()).collect(Collectors.joining(",")),
                            transaction.getFctOutputs().stream().map(output ->
                                    output.getAddress().getValue()).collect(Collectors.joining(","))
                    ));
                    return submitResponse2.getResult();
                });
    }

    private void failOnException(Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
            Assert.fail(throwable.getMessage());
        }
    }
}
