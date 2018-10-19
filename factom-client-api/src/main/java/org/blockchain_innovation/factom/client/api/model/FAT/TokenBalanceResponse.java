package org.blockchain_innovation.factom.client.api.model.FAT;

import java.io.Serializable;
import java.util.List;

public class TokenBalanceResponse implements Serializable {
    private List<Token> balance;

    public List<Token> getBalance(){
        return balance;
    }

    public void setBalance(List<Token> balance) {
        this.balance = balance;
    }
}
