package org.blockchain_innovation.factom.client.api.model.FAT;

import org.blockchain_innovation.factom.client.api.ops.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TokenTransaction implements Serializable {
    private TokenEntry tokenEntry;
    private List<String> externalIds;

    public TokenEntry getTokenEntry() {
        return tokenEntry;
    }

    public List<String> getExternalIds(){
        return externalIds;
    }

    public TokenTransaction setTokenEntry(TokenEntry tokenEntry) {
        this.tokenEntry = tokenEntry;
        return this;
    }

    public TokenTransaction setExternalIds(List<String> externalIds){
        this.externalIds = externalIds;
        return this;
    }

    public static class Builder {
        private List<Inputs> inputs;
        private List<Outputs> outputs;
        private int milliTimestamp;
        private String salt;
        private List<String> externalIds;

        public Builder() {
            this.externalIds = new ArrayList<>();
        }

        public Builder(List<Inputs> inputs, List<Outputs> outputs, int milliTimestamp, String salt, List<String> externalIds) {
            this.inputs = inputs;
            this.outputs = outputs;
            this.milliTimestamp = milliTimestamp;
            this.salt = salt;
            this.externalIds = externalIds;
        }

        public Builder(List<Inputs> inputs, List<Outputs> outputs, int milliTimestamp, List<String> externalIds) {
            this.inputs = inputs;
            this.outputs = outputs;
            this.milliTimestamp = milliTimestamp;
            this.externalIds = externalIds;
        }

        public Builder setInputs(List<Inputs> inputs) {
            this.inputs = inputs;
            return this;
        }

        public Builder setOutputs(List<Outputs> outputs) {
            this.outputs = outputs;
            return this;
        }

        public Builder setMilliTimestamp(int milliTimestamp) {
            this.milliTimestamp = milliTimestamp;
            return this;
        }

        public Builder setSalt(String salt) {
            this.salt = salt;
            return this;
        }

        public Builder setExternalIds(List<String> externalIds) {
            this.externalIds = externalIds;
            return this;
        }

        public TokenTransaction build(){
            TokenEntry tokenEntry = new TokenEntry();
            tokenEntry.setInputs(inputs);
            tokenEntry.setOutputs(outputs);
            tokenEntry.setMilliTimestamp(milliTimestamp);
            if(StringUtils.isNotEmpty(salt)){
                tokenEntry.setSalt(salt);
            }

            TokenTransaction tokenTransaction = new TokenTransaction();
            tokenTransaction.setTokenEntry(tokenEntry);
            tokenTransaction.setExternalIds(externalIds);
            return tokenTransaction;
        }
    }
}
