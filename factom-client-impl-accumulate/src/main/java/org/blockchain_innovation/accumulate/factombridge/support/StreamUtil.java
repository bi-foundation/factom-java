package org.blockchain_innovation.accumulate.factombridge.support;

import io.accumulatenetwork.sdk.commons.codec.DecoderException;
import io.accumulatenetwork.sdk.commons.codec.binary.Hex;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StreamUtil {
    public static String readHashAsHex(final DataInputStream dataInputStream) {
        final byte[] buf = new byte[32];
        try {
            dataInputStream.read(buf);
            return Hex.encodeHexString(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeHexHash(final DataOutputStream dataOutputStream, final String chainId) {
        try {
            dataOutputStream.write(Hex.decodeHex(chainId));
        } catch (IOException | DecoderException e) {
            throw new RuntimeException(e);
        }
    }
}
