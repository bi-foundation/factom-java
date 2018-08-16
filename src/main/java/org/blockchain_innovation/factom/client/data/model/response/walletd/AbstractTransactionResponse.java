package org.blockchain_innovation.factom.client.data.model.response.walletd;

import java.util.List;

public abstract class AbstractTransactionResponse {

    private boolean signed;
    private String name;
    private long timestamp;
    private long totalecoutputs;
    private long totalinputs;
    private long totaloutputs;

    private List<Input> inputs;
    private List<Output> outputs;
    private List<EntryCreditOutput> ecoutputs;

    public boolean isSigned() {
        return signed;
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getTotalEntryCreditOutputs() {
        return totalecoutputs;
    }

    public long getTotalInputs() {
        return totalinputs;
    }

    public long getTotalOutputs() {
        return totaloutputs;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public List<EntryCreditOutput> getEntryCreditOutputs() {
        return ecoutputs;
    }

    public abstract class IO {
        private String address;
        private long amount;

        public String getAddress() {
            return address;
        }

        public long getAmount() {
            return amount;
        }
    }

    public class Input extends IO {
    }

    public class Output extends IO {
    }

    public class EntryCreditOutput extends IO {
    }
}
