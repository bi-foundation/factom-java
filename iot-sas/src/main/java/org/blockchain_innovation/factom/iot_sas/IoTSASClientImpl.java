package org.blockchain_innovation.factom.iot_sas;

import org.blockchain_innovation.factom.client.api.SignatureProdiver;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Address;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IoTSASClientImpl implements SignatureProdiver {

    private IoTSASPort port;

    public IoTSASClientImpl() {
    }

    public IoTSASClientImpl(IoTSASPort port) {
        setPort(port);
    }


    public IoTSASClientImpl setPort(IoTSASPort port) {
        this.port = port;
        return this;
    }

    public IoTSASPort port() {
        if (port == null) {
            throw new FactomRuntimeException.AssertionException("Please setup the IoT SAS port first");
        }
        return port;
    }

    public Address getPublicECAddress() {

        OutputStream outStream;
        try {
            outStream = port().get().getOutputStream();
            InputStream inStream = port().get().getInputStream();

            // Sending data
            byte[] getECHeader = new byte[]{(byte) 0xFA, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00};
            outStream.write(getECHeader, 0, getECHeader.length);


            //Read the IOT-SAS reply, which should be a 52 byte key
            byte[] key = new byte[52];
            int count = 0;
            int bytesRx;
            while (count < key.length) {
                bytesRx = inStream.read(key, count, key.length - count);
                count += bytesRx;
            }

            return Address.fromBytes(key);
        } catch (IOException e) {
            throw new IoTSASPort.IoTSASPortException(e);
        }

    }


    //Sign data on the IOT-SAS board, using the hardware secret key.
    private byte[] signUsingEd25519(byte[] data) {


        //Create a data buffer, which includes header, and data to be signed.
        byte[] toSign = new byte[data.length + 5];
        toSign[0] = (byte) 0xFA;
        toSign[1] = 0x02;
        toSign[2] = 0x02;
        toSign[3] = 0x0;
        toSign[4] = (byte) data.length;
        for (int i = 0; i < data.length; i++) {
            toSign[i + 5] = data[0];
        }

        try {
            //Write the buffer to the IOT-SAS device.
            port().get().getOutputStream().write(toSign, 0, toSign.length);

            //Read the IOT-SAS reply, which should be a 64 byte signature (TODO validation)
            byte[] signature = new byte[64];
            int count = 0;
            int bytesRx;
            while (count < signature.length) {
                bytesRx = port().get().getInputStream().read(signature, count, signature.length - count);
                count += bytesRx;
            }
            return signature;

        } catch (IOException e) {
            throw new IoTSASPort.IoTSASPortException(e);
        }

    }

    @Override
    public byte[] sign(byte[] input) {
        return signUsingEd25519(input);
    }
}
