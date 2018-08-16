package org.blockchain_innovation.factom.client.data.model.response.walletd;

public class AddressResponse {

    private String _public;
    private String secret;

    public String getPublicAddress() {
        return _public;
    }

    public String getSecret() {
        return secret;
    }
}
