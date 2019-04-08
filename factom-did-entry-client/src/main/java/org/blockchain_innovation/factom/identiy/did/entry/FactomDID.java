package org.blockchain_innovation.factom.identiy.did.entry;

import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.api.ops.EntryOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum FactomDID {
    FCTR_V1("^did:ftcr:(\\S*)$", "1.0");

    private final Pattern pattern;
    private final String protocolVersion;


    FactomDID(String regex, String protocolVersion) {
        this.pattern = Pattern.compile(regex);
        this.protocolVersion = protocolVersion;
    }

    public String getTargetDid(String identifier) {
        // parse identifier
        Matcher matcher = getPattern().matcher(identifier);
        if (!matcher.matches()) {
            return null;
        }

        return matcher.group(1);
    }

    public String determineDidChainId(byte[] nonce) {
        EntryOperations entryOperations = new EntryOperations();
        return Encoding.HEX.encode(entryOperations.calculateChainId(createDIDExternalIds(nonce)));
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
