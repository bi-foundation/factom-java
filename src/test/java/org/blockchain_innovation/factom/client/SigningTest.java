package org.blockchain_innovation.factom.client;

import org.blockchain_innovation.factom.client.data.FactomException;
import org.blockchain_innovation.factom.client.data.conversion.Encoding;
import org.blockchain_innovation.factom.client.data.conversion.EntryOperations;
import org.blockchain_innovation.factom.client.data.model.Entry;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.junit.Test;
import sun.misc.BASE64Decoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SigningTest extends AbstractClientTest {

    @Test
    public void importAddress() throws IOException, NoSuchAlgorithmException, CertificateException {
        String certPath = "src/test/resources/certificate.pem";
        File pubCertFile = new File(certPath);
        ArrayList<Certificate> certs = new ArrayList<Certificate>();
        try (FileInputStream fileInputStream = new FileInputStream(pubCertFile); BufferedInputStream bis = new BufferedInputStream(fileInputStream)) {
            CertificateFactory certFact = CertificateFactory.getInstance("X.509");
            Certificate cert = certFact.generateCertificate(bis);
            System.out.println("cert = " + cert);
            certs.add(cert);
        }

        String keyPath = "src/test/resources/key.pem";
        Security.addProvider(new BouncyCastleProvider());
        System.out.println(Signature.getInstance("SHA1withRSA").toString());
        try (FileReader fileReader = new FileReader(keyPath); BufferedReader br = new BufferedReader(fileReader); PEMParser pp = new PEMParser(br)) {
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pp.readObject();
            ASN1Encodable privateKey = privateKeyInfo.parsePrivateKey();

            System.out.println("privateKeyInfo = " + privateKeyInfo);
            System.out.println("privateKey = " + privateKey);

            //samlResponse.sign(Signature.getInstance("SHA1withRSA").toString(), kp.getPrivate(), certs);
        }
    }

    @Test
    public void signMessaage() throws Exception {
        String publicK = "FA22de5NSG2FA2HmMaD4h8qSAZAJyztmmnwgLPghCQKoSekwYYct";
        String secret =  "Fs1jQGc9GJjyWNroLPq7x6LbYQHveyjWNPXSqAvCEKpETNoTU5dP";

        PublicKey publicKey = getPublicKey(publicK);

        System.out.println("publicKey = " + publicKey);


        String data = "... data to be encrypted ....";
        String alg = "RSA/ECB/PKCS1Padding";
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte encryptedBytes[] = cipher.doFinal(data.getBytes());

        System.out.println("encryptedBytes = " + encryptedBytes);
    }


    public PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    @Test
    public void composeEntryCommit() throws IOException, FactomException.ClientException {
        String entryCreditPublicKey = "EC3cqLZPq5ypwRB5CLfXnud5vkWAV2sd235CFf9KcWcE3FH9GRxv";
        String secret =  "Es3Y6U6H1Pfg4wYag8VMtRZEGuEJnfkJ2ZuSyCVcQKweB6y4WvGH";

        EntryOperations entryOperations = new EntryOperations();
        Entry entry = entry();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // write version
            outputStream.write(new byte[]{0});

            // 6 byte milliTimestamp (truncated unix time)
            outputStream.write(currentTimeMillis());

            // 32 byte Entry Hash
            String entryHash = entryOperations.calculateEntryHash(entry.getExternalIds(), entry.getContent(), entry.getChainId());
            byte[] entryHashBytes = Encoding.UTF_8.decode(entryHash);
            outputStream.write(entryHashBytes);

            // 1 byte number of entry credits to pay
            byte cost = entryCost(entry);
            outputStream.write(cost);

            // 32 byte Entry Credit Address Public Key + 64 byte Signature
            byte[] message = outputStream.toByteArray();
            byte[] signature = sign(message, secret);
            outputStream.write(Encoding.BASE64.decode(entryCreditPublicKey));
            outputStream.write(signature);

            byte[] entryParams = outputStream.toByteArray();
            System.out.println("entryParams = " + entryParams);
            System.out.println("entryParams = " + Encoding.UTF_8.encode(entryParams));
            System.out.println("entryParams = " + Encoding.HEX.encode(entryParams));
        }
    }

    private byte[] sign(byte[] message, String secret) {
        return new byte[64];
    }

    private EntryOperations entryOperations = new EntryOperations();

    private byte entryCost(Entry entry) throws FactomException.ClientException {
        byte[] marshaledEntry =  entryOperations.entryToBytes(entry.getExternalIds(), entry.getContent(), entry.getChainId());

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

    private Entry entry() {
        List<String> externalIds = Arrays.asList("cd90", "90cd");

        Entry entry = new Entry();
        entry.setChainId("8008392c6baf81ab99aa14fd3ce7ac62726d76dc25c1c61029ad7a06c0531fb5");
        entry.setContent("abcdef");
        entry.setExternalIds(externalIds);

        return entry;
    }


    private static byte[] currentTimeMillis() {
        long now = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(now);
        byte[] holder = buffer.array();
        byte[] resp = new byte[]{holder[2], holder[3], holder[4], holder[5], holder[6], holder[7]};
        return resp;
    }
}
