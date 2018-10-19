package org.blockchain_innovation.factom.client.api.model.FAT;

import java.io.Serializable;

public class Outputs implements Serializable {
    private String hexAddress;
    private long amount;

    public Outputs(String hexAddress, long amount) {
        this.hexAddress = hexAddress;
        this.amount = amount;
    }

    public String getHexAddress() {
        return hexAddress;
    }

    public void setHexAddress(String hexAddress) {
        this.hexAddress = hexAddress;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
