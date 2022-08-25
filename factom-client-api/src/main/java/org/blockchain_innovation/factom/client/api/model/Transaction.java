package org.blockchain_innovation.factom.client.api.model;

import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.response.factomd.TransactionResponse;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;
import org.blockchain_innovation.factom.client.api.model.types.RCDType;
import org.blockchain_innovation.factom.client.api.ops.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

public class Transaction {
    private final long timestamp;

    private final List<Input> inputs;

    private final List<Output> fctOutputs;
    private final List<Output> ecOutputs;

    private final String id;
    private final String hash;
    private final Map<RCD, byte[]> signatures;
    private final byte[] marshalled;

    protected Transaction(Builder builder) {
        this.timestamp = builder.timestamp;
        this.inputs = builder.inputs;
        this.fctOutputs = builder.fctOutputs;
        this.ecOutputs = builder.ecOutputs;
        this.id = builder.txId;
        this.hash = builder.txHash;
        this.signatures = builder.signatures;
        this.marshalled = builder.marshalled;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public List<Output> getFctOutputs() {
        return fctOutputs;
    }

    public List<Output> getEcOutputs() {
        return ecOutputs;
    }

    public String getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    protected Map<RCD, byte[]> getSignatures() {
        return signatures;
    }

    public byte[] marshal() {
        return marshalled;
    }

    public boolean isSigned() {
        return !getInputs().isEmpty() && getInputs().stream().allMatch(input -> getSignatures().containsKey(input.getRcd()));
    }

    public static class Output {
        private Address address;
        private long amount;

        public Output(long amount, Address address) {

            this.address = address;
            this.amount = amount;
        }

        public Address getAddress() {
            return address;
        }

        public long getAmount() {
            return amount;
        }

        public byte[] marshal() {
            return new Marshaller().output(this);
        }
    }

    public static class Input {
        private final long amount;
        private final RCD rcd;
        private byte[] signature;

        public Input(long amount, RCD rcd) {
            this.amount = amount;
            this.rcd = rcd;
        }

        public Input(long amount, RCD rcd, byte[] signature) {
            this(amount, rcd);
            this.signature = signature;
        }

        public Address getAddress() {
            return rcd.getAddress();
        }

        public long getAmount() {
            return amount;
        }

        public RCD getRcd() {
            return rcd;
        }

        public byte[] marshal() {
            return new Marshaller().input(this);
        }
    }

    public static class Builder {
        private static final AddressKeyConversions CONVERSIONS = new AddressKeyConversions();
        private static final SigningOperations SIGNING = new SigningOperations();
        private static final ByteOperations BYTES = new ByteOperations();
        private static final Marshaller MARSHALLER = new Marshaller();
        private static final TransactionOperations TRANSACTIONS = new TransactionOperations();

        private String txId;
        private String txHash;
        private byte[] marshalled;
        private Map<RCD, AddressSignatureProvider> signatureProviders = new HashMap<>();
        private Map<RCD, byte[]> signatures = new HashMap<>();
        private List<Input> inputs = new ArrayList<>();
        private List<Output> fctOutputs = new ArrayList<>();
        private List<Output> ecOutputs = new ArrayList<>();
        private long timestamp;

        public Builder addInput(long amount, final Address secretAddress) {
            if (secretAddress == null || secretAddress.getType() != AddressType.FACTOID_SECRET) {
                throw new FactomRuntimeException.AssertionException("Input needs to be a secret Factoid address");
            }
            final byte[] publicKey = CONVERSIONS.addressToPublicKey(secretAddress.getValue());
            final RCD rcd = RCD.fromPublicKey(RCDType.TYPE_1, publicKey);
            signatureProviders.put(rcd, new AddressSignatureProvider(secretAddress));
            inputs.add(new Input(amount, rcd));
            return this;
        }

        // this one is probably not usable as you need access to all inputs/outputs to generate the signature
        /*
        public Builder addInput(long amount, final RCD rcd, byte[] signature) {
            inputs.add(new Input(amount, rcd));
            signatures.put(rcd, signature);
            return this;
        }*/

        public Builder addInput(long amount, String secretAddress) {
            return addInput(amount, ECAddress.fromString(secretAddress));
        }

        // TODO: 07/08/2021 We need to verify how/what the RCD signs. Right now it signs the header + addresses
        public Builder addInput(long amount, RCD rcd, AddressSignatureProvider addressSignatureProvider) {
            if (!Objects.equals(rcd.getAddress(), addressSignatureProvider.getPublicAddress())) {
                throw new FactomRuntimeException.AssertionException("RCD address did not match signature provider public address");
            }
            signatureProviders.put(rcd, addressSignatureProvider);
            inputs.add(new Input(amount, rcd));
            return this;
        }

        public Builder addOutput(long amount, Address address) {
            if (address.getType() == AddressType.FACTOID_PUBLIC) {
                fctOutputs.add(new Output(amount, address));
            } else if (address.getType() == AddressType.ENTRY_CREDIT_PUBLIC) {
                ecOutputs.add(new Output(amount, address));
            } else {
                throw new FactomRuntimeException.AssertionException("Output needs to be a public Entry Credit or Factoid address");
            }
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            return timestamp(timestamp.toEpochMilli());
        }

        public Builder response(TransactionResponse transactionResponse) {
            final TransactionResponse.Transaction trans = transactionResponse.getFactoidTransaction();
            timestamp(trans.getMilliTimestamp());
            for (int idx = 0; idx < trans.getInputs().size(); idx++) {
                final TransactionResponse.Transaction.Input input = trans.getInputs().get(idx);
                final String rcdHash = trans.getRedeemConditionDataStructures().get(idx);
                final RCD rcd = RCD.fromHash(RCDType.TYPE_1, rcdHash.getBytes(StandardCharsets.UTF_8));
                inputs.add(new Input(input.getAmount(), rcd));
                signatures.put(rcd, trans.getSignatureBlocks().get(idx).getSignatures().get(0).getBytes(StandardCharsets.UTF_8));
            }
            for (TransactionResponse.Transaction.Output output : trans.getOutputs()) {
                fctOutputs.add(new Output(output.getAmount(),
                        ECAddress.fromString(StringUtils.isNotEmpty(output.getUserAddress()) ? output.getUserAddress() : CONVERSIONS.rcdHashToFctAddress(output.getAddress().getBytes(StandardCharsets.UTF_8)))));
            }
            for (TransactionResponse.Transaction.Output output : trans.getOutputs()) {
                ecOutputs.add(new Output(output.getAmount(), ECAddress.fromString(output.getUserAddress())));
            }
            this.timestamp = trans.getMilliTimestamp();
            return this;
        }

        public Transaction build() {
            if (timestamp == 0) {
                this.timestamp = System.currentTimeMillis();
            }
            if (StringUtils.isEmpty(txId)) {

                final byte[] marshalledHeader = MARSHALLER.excludeSignatures(timestamp, inputs, fctOutputs, ecOutputs);

                // If we have signature providers, make sure we create the sigs just in time
                signatureProviders.forEach((rcd, addressSignatureProvider) -> {
                    signatures.put(rcd, addressSignatureProvider.sign(marshalledHeader));
                });

                final byte[] marshalledSignatures = MARSHALLER.signaturesBlock(inputs, signatures);
                this.marshalled = BYTES.concat(marshalledHeader, marshalledSignatures);
                this.txId = Encoding.HEX.encode(Digests.SHA_256.digest(marshalledHeader));
                this.txHash = Encoding.HEX.encode(Digests.SHA_256.digest(marshalled));

            }

            return new Transaction(this);
        }


    }

    public static class Marshaller {
        private static final byte VERSION = 2;


        private final EncodeOperations encodeOps = new EncodeOperations();
        private final ByteOperations byteOps = new ByteOperations();
        private final TransactionOperations transOps = new TransactionOperations();

        public byte[] excludeSignatures(long milliTimestamp, List<Input> fctInputs, List<Output> fctOutputs, List<Output> ecOutputs) {
            try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                writeHeader(milliTimestamp, fctInputs, fctOutputs, ecOutputs, os);

                fctInputs.stream().forEach(input -> appendBytes(os, input.marshal()));
                fctOutputs.stream().forEach(output -> appendBytes(os, output.marshal()));
                ecOutputs.stream().forEach(output -> appendBytes(os, output.marshal()));

                return os.toByteArray();
            } catch (IOException e) {
                throw new FactomRuntimeException(e);
            }

        }

        public byte[] signaturesBlock(List<Input> inputs, Map<RCD, byte[]> signatures) {
            try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                inputs.stream().forEach(input -> {
                    if (!signatures.containsKey(input.getRcd())) {
                        throw new FactomRuntimeException.AssertionException("No RCD provided for input " + input.getAddress());
                    }
                    appendBytes(os, input.getRcd().getType().getValue());
                    appendBytes(os, input.getRcd().getPublicKey());
                    appendBytes(os, signatures.get(input.getRcd()));
                });
                return os.toByteArray();
            } catch (IOException e) {
                throw new FactomRuntimeException(e);
            }
        }

        public byte[] transaction(long milliTimestamp, List<Input> fctInputs, List<Output> fctOutputs, List<Output> ecOutputs, Map<RCD, byte[]> signatures) {
            return byteOps.concat(
                    excludeSignatures(milliTimestamp, fctInputs, fctOutputs, ecOutputs),
                    signaturesBlock(fctInputs, signatures)
            );
        }

        public byte[] input(final Input input) {
            try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                bos.write(encodeOps.encodeVarInt(input.getAmount()));
                bos.write(encodeOps.decodeAddress(input.getAddress().getValue()));
                return bos.toByteArray();
            } catch (IOException e) {
                throw new FactomRuntimeException(e);
            }
        }

        public byte[] output(final Output output) {
            try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                bos.write(encodeOps.encodeVarInt(output.getAmount()));
                bos.write(encodeOps.decodeAddress(output.getAddress().getValue()));
                return bos.toByteArray();
            } catch (IOException e) {
                throw new FactomRuntimeException(e);
            }
        }

        private void writeHeader(long milliTimestamp, List<Input> fctInputs, List<Output> fctOutputs, List<Output> ecOutputs, ByteArrayOutputStream os) throws IOException {
            // 1 byte version
            os.write(encodeOps.encodeVarInt(VERSION));
            // 6 byte milliTimestamp
            os.write(transOps.toMilliTimestamp(milliTimestamp));
            // Input count
            os.write(fctInputs.size());
            // FCT output count
            os.write(fctOutputs.size());
            // EC output count
            os.write(ecOutputs.size());
        }

        private void appendBytes(final ByteArrayOutputStream os, final byte[] bytes) {
            try {
                os.write(bytes);
            } catch (IOException e) {
                throw new FactomRuntimeException(e);
            }
        }


    }
}
