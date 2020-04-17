package org.blockchain_innovation.factom.client.impl;

import org.blockchain_innovation.factom.client.api.FactomResponse;
import org.blockchain_innovation.factom.client.api.SignatureProdiver;
import org.blockchain_innovation.factom.client.api.errors.FactomException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.Chain;
import org.blockchain_innovation.factom.client.api.model.Entry;
import org.blockchain_innovation.factom.client.api.model.response.walletd.ComposeResponse;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.ops.AddressSignatureProvider;
import org.blockchain_innovation.factom.client.api.ops.ByteOperations;
import org.blockchain_innovation.factom.client.api.ops.Digests;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;
import org.blockchain_innovation.factom.client.api.rpc.RpcResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings("PMD.TooManyMethods")
public class OfflineWalletdClientImpl extends WalletdClientImpl {

    private final OfflineAddressKeyConversions addressKeyConversions = new OfflineAddressKeyConversions();
    private final EntryOperations entryOperations = new EntryOperations();
    private final ByteOperations byteOperations = new ByteOperations();

    @Override
    public CompletableFuture<FactomResponse<ComposeResponse>> composeChain(Chain chain, SignatureProdiver signatureProdiver) throws FactomException.ClientException {
        Supplier<FactomResponse<ComposeResponse>> supplier = () -> {

            String message = composeChainCommit(chain, signatureProdiver);
            String entryReveal = composeChainReveal(chain);
            return composeResponse("commit-chain", message, "reveal-chain", entryReveal);
        };
        return CompletableFuture.supplyAsync(supplier);
    }

    @Override
    public CompletableFuture<FactomResponse<ComposeResponse>> composeChain(Chain chain, Address address) throws FactomException.ClientException {
        AddressType.ENTRY_CREDIT_SECRET.assertValid(address);
        return composeChain(chain, new AddressSignatureProvider(address));
    }

    @Override
    public CompletableFuture<FactomResponse<ComposeResponse>> composeEntry(Entry entry, SignatureProdiver signatureProdiver) throws FactomException.ClientException {
        Supplier<FactomResponse<ComposeResponse>> supplier = () -> {

            String message = composeEntryCommit(entry, signatureProdiver);
            String entryReveal = composeEntryReveal(entry);
            return composeResponse("commit-entry", message, "reveal-entry", entryReveal);
        };

        return CompletableFuture.supplyAsync(supplier);
    }

    @Override
    public CompletableFuture<FactomResponse<ComposeResponse>> composeEntry(Entry entry, Address address) throws FactomException.ClientException {
        AddressType.ENTRY_CREDIT_SECRET.assertValid(address);
        return composeEntry(entry, new AddressSignatureProvider(address));
    }

    private FactomResponse<ComposeResponse> composeResponse(String commitMethod, String commitMessage, String revealMethod, String revealEntry) {
        ComposeResponse.Commit.Params commitParams = new ComposeResponse.Commit.Params(commitMessage);


        ComposeResponse.Commit commit = new ComposeResponse.Commit("2.0", commitMethod, 0, commitParams);
        ComposeResponse.Reveal.Params revealParams = new ComposeResponse.Reveal.Params(revealEntry);
        ComposeResponse.Reveal reveal = new ComposeResponse.Reveal("2.0", revealMethod, 0, revealParams);
        ComposeResponse composeResponse = new ComposeResponse(commit, reveal);

        RpcResponse<ComposeResponse> rpcResponse = new RpcResponse<>(composeResponse);
        return new OfflineFactomResponseImpl<>(rpcResponse);
    }

    protected String composeChainCommit(Chain chain, SignatureProdiver signatureProdiver) throws FactomException.ClientException {
        Entry firstEntry = chain.getFirstEntry();
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
            byte[] signature = signatureProdiver.sign(message);
            byte[] entryCreditKey = addressKeyConversions.addressToKey(signatureProdiver.getPublicECAddress());

            outputStream.write(entryCreditKey);
            outputStream.write(signature);

            byte[] entryParams = outputStream.toByteArray();
            return Encoding.HEX.encode(entryParams);
        } catch (IOException e) {
            throw new FactomException.ClientException("failed to compose chain commit message", e);
        }
    }

    /**
     * Compose an entry commit message that is needed to commit the entry.
     *
     * @param entry
     * @param signatureProdiver
     * @return
     * @throws FactomException.ClientException
     */
    protected String composeEntryCommit(Entry entry, SignatureProdiver signatureProdiver) throws FactomException.ClientException {
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
            byte[] signature = signatureProdiver.sign(message);
            byte[] entryCreditKey = addressKeyConversions.addressToKey(signatureProdiver.getPublicECAddress());

            outputStream.write(entryCreditKey);
            outputStream.write(signature);

            byte[] entryParams = outputStream.toByteArray();
            return Encoding.HEX.encode(entryParams);
        } catch (IOException e) {
            throw new FactomException.ClientException("failed to compose entry commit message", e);
        }
    }

    /**
     * Compose chain reveal message that is needed to reveal the chain.
     *
     * @param chain
     * @return
     */
    protected String composeChainReveal(Chain chain) {
        Entry firstEntry = chain.getFirstEntry();
        byte[] revealParam = entryOperations.firstEntryToBytes(firstEntry.getExternalIds(), firstEntry.getContent());
        return Encoding.HEX.encode(revealParam);
    }

    /**
     * Compose entry reveal message that is needed to reveal the chain.
     *
     * @param entry
     * @return
     */
    protected String composeEntryReveal(Entry entry) {
        byte[] revealParam = entryOperations.entryToBytes(entry.getExternalIds(), entry.getContent(), entry.getChainId());
        return Encoding.HEX.encode(revealParam);
    }

    /**
     * chain cost 10 + the first entry cost.
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
        int cost = (int) Math.ceil(1.0 * length / 1024);
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
