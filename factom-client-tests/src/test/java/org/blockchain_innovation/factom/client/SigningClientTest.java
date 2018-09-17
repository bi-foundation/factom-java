package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;
import org.blockchain_innovation.factom.client.impl.EntryApiOfflineSigningImpl;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class SigningClientTest extends AbstractClientTest {

    private SigningTestClientOfflineSigningImpl signingClient = new SigningTestClientOfflineSigningImpl();

    @Test
    public void entryOperations() {
        String expectedChainId = "be27b8476ed4840cca115c8f0a1e3783d9f9b35fd008470a55f2db9097a4073e";

        Chain chain = chain();
        EntryOperations entryOperations = new EntryOperations();
        byte[] chainId = entryOperations.calculateChainId(chain.getFirstEntry().getExternalIds());

        String chainIdHex = Encoding.HEX.encode(chainId);
        Assert.assertEquals(expectedChainId, chainIdHex);
    }

    @Test
    public void testChainCommit() {
        String expectedChainCommit = "000160ddcd307eb8a99a8a770edcd78b3490d70d746b4dad76e966319a6a8bcb4d47fad2b89595c9e3fbe05ac8286a417719e76282b6b97123e183e79dfed97e2bdbd24645b0d0523b9c0a06ce1ce6a33925b8a22d2c35dcc214af965977fe8cb7a4ad906b3b170bf48167f7f868d6163b4afb49a357829d9bdb2de092374d357424e2318c6f894afff4cf749867b5df9046525b6d0ca0b691e04efc5f0cd76e09a268ec59958cde063bd7f7d2c2cd563d46615ab3f87f2b5b8b201c2791286cf8dde74958904b00";
        Chain chain = chain();

        String composeChainCommit = signingClient.composeChainCommit(chain, EC_PUBLIC_ADDRESS, EC_SECRET_ADDRESS);
        System.out.println("composeChainCommit = " + composeChainCommit);
        Assert.assertEquals(expectedChainCommit, composeChainCommit);
    }

    @Test
    public void testChainReveal() {
        String expectedChainReveal = "00be27b8476ed4840cca115c8f0a1e3783d9f9b35fd008470a55f2db9097a4073e001d000d45787465726e616c2049442031000c53696e67656420434841494e436f6e74656e74";

        Chain chain = chain();

        String composeChainReveal = signingClient.composeChainReveal(chain);
        System.out.println("composeChainReveal = " + composeChainReveal);
        Assert.assertEquals(expectedChainReveal, composeChainReveal);
    }

    @Test
    public void testEntryCommit() {
        String expectedEntryCommit = "000160ddcd307ebb97838651bd9d3428b77f22c99138a05f15ed3716fa776a148e054213a09f6701f48167f7f868d6163b4afb49a357829d9bdb2de092374d357424e2318c6f894a004b1623c13aaafb421ae949e1c78291233fb9e28c505a3f0ec6e37d7e1f199a0e8762e09f3e479fea3ac097db0e7064dd38c5d53fd538d3d2b8259128dcbf09";
        Entry entry = entry("be27b8476ed4840cca115c8f0a1e3783d9f9b35fd008470a55f2db9097a4073e");

        String composeEntryCommit = signingClient.composeEntryCommit(entry, EC_PUBLIC_ADDRESS, EC_SECRET_ADDRESS);
        System.out.println("composeEntryCommit = " + composeEntryCommit);
        Assert.assertEquals(expectedEntryCommit, composeEntryCommit);
    }

    @Test
    public void testEntryReveal() {
        String expectedRevealEntry = "00be27b8476ed4840cca115c8f0a1e3783d9f9b35fd008470a55f2db9097a4073e002c000d45787465726e616c2049442031000d45787465726e616c2049442032000c53696e67656420454e545259456e74727920436f6e74656e74";
        Entry entry = entry("be27b8476ed4840cca115c8f0a1e3783d9f9b35fd008470a55f2db9097a4073e");

        String composeEntryReveal = signingClient.composeEntryReveal(entry);
        System.out.println("composeEntryReveal = " + composeEntryReveal);
        Assert.assertEquals(expectedRevealEntry, composeEntryReveal);
    }

    private Chain chain() {
        Chain.Builder builder = new Chain.Builder();
        builder.setContent("Content");
        builder.addExternalIds("External ID 1");
        builder.addExternalIds("Singed CHAIN");
        return builder.build();
    }

    private Entry entry(String chainId) {
        Entry entry = new Entry();
        List<String> externalIds = Arrays.asList("External ID 1", "External ID 2", "Singed ENTRY");
        entry.setExternalIds(externalIds);
        entry.setContent("Entry Content");
        entry.setChainId(chainId);
        return entry;
    }

    class SigningTestClientOfflineSigningImpl extends EntryApiOfflineSigningImpl {
        protected byte[] currentTimeMillis() {
            Instant timeInstant = Instant.parse("2018-01-10T02:01:40.222Z");
            long now = timeInstant.toEpochMilli();
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(now);
            byte[] holder = buffer.array();
            byte[] resp = new byte[]{holder[2], holder[3], holder[4], holder[5], holder[6], holder[7]};
            return resp;
        }
    }
}
