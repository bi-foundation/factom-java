package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum FactomDID {
    FCTR_V1("^did:fctr:(\\S*)$", "1.0");

    private static final EntryOperations ENTRY_OPS = new EntryOperations();

    private final Pattern pattern;
    private final String protocolVersion;


    FactomDID(String regex, String protocolVersion) {
        this.pattern = Pattern.compile(regex);
        this.protocolVersion = protocolVersion;
    }

    public String getTargetId(String didReference) {
        // parse identifier
        Matcher matcher = getPattern().matcher(didReference);
        if (!matcher.matches()) {
            return null;
        }

        return matcher.group(1);
    }

    public String determineChainId(String nonce, Encoding encoding) {
        return determineChainId(encoding.decode(nonce));
    }


    public String determineChainId(byte[] nonce) {
        return Encoding.HEX.encode(ENTRY_OPS.calculateChainId(createDIDExternalIds(nonce)));
    }

    protected List<String> createDIDExternalIds(byte[] nonce) {
        List<String> externalIds = new ArrayList<>();
        externalIds.add("CreateDID");
        externalIds.add(FactomDID.FCTR_V1.getProtocolVersion());
        externalIds.add(Encoding.HEX.encode(nonce));
        return externalIds;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
