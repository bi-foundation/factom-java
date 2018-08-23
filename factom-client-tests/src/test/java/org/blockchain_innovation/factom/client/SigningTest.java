package org.blockchain_innovation.factom.client;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import org.blockchain_innovation.factom.client.api.Digests;
import org.blockchain_innovation.factom.client.api.Encoding;
import org.blockchain_innovation.factom.client.api.FactomException;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitEntryResponse;
import org.blockchain_innovation.factom.client.impl.ops.ByteOperations;
import org.blockchain_innovation.factom.client.impl.ops.EntryOperations;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.junit.Test;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.err;
import static java.lang.System.out;

public class SigningTest extends AbstractClientTest {

    private EntryOperations entryOperations = new EntryOperations();

    @Test
    public void composeEntryCommit() throws Exception {
        String entryCreditPublicKey = "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv";
        String secret = "Es3Y6U6H1Pfg4wYag8VMtRZEGuEJnfkJ2ZuSyCVcQKweB6y4WvGH";

        EntryOperations entryOperations = new EntryOperations();
        Entry entry = entry();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 1 byte version
            byte[] version = {0};
            outputStream.write(version);

            // 6 byte milliTimestamp
            byte[] millis = currentTimeMillis();
            outputStream.write(millis);

            // 32 byte Entry Hash
            String entryHash = entryOperations.calculateEntryHash(entry.getExternalIds(), entry.getContent(), entry.getChainId());
            byte[] entryHashBytes = Encoding.HEX.decode(entryHash);
            outputStream.write(entryHashBytes);

            // 1 byte number of entry credits to pay
            byte cost = entryCost(entry);
            outputStream.write(cost);

            // 32 byte Entry Credit Address Public Key + 64 byte Signature
            byte[] message = outputStream.toByteArray();
            byte[] signature = sign(message, secret);
            byte[] entryCreditKey = getKey(entryCreditPublicKey);

            outputStream.write(entryCreditKey);
            outputStream.write(signature);

            byte[] entryParams = outputStream.toByteArray();
            System.out.println("entryParams = " + entryParams);
            System.out.println("entryParams = " + Encoding.UTF_8.encode(entryParams));
            System.out.println("entryParams = " + Encoding.HEX.encode(entryParams));
        }
    }

    @Test
    public void composeChain() throws Exception {
        EntryOperations entryOperations = new EntryOperations();
        ByteOperations byteOperations = new ByteOperations();
        String entryCreditPublicKey = "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv";
        String secret = "Es3Y6U6H1Pfg4wYag8VMtRZEGuEJnfkJ2ZuSyCVcQKweB6y4WvGH";

        Chain chain = chain();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 1 byte version
            byte[] version = {0};
            outputStream.write(version);

            // 6 byte milliTimestamp
            byte[] millis = currentTimeMillis();
            outputStream.write(millis);

            // 32 byte ChainID Hash
            Chain.Entry firstEntry = chain.getFirstEntry();
            byte[] chainId = Encoding.HEX.decode(entryOperations.calculateChainId(firstEntry.getExternalIds()));
            // double sha256 hash of ChainID
            byte[] chainIdHash = Digests.SHA_256.doubleDigest(chainId);
            outputStream.write(chainIdHash);

            // 32 byte Weld; sha256(sha256(EntryHash + ChainID))
            byte[] entryHash = Encoding.HEX.decode(entryOperations.calculateEntryHash(firstEntry.getExternalIds(), firstEntry.getContent(), Encoding.HEX.encode(chainId)));
            byte[] weld = byteOperations.concat(entryHash, chainId);
            byte[] entryChainWeld =Digests.SHA_256.doubleDigest(weld);
            outputStream.write(entryChainWeld);

            // 32 byte Entry Hash of the First Entry
            outputStream.write(entryHash);

            // 1 byte number of Entry Credits to pay
            byte cost = chainCost(chain);
            outputStream.write(cost);

            // 32 byte Entry Credit Address Public Key + 64 byte Signature
            byte[] message = outputStream.toByteArray();
            byte[] signature = sign(message, secret);
            byte[] entryCreditKey = getKey(entryCreditPublicKey);

            outputStream.write(entryCreditKey);
            outputStream.write(signature);

            byte[] entryParams = outputStream.toByteArray();
            System.out.println("entryParams = " + entryParams);
            System.out.println("entryParams = " + Encoding.UTF_8.encode(entryParams));
            System.out.println("entryParams = " + Encoding.HEX.encode(entryParams));
        }
    }



    @Test
    public void testChain() throws FactomException.ClientException {
        FactomResponse<CommitChainResponse> response = factomdClient.commitChain("0001656758e2457c67a99e7e8f25dbb625040789dda87f66431f91fee25a214dd8fdb91eec0df397acf73670c195dcb970a926753372f77b26d96e8f6ce3fde882b0051c22f99736b7cd9f204aeb4300d45f1e451dacffcbd50eca8148fbfac151ae13c6ae9c9d0bf48167f7f868d6163b4afb49a357829d9bdb2de092374d357424e2318c6f894a49be00e624283ff6c4b40e97b42aee1720f3711d999c251b1d22bf0305b695eec42f98d8dde00d2527e7e6f6e50e09d361b2c5702e3aeb3fd67a1af852a31b0b");
        assertValidResponse(response);
    }
    @Test
    public void testEntry() throws FactomException.ClientException {
        FactomResponse<CommitEntryResponse> response = factomdClient.commitEntry("000165675d3d892047b9676e79ea9fae41053a7909380807bf83ac1064d8817e10bd664821812e01f48167f7f868d6163b4afb49a357829d9bdb2de092374d357424e2318c6f894a3f1726ed7185c3c6e00f5c47b646ab8ad664004a949e9945d7600ab8f4fb66085864d30f7a1c68c0e3d7db6df4fa8fdc3b3b0539397550a9a031c7a6ea2e5204");
        assertValidResponse(response);
    }

    private byte[] sign(byte[] message, String secret) throws Exception {
        byte[] privateKey = getKey(secret);

        PKCS8EncodedKeySpec encoded = new PKCS8EncodedKeySpec(privateKey);

        EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(privateKey, EdDSANamedCurveTable.ED_25519_CURVE_SPEC);
        EdDSAPrivateKey keyIn = new EdDSAPrivateKey(privateKeySpec);

        Signature instance = new EdDSAEngine();//.getInstance("RSA/ECB/PKCS1Padding");
        instance.initSign(keyIn);
        instance.update(message);

        byte[] signed = instance.sign();


        return signed;
    }

    private byte[] getKey(String key) {
        ByteOperations byteOperations = new ByteOperations();

        byte[] decodeKey = Base58.decodeChecked(key);
        return Arrays.copyOfRange(decodeKey,2, 34);
    }

    private byte entryCost(Entry entry) throws FactomException.ClientException {
        byte[] marshaledEntry = entryOperations.entryToBytes(entry.getExternalIds(), entry.getContent(), entry.getChainId());

        // calculate the length excluding the header size 35 for Milestone 1
        int length = marshaledEntry.length - 35;

        if (length > 10240) {
            throw new FactomException.ClientException("Entry cannot be larger than 10KB");
        }

        // cost is the capacity of the entry payment in KB
        int cost = (int) Math.ceil(length / 1024);
        if (cost < 1) {
            cost = 1;
        }

        return (byte) cost;
    }

    private byte chainCost(Chain chain) throws FactomException.ClientException {
        Chain.Entry entry = chain.getFirstEntry();
        String chainId =entryOperations.calculateChainId(entry.getExternalIds());

        byte[] marshaledEntry = entryOperations.entryToBytes(entry.getExternalIds(), entry.getContent(), chainId);
        // calculate the length excluding the header size 35 for Milestone 1
        int length = marshaledEntry.length - 35;

        if (length > 10240) {
            throw new FactomException.ClientException("Entry cannot be larger than 10KB");
        }

        // cost is the capacity of the entry payment in KB
        int cost = (int) Math.ceil(length / 1024);
        if (cost < 1) {
            cost = 1;
        }

        return (byte) (10+ cost);
    }

    private byte[] currentTimeMillis() {
        long now = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(now);
        byte[] holder = buffer.array();
        byte[] resp = new byte[]{holder[2], holder[3], holder[4], holder[5], holder[6], holder[7]};
        return resp;
    }

    private Chain chain() {
        List<String> externalIds = Arrays.asList(
                "80731950",
                "61626364",
                "31323334");

        Chain.Entry firstEntry = new Chain.Entry();
        firstEntry.setExternalIds(externalIds);
        firstEntry.setContent("3132333461626364");

        Chain chain = new Chain();
        chain.setFirstEntry(firstEntry);
        return chain;
    }

    private Entry entry() {
        List<String> externalIds = Arrays.asList("cd90", "90cd");

        Entry entry = new Entry();
        entry.setChainId("8008392c6baf81ab99aa14fd3ce7ac62726d76dc25c1c61029ad7a06c0531fb5");
        entry.setContent("abcdef");
        entry.setExternalIds(externalIds);

        return entry;
    }
}
