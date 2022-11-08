package org.blockchain_innovation.accumulate.factombridge.model;

import io.accumulatenetwork.sdk.commons.codec.DecoderException;
import io.accumulatenetwork.sdk.commons.codec.binary.Hex;
import io.accumulatenetwork.sdk.protocol.Url;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AccUrlKey implements DbKey {

    private final DbBucket bucket;
    private final String bucketSuffix;
    private final Url url;

    public AccUrlKey(final DbBucket bucket, final Url url) {
        this.bucket = bucket;
        this.bucketSuffix = null;
        this.url = url;
    }

    public AccUrlKey(final DbBucket bucket, final String bucketSuffix, final Url url) {
        this.bucket = bucket;
        this.bucketSuffix = bucketSuffix;
        this.url = url;
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
            key.write(url.string().getBytes(StandardCharsets.UTF_8));
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
