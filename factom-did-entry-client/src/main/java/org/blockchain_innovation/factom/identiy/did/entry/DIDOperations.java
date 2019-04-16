package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.ops.Encoding;

import java.util.ArrayList;
import java.util.List;

public enum DIDOperations {

    CreateDID() {
        public List<String> externalIds(FactomDID didVersion, byte[] nonce) {
            List<String> externalIds = new ArrayList<>();
            externalIds.add(name());
            externalIds.add(didVersion.getProtocolVersion());
            externalIds.add(Encoding.HEX.encode(nonce));
            return externalIds;
        }
    };

    public abstract List<String> externalIds(FactomDID didVersion, byte[] nonce);

}
