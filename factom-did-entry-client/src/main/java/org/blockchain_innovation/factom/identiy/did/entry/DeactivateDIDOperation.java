package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.ops.Encoding;

import java.util.ArrayList;
import java.util.List;

public class DeactivateDIDOperation extends DIDOperation {
    public DeactivateDIDOperation(FactomDID didVersion, String keyId) {
        super(didVersion, keyId);
    }

    public List<String> externalIds() {
        List<String> externalIds = new ArrayList<>();
        externalIds.add("DeactivateDID");
        externalIds.add(keyId);
        String chainId = FactomDID.FCTR_V1.getTargetId(keyId);
        //fixme add signature according to spec
        externalIds.add(Encoding.BASE58.encode(chainId.getBytes()));
        return externalIds;
    }
}
