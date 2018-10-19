package org.blockchain_innovation.factom.client.api.model.FAT;

import java.io.Serializable;
import java.util.List;

public class TokenEntry implements Serializable {

    private List<Inputs> inputs;
    private List<Outputs> outputs;
    private int milliTimestamp;
    private String salt;

    public TokenEntry(List<Inputs> inputs, List<Outputs> outputs, int milliTimestamp) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.milliTimestamp = milliTimestamp;
    }

    public TokenEntry(){}

    /*
     *@param salt required for coinbase transaction
     */
    public TokenEntry(List<Inputs> inputs, List<Outputs> outputs, int milliTimestamp, String salt) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.milliTimestamp = milliTimestamp;
        this.salt = salt;
    }

    public List<Inputs> getInputs() {
        return inputs;
    }

    public void setInputs(List<Inputs> inputs) {
        this.inputs = inputs;
    }

    public List<Outputs> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Outputs> outputs) {
        this.outputs = outputs;
    }

    public int getMilliTimestamp() {
        return milliTimestamp;
    }

    public void setMilliTimestamp(int milliTimestamp) {
        this.milliTimestamp = milliTimestamp;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
