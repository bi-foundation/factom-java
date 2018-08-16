package org.blockchain_innovation.factom.client.data.model;

public class Address {

    private String secret;

    public String getSecret() {
        return secret;
    }

    public Address setSecret(String secret) {
        this.secret = secret;
        return this;
    }
}
