package org.blockchain_innovation.factom.client.api.model.FAT;

import java.io.Serializable;

public class Token implements Serializable {
    private String name;
    private long amount;

    public Token(String name, long amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
