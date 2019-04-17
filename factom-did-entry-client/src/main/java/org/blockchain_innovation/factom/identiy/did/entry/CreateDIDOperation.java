package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.ops.Encoding;

import java.util.ArrayList;
import java.util.List;

public class CreateDIDOperation extends DIDOperation {
    public CreateDIDOperation(FactomDID didVersion, byte[] nonce) {
        super(didVersion, nonce);
    }

    public List<String> externalIds() {
        List<String> externalIds = new ArrayList<>();
        externalIds.add("CreateDID");
        externalIds.add(didVersion.getProtocolVersion());
        externalIds.add(Encoding.HEX.encode(nonce));
        return externalIds;
    }
    
}
