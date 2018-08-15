package org.blockchain_innovation.factom.client.data.model.response.factomd;

import java.util.List;

public class ReceiptResponse {

    private Entry entry;
    private List<MerkleBranch> merklebranch;
    private String entryblockkeymr;
    private String directoryblockkeymr;
    private String bitcointransactionhash;
    private String bitcoinblockhash;

    public Entry getEntry() {
        return entry;
    }

    public List<MerkleBranch> getMerkleBranch() {
        return merklebranch;
    }

    public String getEntryBlockKeyMR() {
        return entryblockkeymr;
    }

    public String getDirectoryBlockKeyMR() {
        return directoryblockkeymr;
    }

    public String getBitcoinTransactionHash() {
        return bitcointransactionhash;
    }

    public String getBitcoinBlockHash() {
        return bitcoinblockhash;
    }

    public class Entry {
        private String entryhash;

        public String getEntryHash() {
            return entryhash;
        }
    }

    public class MerkleBranch {
        private String left;
        private String right;
        private String top;

        public String getLeft() {
            return left;
        }

        public String getRight() {
            return right;
        }

        public String getTop() {
            return top;
        }
    }
}
