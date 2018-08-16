package org.blockchain_innovation.factom.client.data.model;

import java.util.List;

public class Chain {

    private Entry firstentry;

    public Entry getFirstEntry() {
        return firstentry;
    }

    public Chain setFirstEntry(Entry firstEntry) {
        this.firstentry = firstEntry;
        return this;
    }

    public static class Entry {
        private List<String> extids;
        private String content;

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
}
