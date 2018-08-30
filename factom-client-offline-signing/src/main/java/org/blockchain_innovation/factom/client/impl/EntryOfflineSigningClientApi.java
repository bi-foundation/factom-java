package org.blockchain_innovation.factom.client.impl;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import org.blockchain_innovation.factom.client.api.Digests;
import org.blockchain_innovation.factom.client.api.Encoding;
import org.blockchain_innovation.factom.client.api.FactomException;
import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.StringUtils;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.CommitAndRevealEntryResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.CommitChainResponse;
import org.blockchain_innovation.factom.client.api.model.response.factomd.RevealResponse;
import org.blockchain_innovation.factom.client.impl.ops.ByteOperations;
import org.blockchain_innovation.factom.client.impl.ops.EntryOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class EntryOfflineSigningClientApi {

    private final Logger logger = LoggerFactory.getLogger(EntryClient.class);
    public static final int ENTRY_REVEAL_WAIT = 1000;

    private EntryOperations entryOperations = new EntryOperations();
    private ByteOperations byteOperations = new ByteOperations();

    private FactomdClient factomdClient;

    private CompletableFuture<Void> waitFuture() {
        return  CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(ENTRY_REVEAL_WAIT);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private FactomdClient getFactomdClient() throws FactomException.ClientException {
        if (factomdClient == null) {
            throw new FactomException.ClientException("factomd client not provided");
        }
        return factomdClient;
    }

    public EntryOfflineSigningClientApi setFactomdClient(FactomdClient factomdClient) {
        this.factomdClient = factomdClient;
        return this;
    }

    /**
     * Compose, reveal and commit a chain
     *
     * @param chain
     * @param entryCreditAddress
     * @param secret
     * @throws FactomException.ClientException
     */
    public CompletableFuture<CommitAndRevealChainResponse> commitAndRevealChain(Chain chain, String entryCreditAddress, String secret) throws FactomException.ClientException {
        String commitChainMessage = composeChainCommit(chain, entryCreditAddress, secret);
        String revealChainEntry = composeChainReveal(chain);

        CompletableFuture<CommitAndRevealChainResponse> commitAndRevealChainFuture =
                getFactomdClient().commitChain(commitChainMessage).thenCompose(_commitChainResponse ->
                        waitFuture().thenCompose(_void ->
                                getFactomdClient().revealChain(revealChainEntry).thenApply(_revealChainResponse -> {
                                    CommitAndRevealChainResponse response = new CommitAndRevealChainResponse();
                                    response.setCommitChainResponse(_commitChainResponse.getResult());
                                    response.setRevealResponse(_revealChainResponse.getResult());
                                    return response;
                                })));
        return commitAndRevealChainFuture;
    }

    /**
     * Compose, reveal and commit an entry
     *
     * @param entry
     * @param entryCreditAddress
     * @param secret
     * @throws FactomException.ClientException
     */
    public CompletableFuture<CommitAndRevealEntryResponse> commitAndRevealEntry(Entry entry, String entryCreditAddress, String secret) throws FactomException.ClientException {
        String commitEntryMessage = composeEntryCommit(entry, entryCreditAddress, secret);
        String revealCommitMessage = composeEntryReveal(entry);

        CompletableFuture<CommitAndRevealEntryResponse> commitAndRevealEntryFuture =
                getFactomdClient().commitEntry(commitEntryMessage).thenCompose(_commitEntryResponse ->
                        waitFuture().thenCompose(_void ->
                                getFactomdClient().revealChain(revealCommitMessage).thenApply(_revealEntryResponse -> {
                                    CommitAndRevealEntryResponse response = new CommitAndRevealEntryResponse();
                                    response.setCommitEntryResponse(_commitEntryResponse.getResult());
                                    response.setRevealResponse(_revealEntryResponse.getResult());
                                    return response;
                                })));
        return commitAndRevealEntryFuture;
    }

    /**
     * Compose a chain commit message that is needed to commit the chain
     *
     * @param chain
     * @param entryCreditPublicKey
     * @param secret
     * @return
     * @throws FactomException.ClientException
     */
    public String composeChainCommit(Chain chain, String entryCreditPublicKey, String secret) throws FactomException.ClientException {
        Chain.Entry firstEntry = chain.getFirstEntry();
        byte[] chainId = entryOperations.calculateChainId(firstEntry.getExternalIds());
        String chainIdHex = Encoding.HEX.encode(chainId);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 1 byte version
            byte[] version = {0};
            outputStream.write(version);

            // 6 byte milliTimestamp
            byte[] millis = currentTimeMillis();
            outputStream.write(millis);

            // 32 byte double sha256 hash of ChainID
            byte[] chainIdHash = Digests.SHA_256.doubleDigest(chainId);
            outputStream.write(chainIdHash);

            // 32 byte Weld; sha256(sha256(EntryHash + ChainID))
            byte[] entryHash = entryOperations.calculateEntryHash(firstEntry.getExternalIds(), firstEntry.getContent(), chainIdHex);
            byte[] weld = byteOperations.concat(entryHash, chainId);
            byte[] entryChainWeld = Digests.SHA_256.doubleDigest(weld);
            outputStream.write(entryChainWeld);

            // 32 byte Entry Hash of the First Entry
            outputStream.write(entryHash);

            // 1 byte number of Entry Credits to pay
            byte cost = chainCost(firstEntry.getExternalIds(), firstEntry.getContent(), chainIdHex);
            outputStream.write(cost);

            // 32 byte Entry Credit Address Public Key + 64 byte Signature
            byte[] message = outputStream.toByteArray();
            byte[] signature = sign(message, secret);
            byte[] entryCreditKey = getKeyFromAddress(entryCreditPublicKey);

            outputStream.write(entryCreditKey);
            outputStream.write(signature);

            byte[] entryParams = outputStream.toByteArray();
            return Encoding.HEX.encode(entryParams);
        } catch (IOException e) {
            throw new FactomException.ClientException("failed to compose chain commit message", e);
        }
    }

    /**
     * Compose chain reveal message that is needed to reveal the chain
     *
     * @param chain
     * @return
     */
    public String composeChainReveal(Chain chain) {
        Chain.Entry firstEntry = chain.getFirstEntry();
        byte[] revealParam = entryOperations.entryToBytes(firstEntry.getExternalIds(), firstEntry.getContent());
        return Encoding.HEX.encode(revealParam);
    }

    /**
     * Compose an entry commit message that is needed to commit the entry
     *
     * @param entry
     * @param entryCreditPublicKey
     * @param secret
     * @return
     * @throws FactomException.ClientException
     */
    public String composeEntryCommit(Entry entry, String entryCreditPublicKey, String secret) throws FactomException.ClientException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 1 byte version
            byte[] version = {0};
            outputStream.write(version);

            // 6 byte milliTimestamp
            byte[] millis = currentTimeMillis();
            outputStream.write(millis);

            // 32 byte Entry Hash
            byte[] entryHash = entryOperations.calculateEntryHash(entry.getExternalIds(), entry.getContent(), entry.getChainId());
            outputStream.write(entryHash);

            // 1 byte number of entry credits to pay
            byte cost = entryCost(entry.getExternalIds(), entry.getContent(), entry.getChainId());
            outputStream.write(cost);

            // 32 byte Entry Credit Address Public Key + 64 byte Signature
            byte[] message = outputStream.toByteArray();
            byte[] signature = sign(message, secret);
            byte[] entryCreditKey = getKeyFromAddress(entryCreditPublicKey);

            outputStream.write(entryCreditKey);
            outputStream.write(signature);

            byte[] entryParams = outputStream.toByteArray();
            return Encoding.HEX.encode(entryParams);
        } catch (IOException e) {
            throw new FactomException.ClientException("failed to compose entry commit message", e);
        }
    }

    /**
     * Compose entry reveal message that is needed to reveal the chain
     *
     * @param entry
     * @return
     */
    public String composeEntryReveal(Entry entry) {
        byte[] revealParam = entryOperations.entryToBytes(entry.getExternalIds(), entry.getContent(), entry.getChainId());
        return Encoding.HEX.encode(revealParam);
    }

    /**
     * sign message
     *
     * @param message
     * @param secret
     * @return
     * @throws FactomException.ClientException
     */
    private byte[] sign(byte[] message, String secret) throws FactomException.ClientException {
        byte[] privateKey = getKeyFromAddress(secret);

        EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(privateKey, EdDSANamedCurveTable.ED_25519_CURVE_SPEC);
        EdDSAPrivateKey keyIn = new EdDSAPrivateKey(privateKeySpec);

        try {
            Signature instance = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
            instance.initSign(keyIn);
            instance.update(message);

            byte[] signed = instance.sign();
            return signed;
        } catch (InvalidKeyException e) {
            throw new FactomException.ClientException(String.format("invalid key: ", e.getMessage()), e);
        } catch (SignatureException | NoSuchAlgorithmException e) {
            throw new FactomException.ClientException("failed to sign message", e);
        }
    }

    /**
     * extract key from Factoid or Entry Credit address. They have an identifiable prefix and a checksum to prevent typos
     *
     * @param address
     * @return
     */
    private byte[] getKeyFromAddress(String address) throws FactomException.ClientException {
        if (StringUtils.isEmpty(address) || address.length() != 52) {
            throw new FactomException.ClientException(String.format("invalid address: '%s'", address));
        }
        byte[] decodeKey = Encoding.BASE58.decode(address);
        return Arrays.copyOfRange(decodeKey, 2, 34);
    }

    /**
     * chain cost 10 + the first entry cost
     *
     * @param externalIds
     * @param content
     * @param chainId
     * @return
     * @throws FactomException.ClientException
     */
    private byte chainCost(List<String> externalIds, String content, String chainId) throws FactomException.ClientException {
        byte cost = entryCost(externalIds, content, chainId);
        return (byte) (10 + cost);
    }

    /**
     * The number of Entry Credits is based on the Payload size. Cost is 1 EC per partial KiB. Empty Entries cost 1 EC.
     *
     * @param externalIds
     * @param content
     * @param chainId
     * @return
     * @throws FactomException.ClientException
     */
    private byte entryCost(List<String> externalIds, String content, String chainId) throws FactomException.ClientException {
        byte[] marshaledEntry = entryOperations.entryToBytes(externalIds, content, chainId);

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

    /**
     * @return 6 byte current milli timestamp
     */
    protected byte[] currentTimeMillis() {
        long now = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(now);
        byte[] holder = buffer.array();
        byte[] resp = new byte[]{holder[2], holder[3], holder[4], holder[5], holder[6], holder[7]};
        return resp;
    }
}
