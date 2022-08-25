package org.blockchain_innovation.accumulate.factombridge.model;

import io.accumulatenetwork.sdk.commons.codec.binary.Hex;
import io.accumulatenetwork.sdk.generated.protocol.SignatureType;
import io.accumulatenetwork.sdk.protocol.SignatureKeyPair;
import io.accumulatenetwork.sdk.protocol.Url;
import io.accumulatenetwork.sdk.signing.AccKeyPairGenerator;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.model.types.AddressType;

public class LiteAccount extends io.accumulatenetwork.sdk.protocol.LiteAccount implements Address {
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
}
