package org.blockchain_innovation.factom.client.api;

import org.blockchain_innovation.factom.client.api.model.types.AddressType;

/**
 * Enum to denote whether you want to use offline (private address) signing modes or
 * online signing using walletd (public addresses).
 *
 * Offline signing does require the factom-client-offline-signing module on the classpath
 */
public enum SigningMode {
    ONLINE_WALLETD, OFFLINE;

    public static SigningMode fromAddressType(AddressType addressType) {
        return addressType.isPublic() ? ONLINE_WALLETD : OFFLINE;
    }

    public static SigningMode fromAddressValue(String address) {
        return fromAddressType(AddressType.getType(address));
    }

    public static SigningMode fromModeString(String mode) {
        if (mode != null &&
                (mode.toLowerCase().contains("on") || mode.toLowerCase().contains("walletd"))) {
            return ONLINE_WALLETD;
        }
        return OFFLINE;
    }


}
