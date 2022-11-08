package org.blockchain_innovation.accumulate.factombridge;

import org.apache.commons.lang3.StringUtils;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryBlockResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.EntryResponse;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FactomConversionTest extends AbstractClientTest {

    private static final String chainId = "1e70009ca2a8c6f14821207549eb50e36c615ad575bcdb34e90dd066569e5e2d";

    @Test
    public void _01_testEntriesUpTilKeyMRFactom() throws ExecutionException, InterruptedException {
        final CompletableFuture<List<EntryResponse>> result = entryClient.entriesUpTilKeyMR("4135d3e4732cd3c0dd1ecbb36fe001daf9dc3b89b135d6359d8d8136fb39681e");
        result.get().forEach(entryResponse -> {
            System.out.println(entryResponse);
        });
    }

    @Test
    public void _02_testGetAllEntriesBlocks() throws InterruptedException, ExecutionException {

        final CompletableFuture<List<EntryBlockResponse>> future = entryClient.allEntryBlocks(chainId);
        future.thenAccept(entryResponses -> {
            Assert.assertEquals(11332, entryResponses.size());

        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            Assert.fail("allEntries failed");
            return null;
        });
        int count = 0;
        while (count < 300) {
            count++;
            Thread.sleep(1000);
            if (future.isCompletedExceptionally()) {
                Assert.fail("allEntries failed");
            } else if (future.isDone()) {
                break;
            }
        }
        future.cancel(true);

    }

    @Test
    public void _05_testGetAllEntries() throws InterruptedException {
        final CompletableFuture<List<EntryResponse>> future = entryClient.allEntries(chainId);
        future.thenAccept(entryResponses -> {
            Assert.assertEquals(11332, entryResponses.size());
            final EntryResponse entryResponse = entryResponses.get(0);
            Assert.assertNotNull(entryResponse.getContent());
            Assert.assertNotNull(entryResponse.getExtIds());
            Assert.assertFalse(entryResponse.getExtIds().isEmpty());
            Assert.assertNotNull(entryResponse.getExtIds().get(0));
            Assert.assertTrue(StringUtils.startsWith(new String(Hex.decode(entryResponse.getContent())), "{\"inputs\":"));
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            Assert.fail("allEntries failed");
            return null;
        });
        int count = 0;
        while (count < 100) {
            count++;
            Thread.sleep(1000);
            if (future.isCompletedExceptionally()) {
                Assert.fail("allEntries failed");
            } else if (future.isDone()) {
                break;
            }
        }
        future.cancel(true);

    }
}
