package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.ops.Digests;
import org.blockchain_innovation.factom.client.api.ops.Encoding;

import java.util.ArrayList;
import java.util.List;

public class UpdateDIDOperation extends DIDOperation {

    public UpdateDIDOperation(FactomDID didVersion, String keyId, byte[] nonce) {
        super(didVersion, keyId, nonce);
    }

    public List<String> externalIds() {
        List<String> externalIds = new ArrayList<>();
        externalIds.add("UpdateDID");
        externalIds.add(keyId);
        // fixme add content according to spec and sign using key
        externalIds.add(Encoding.BASE58.encode(Digests.SHA_512.digest(nonce /* + content */)));
        return externalIds;
    }

}
