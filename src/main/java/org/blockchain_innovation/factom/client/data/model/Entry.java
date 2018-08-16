package org.blockchain_innovation.factom.client.data.model;

import java.util.List;

public class Entry {

    private String chainid;
    private List<String> extids;
    private String content;

    public String getChainId() {
        return chainid;
    }

    public Entry setChainId(String chainid) {
        this.chainid = chainid;
        return this;
    }

    public List<String> getExternalIds() {
        return extids;
    }

    public Entry setExternalIds(List<String> externalIds) {
        this.extids = externalIds;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Entry setContent(String content) {
        this.content = content;
        return this;
    }
}
