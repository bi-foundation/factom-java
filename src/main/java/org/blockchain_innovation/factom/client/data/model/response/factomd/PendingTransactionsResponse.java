package org.blockchain_innovation.factom.client.data.model.response.factomd;

import java.util.List;

public class PendingTransactionsResponse {

    private String transactionid;
    private String status;
    private List<Input> inputs;
    private List<String> ecoutputs;
    private int fees;

    public String getTransactionId() {
        return transactionid;
    }

    public String getStatus() {
        return status;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public List<String> getEntryCreditOutputs() {
        return ecoutputs;
    }

    public int getFees() {
        return fees;
    }

    public class IO {
        private int amount;
        private String address;
        private String useraddress;

        public int getAmount() {
            return amount;
        }

        public String getAddress() {
            return address;
        }

        public String getUserAddress() {
            return useraddress;
        }
    }

    public class Input extends IO {

    }

    public class Output extends IO {

    }
}
