package org.blockchain_innovation.factom.iot_sas;

import org.blockchain_innovation.factom.client.api.SignatureProdiver;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.ops.ByteOperations;
import org.blockchain_innovation.factom.client.api.ops.Encoding;

import java.nio.charset.StandardCharsets;

public class IoTSASClientImpl implements SignatureProdiver {

    protected static final String EC_ADDRESS_HEADER = "FA010100000000";
    protected static final String ED25519_SIGN_HEADER = "FA020200";

    private IoTSASPort port;
    private final Logger logger = LogFactory.getLogger(IoTSASClientImpl.class);

    public IoTSASClientImpl() {
        // Call to setPort needed
    }

    public IoTSASClientImpl(IoTSASPort port) {
        setPort(port);
    }


    public IoTSASClientImpl setPort(IoTSASPort port) {
        this.port = port;
        return this;
    }

    public IoTSASPort port() {
        assertPort();
        port.open();
        return port;
    }

    @Override
    public Address getPublicECAddress() {
        port().clearInBuffer();
        logger.info(String.format("Getting public EC address from %s", port()));

        // Sending data
        logger.debug(String.format("Sending public EC address retrieval header to %s", port()));
        byte[] eCHeader = Encoding.HEX.decode(EC_ADDRESS_HEADER);
        final int bytesWritten = port().get().writeBytes(eCHeader, eCHeader.length);
        if (bytesWritten != eCHeader.length) {
            throw new IoTSASPort.IoTSASPortException(String.format("Could not write public EC header to IoT-SAS device serial port. Bytes written: %d", bytesWritten));
        }
        logger.debug(String.format("Public EC address retrieval header with length %d sent to %s", bytesWritten, port()));


        //Read the IOT-SAS reply, which should be a 52 byte key

        logger.debug(String.format("Retrieving public EC address from %s", port()));
        byte[] key = new byte[52];

        int count = port().get().readBytes(key, key.length);
        if (count != 52) {
            throw new IoTSASPort.IoTSASPortException(String.format("Could not read public EC address. Only %d bytes returned instead of 52. Result: %s", count, new String(key, StandardCharsets.UTF_8)));
        }

        Address address = Address.fromHexBytes(key);
        logger.info(String.format("Retrieved public EC address %s from %s", address, port()));
        return address;

    }


    @Override
    public byte[] sign(byte[] input) {
        return signUsingEd25519(input);
    }

    /**
     * Sign data on the IOT-SAS board, using the hardware secret key.
     *
     * @param data The input data to sign
     * @return The signature
     */
    private byte[] signUsingEd25519(byte[] data) {
        //Create a data buffer, which includes header, and data to be signed.
        byte[] toSign = new byte[0];
        ByteOperations byteOperations = new ByteOperations();
        toSign = byteOperations.concat(toSign, Encoding.HEX.decode(ED25519_SIGN_HEADER));
        toSign = byteOperations.concat(toSign, (byte) data.length);
        toSign = byteOperations.concat(toSign, data);

        port().clearInBuffer().writeBytes(toSign, toSign.length);

        //Read the IOT-SAS reply, which should be a 64 byte signature
        byte[] signature = new byte[64];
        int bytesRx;
        bytesRx = port().get().readBytes(signature, signature.length);
        if (bytesRx != signature.length) {
            throw new FactomRuntimeException.AssertionException(String.format("Received %d bytes instead of %d", bytesRx, signature.length));
        }
        return signature;
    }

    private void assertPort() {
        if (port == null) {
            throw new FactomRuntimeException.AssertionException("Please setup the IoT-SAS serial port first using the setPort() method");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (port != null) {
            port.close();
        }
        super.finalize();
    }
}
