package org.blockchain_innovation.accumulate.factombridge.model;

import io.accumulatenetwork.sdk.commons.codec.DecoderException;
import io.accumulatenetwork.sdk.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;

public class HexStringKey implements DbKey {

    private final DbBucket bucket;
    private final String bucketSuffix;
    private String hexValue;

    public HexStringKey(final DbBucket bucket, final String hexValue) {
        this.bucket = bucket;
        this.bucketSuffix = null;
        this.hexValue = hexValue;
    }

    public HexStringKey(final DbBucket bucket, final String bucketSuffix, final String hexValue) {
        this.bucket = bucket;
        this.bucketSuffix = bucketSuffix;
        this.hexValue = hexValue;
    }


    private String buildKey(final boolean toKey) {
        final ByteArrayOutputStream keyBuf = new ByteArrayOutputStream();
        final DataOutputStream key = new DataOutputStream(keyBuf);
        try {
            key.write(bucket.getValue());
            if (StringUtils.isNotEmpty(bucketSuffix)) {
                key.write(Hex.decodeHex(bucketSuffix));
            }
            if (toKey) {
                key.writeByte(';' + 1);
            } else {
                key.writeByte(';');
            }
            if (StringUtils.isNotEmpty(hexValue)) {
                key.write(Hex.decodeHex(hexValue));
            }
        } catch (DecoderException | IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(keyBuf.toByteArray());
    }


    @Override
    public String getBase64Key() {
        return buildKey(false);
    }

    @Override
    public String getBase64ToKey() {
        return buildKey(true);
    }
}
