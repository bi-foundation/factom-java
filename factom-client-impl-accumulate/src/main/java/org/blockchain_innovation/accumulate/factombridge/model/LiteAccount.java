package org.blockchain_innovation.accumulate.factombridge.model;

import com.iwebpp.crypto.TweetNaclFast;
import io.accumulatenetwork.sdk.commons.codec.binary.Hex;
import io.accumulatenetwork.sdk.generated.protocol.AccountType;
import io.accumulatenetwork.sdk.generated.protocol.SignatureType;
import io.accumulatenetwork.sdk.protocol.SignatureKeyPair;
import io.accumulatenetwork.sdk.protocol.Url;
import io.accumulatenetwork.sdk.signing.AccKeyPairGenerator;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Base64;

public class LiteAccount extends io.accumulatenetwork.sdk.protocol.LiteAccount implements Address {

    public LiteAccount() {
    }

    public LiteAccount(SignatureKeyPair keyPair) {
        super(keyPair);
    }

    public LiteAccount(Url acmeTokenUrl, SignatureKeyPair keyPair) {
        super(acmeTokenUrl, keyPair);
    }

    @Override
    public String getValue() {
        return super.getAccount().getUrl().string() + "|" + Hex.encodeHexString(super.getSignatureKeyPair().getPrivateKey());
    }

    @Override
    public AddressType getType() {
        return AddressType.LITE_ACCOUNT;
    }

    public static LiteAccount generate(SignatureType signatureType) {
        SignatureKeyPair keyPair = new SignatureKeyPair(AccKeyPairGenerator.generate(signatureType), signatureType);
        return new LiteAccount(keyPair);
    }

    public static LiteAccount importFromBase64(final String data) {
        try {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            final DataInputStream inputStream = new DataInputStream(byteArrayInputStream);
            final AccountType accountType = AccountType.fromValue(inputStream.readByte());
            if (accountType != AccountType.LITE_TOKEN_ACCOUNT) {
                throw new IllegalArgumentException("Only account type LiteTokenAccount is supported for import atm.");
            }
            final SignatureType signatureType = SignatureType.fromValue(inputStream.readByte());
            final byte[] secretKey = new byte[inputStream.readByte()];
            inputStream.read(secretKey);
            final SignatureKeyPair keyPair = new SignatureKeyPair(TweetNaclFast.Signature.keyPair_fromSecretKey(secretKey), signatureType);
            return new LiteAccount(keyPair);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
