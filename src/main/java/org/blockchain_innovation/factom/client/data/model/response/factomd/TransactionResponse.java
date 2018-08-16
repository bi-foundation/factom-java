package org.blockchain_innovation.factom.client.data.model.response.factomd;

import java.util.List;

public class TransactionResponse {

    private Transaction factoidtransaction;
    private String includedintransactionblock;
    private String includedindirectoryblock;
    private int includedindirectoryblockheight;

    public class Transaction {

        private long millitimestamp;
        private List<Input> inputs;
        private List<Output> outputs;
        private List<String> outecs;
        private List<String> rcds;
        private List<SigBlock> sigblocks;
        private int blockheight;

        public abstract class IO {
            private int amount;
            private String address;
            private String useraddress;
        }

        public class Input extends IO {
        }

        public class Output extends IO {
        }

        public class SigBlock {
            private List<String> signatures;
        }
    }
}
