package org.blockchain_innovation.factom.client.data.model.response.walletd;

import java.util.List;

public class WalletBackupResponse {

    private String walletSeed;
    private List<AddressResponse> addresses;

    public String getWalletSeed() {
        return walletSeed;
    }

    public List<AddressResponse> getAddresses() {
        return addresses;
    }
}
