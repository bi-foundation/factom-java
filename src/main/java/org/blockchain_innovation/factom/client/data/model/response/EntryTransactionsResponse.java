package org.blockchain_innovation.factom.client.data.model.response;

public class EntryTransactionsResponse {

    private String committxid;
    private String entryhash;
    private CommitData commitdata;
    private EntryData entrydata;

    public class CommitData {
        private String status;
    }

    public class EntryData {
        private String status;
    }
}
