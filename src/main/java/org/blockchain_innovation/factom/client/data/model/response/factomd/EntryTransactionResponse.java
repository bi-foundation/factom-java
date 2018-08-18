package org.blockchain_innovation.factom.client.data.model.response.factomd;

public class EntryTransactionResponse {

    private String committxid;
    private String entryhash;
    private CommitData commitdata;
    private EntryData entrydata;

    public String getCommitTxId() {
        return committxid;
    }

    public String getEntryHash() {
        return entryhash;
    }

    public CommitData getCommitData() {
        return commitdata;
    }

    public EntryData getEntryData() {
        return entrydata;
    }

    public enum Status {
        Unknown,
        NotConfirmed,
        TransactionACK,
        DBlockConfirmed;
    }

    public static class CommitData {

        private Status status;

        public Status getStatus() {
            return status;
        }
    }

    public class EntryData {
        private String status;

        public String getStatus() {
            return status;
        }
    }
}
