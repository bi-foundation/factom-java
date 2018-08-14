package org.blockchain_innovation.factom.client.data.model.response;

import java.util.List;

public class FactoidBlockResponse {

    private FactoidBlock fblock;
    private String rawdata;

    public class FactoidBlock {
        private String bodymr;
        private String prevkeymr;
        private String prevledgerkeymr;
        private int exchrate;
        private int dbheight;
        private List<Transaction> transactions;
        private String chainid;
        private String keymr;
        private String ledgerkeymr;

        public class Transaction {
            private String txid;
            private int blockheight;
            private long millitimestamp;
            private List<Input> inputs;
            private List<Output> outputs;
            private List<String> outecs;
            private List<SigBlock> sigblocks;

            public class IO {
                private int amount;
                private String address;
                private String useraddress;
            }

            public class Input extends IO { }

            public class Output extends IO { }

            public class SigBlock {
                private List<String> signatures;
            }
        }
    }
}
