package org.blockchain_innovation.factom.client.data.model.response.walletd;

import java.util.List;

public class TmpTransactions {

    private List<Transaction> transactions;

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public class Transaction {
        private String txName;
        private String txid;
        private long totalinputs;
        private long totaloutputs;
        private long totalecoutputs;

        public String getTxName() {
            return txName;
        }

        public String getTxId() {
            return txid;
        }

        public long getTotalInputs() {
            return totalinputs;
        }

        public long getTotalOutputs() {
            return totaloutputs;
        }

        public long getTotalEntryCreditOutputs() {
            return totalecoutputs;
        }
    }
}
